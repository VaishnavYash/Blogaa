package yash.com.example.blogaa

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

class ResetActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    private lateinit var mProgress: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_reset)
        supportActionBar?.hide()

        var btnLogin: Button = findViewById(R.id.btn_login_back)
        var btnReset: Button = findViewById(R.id.btn_reset)

        var editResetEmail: EditText = findViewById(R.id.editResetUserEmail)
        var textOldEmail: TextInputLayout = findViewById(R.id.textResetUserEmail)

        mProgress = ProgressDialog(this)
        mAuth = FirebaseAuth.getInstance()

        btnLogin.setOnClickListener {
            startActivity(Intent(this@ResetActivity, LoginActivity::class.java))
        }

        btnReset.setOnClickListener {
            var value: String = editResetEmail.text.toString().trim()
            if (value.isEmpty()) {
                Toast.makeText(this@ResetActivity, "Please Enter Email ID", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            recoverPassword(value)
        }

    }

    private fun recoverPassword(eMail: String) {

        mProgress.setMessage("Please Wait..")
        mProgress.show()
        mAuth?.sendPasswordResetEmail(eMail)?.addOnCompleteListener { task ->
            if(task.isSuccessful){
                Toast.makeText(this@ResetActivity, "Please Check Email", Toast.LENGTH_SHORT).show()
                mProgress.dismiss()
                startActivity(Intent(this@ResetActivity, LoginActivity::class.java))
            }else{
                mProgress.dismiss()
                Toast.makeText(this@ResetActivity, "Error Occurred Try Again", Toast.LENGTH_SHORT).show()
            }
        }?.addOnFailureListener { e ->
            mProgress.dismiss()
            Toast.makeText(this@ResetActivity, "" + e, Toast.LENGTH_SHORT).show()
        }

    }

}