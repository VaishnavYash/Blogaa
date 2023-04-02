package yash.com.example.blogaa

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth


class RegistrationActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    private lateinit var mProgress: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        supportActionBar?.hide()

        val regBtn : Button = findViewById(R.id.btn_reg)
        val oldBtn : Button = findViewById(R.id.oldAccount)

        val editUserName: EditText = findViewById(R.id.editUserName)
        val editEmailName: EditText = findViewById(R.id.editUserEmail)
        val editPassword: EditText = findViewById(R.id.editUserPassword)
        val editConPass: EditText = findViewById(R.id.editConfirmPassword)

        val textUserName: TextInputLayout = findViewById(R.id.textUserName)
        val textEmailName: TextInputLayout = findViewById(R.id.textUserEmail)
        val textPassword: TextInputLayout = findViewById(R.id.textUserPassword)
        val textConPass: TextInputLayout = findViewById(R.id.textConfirmPassword)

        mProgress = ProgressDialog(this)

        mAuth = FirebaseAuth.getInstance();

        regBtn.setOnClickListener{
            if(!InputValidation.isInputEditTextFilled(editUserName, textUserName, "Enter Full Name")){
                return@setOnClickListener
            }
            if(!InputValidation.isInputEditEmailFilled(editEmailName, textEmailName, "Enter Valid Email")){
                return@setOnClickListener
            }
            if(!InputValidation.isInputEditTextFilled(editPassword, textPassword, "Set Password Here")){
                return@setOnClickListener
            }
            if(!InputValidation.isInputEditMatchFilled(editPassword, editConPass, textConPass, "Password Does not Matches")){
                return@setOnClickListener
            }

            val email: String = editEmailName.text.toString().trim()
            val pass: String = editConPass.text.toString().trim()
            val name: String = editUserName.text.toString().trim()

            if(pass.length < 6){
                Toast.makeText(this, "Password Length must be greater than 6", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            registerUser(email, pass,name)

        }

        oldBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }


    }

    private fun registerUser(email: String, password: String, name: String) {
        mProgress.setMessage("Please Wait ...")
        mProgress.show()

        mAuth?.createUserWithEmailAndPassword(email, password)
            ?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                mProgress.dismiss()

                var intent = Intent(this, MainActivity::class.java )
                intent.putExtra("userName", name)
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
}