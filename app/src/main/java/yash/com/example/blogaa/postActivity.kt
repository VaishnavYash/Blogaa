package yash.com.example.blogaa

import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream


class postActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    var userName: String? = null
    private lateinit var titlePost : EditText
    private lateinit var descPost : EditText
    private lateinit var backBtn : Button
    private lateinit var submitBtn : Button
    private lateinit var imageBtn : ImageButton

    private lateinit var imageUri : Uri
    private var GALLARY_IMAGE_CODE = 100
    private var CAMERA_IMAGE_CODE = 200

    private lateinit var mStorage: StorageReference
    private lateinit var mProgress: ProgressDialog

    private lateinit var mDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        imageBtn = findViewById(R.id.imgBtn)
        titlePost = findViewById(R.id.title_text)
        descPost = findViewById(R.id.content_text)
        backBtn = findViewById(R.id.back_btn)
        submitBtn = findViewById(R.id.submit_btn)

        mStorage = FirebaseStorage.getInstance().reference
        mProgress = ProgressDialog(this)
        mDatabase = FirebaseDatabase.getInstance().reference.child("Posts")
        mAuth = FirebaseAuth.getInstance()

        imageBtn.setOnClickListener {
            permission()
            imagePickDialog()
        }
        backBtn.setOnClickListener {
            val intent = Intent(this@postActivity, MainActivity::class.java)
            startActivity(intent)
        }
        submitBtn.setOnClickListener {
            val title: String = titlePost.text.toString().trim()
            val desc: String = descPost.text.toString().trim()

            if(TextUtils.isEmpty(title)){
                titlePost.error = "Title is required"
            }else if(TextUtils.isEmpty(desc)){
                descPost.error = "Description is required"
            }else{
                uploadData(title, desc)
            }
        }

        var intent:Intent? = intent
        userName= intent?.getStringExtra("userName")

    }

    private fun uploadData(title: String, desc: String) {
        mProgress.setMessage("Publishing Post...")
        mProgress.show()

        val timeStamp = System.currentTimeMillis().toString()
        val filepath = "Posts/post$timeStamp"

        GlobalScope.launch(Dispatchers.IO) {
            val imageBytes = imageBtn.drawable?.toBitmap()?.let { bitmap ->
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
                baos.toByteArray()
            }
            val uri = imageBytes?.let { bytes ->
                val ref = mStorage.child(filepath)
                withContext(Dispatchers.Main) { // switch to Main thread to show progress dialog
                    ref.putBytes(bytes).await()
                }
                ref.downloadUrl.await()
            }

            val user = mAuth.currentUser
            val hashMap = hashMapOf(
                "uid" to user?.uid,
                "pEmail" to user?.email,
                "pId" to timeStamp,
                "pTitle" to title,
                "pImage" to uri.toString(),
                "pDesc" to desc,
                "pTime" to timeStamp,
                "pName" to userName
            )

            try {
                withContext(Dispatchers.IO) {
                    mDatabase.child(timeStamp).setValue(hashMap).await()
                }
                withContext(Dispatchers.Main) { // switch to Main thread to dismiss progress dialog
                    mProgress.dismiss()
                    Toast.makeText(this@postActivity, "Post Published", Toast.LENGTH_SHORT).show()
                    titlePost.setText("")
                    descPost.setText("")
                    imageBtn.setImageURI(null)
                    startActivity(Intent(this@postActivity, MainActivity::class.java))
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    mProgress.dismiss()
                    Toast.makeText(this@postActivity, e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun imagePickDialog() {
        val options = arrayOf<String>("Camera","Gallery")
//        Toast.makeText(this@postActivity, "Choose now", Toast.LENGTH_SHORT).show()
        val builder = AlertDialog.Builder(this@postActivity)
        builder.setTitle("Choose image Area")

        builder.setItems(options) { _, which ->
            if (which == 0) {
                cameraPick()
            }
            if (which == 1) {
                galleryPick()
            }
        }
        builder.show()
    }

    private fun cameraPick() {
        val contentValues: ContentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp Pick")
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp desc")

        imageUri =
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)!!
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, CAMERA_IMAGE_CODE)

    }

    private fun galleryPick() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GALLARY_IMAGE_CODE)
    }

    private fun permission() {
        Dexter.withContext(this)
            .withPermissions(
                CAMERA,
                READ_EXTERNAL_STORAGE
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        Toast.makeText(this@postActivity, "Permission Granted", Toast.LENGTH_SHORT).show()
                    } else {
                        if (report.isAnyPermissionPermanentlyDenied) {
                            Toast.makeText(this@postActivity, "Permission Denied /nTo upload the Image" +
                                    "go to Setting and Give Permissions", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(this@postActivity, "Permission Denied AGAIN /nTo upload the Image" +
                                    "go to Setting and Give Permissions", Toast.LENGTH_LONG).show()
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest?>?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }
            }).check()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == RESULT_OK){
            if(requestCode == GALLARY_IMAGE_CODE){
                imageUri = data?.data!!
                imageBtn.setImageURI(imageUri)
            }
            if(requestCode == CAMERA_IMAGE_CODE){
                imageBtn.setImageURI(imageUri)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}
