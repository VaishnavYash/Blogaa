package yash.com.example.blogaa

import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.IntentSender
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    private lateinit var mProgress: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()

        mProgress = ProgressDialog(this)
        mAuth = FirebaseAuth.getInstance()


        var btnLogin : Button = findViewById(R.id.btn_log)
        var newReg : Button = findViewById(R.id.newAccount)
        var forgotPass: Button = findViewById(R.id.forgotPass)

        var editOldEmail: EditText = findViewById(R.id.editOldUserEmail)
        var editOldPass: EditText = findViewById(R.id.editOldUserPassword)

        var textOldEmail: TextInputLayout = findViewById(R.id.textOldUserEmail)
        var textOldPass: TextInputLayout = findViewById(R.id.textOldUserPassword)

        newReg?.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener {
            if(!InputValidation.isInputEditTextFilled(editOldEmail!!, textOldEmail!!, "Enter Valid Email")){
                return@setOnClickListener
            }
            if(!InputValidation.isInputEditTextFilled(editOldPass!!, textOldPass!!, "Enter Correct Password")){
                return@setOnClickListener
            }

            val email: String = editOldEmail.text.toString().trim()
            val password: String = editOldPass.text.toString().trim()

            Login(email,password)

        }

        forgotPass.setOnClickListener {
            startActivity(Intent(this@LoginActivity, ResetActivity::class.java))
        }



    }

    private fun Login(email: String, password: String) {

        mProgress.setMessage("Please Wait ...")
        mProgress.show()

        mAuth?.signInWithEmailAndPassword(email, password)
            ?.addOnCompleteListener{task ->
                if (task.isSuccessful) {
                    mProgress.dismiss()

                    var intent = Intent(this, MainActivity::class.java )
                    startActivity(intent)
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                    finish()

                } else {
                    mProgress.dismiss()
                    Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
                }
            }?.addOnFailureListener { e ->
                mProgress.dismiss()
                Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


//    override fun onStart() {
//        super.onStart()
//
//        val currentUser = auth.currentUser
//        updateUI(currentUser)
//
////        var user: FirebaseUser? = mAuth?.currentUser
////        if(user != null){
////            startActivity(Intent(this, RegistrationActivity::class.java))
////        }
//    }



}