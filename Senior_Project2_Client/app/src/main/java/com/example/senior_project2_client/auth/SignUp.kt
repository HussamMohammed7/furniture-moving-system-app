package com.example.senior_project2_client.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.example.senior_project2_client.MainActivitys.MainActivity
import com.example.senior_project2_client.R
import com.example.senior_project2_client.dataClass.ClientData
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SignUp : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var fullName : String
    private lateinit var email : String
    private lateinit var phone : String
    private lateinit var password : String
    private lateinit var confirmPass : String
    private var mRef : DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        // Initialize Firebase Auth
        auth = Firebase.auth
        database = Firebase.database
        val fullNameEditText : TextInputEditText = findViewById(R.id.FullName_input_SignUp_EditText)
        val fullNameInputLayout : TextInputLayout = findViewById(R.id.FullName_input_SignUp_layout)

        fullNameEditText.doOnTextChanged { text, start, before, count ->
            if (text!!.isEmpty()){
                fullNameInputLayout.error = "Enter Full Name please"
            }else{
                fullNameInputLayout.error = null

            }
        }

        val emailEditText : TextInputEditText = findViewById(R.id.Email_number_input_SignUp_EditText)
        val emailInputLayout : TextInputLayout = findViewById(R.id.Email_number_input_SignUp_layout)

        emailEditText.doOnTextChanged { text, start, before, count ->
            if (text!!.isEmpty()){
                emailInputLayout.error = "Enter email please"
            }else if(!isEmailValid(text)) {
                emailInputLayout.error = "Enter a correct email please"

            }else{
                emailInputLayout.error = null

            }
        }


        val passwordEditText : TextInputEditText = findViewById(R.id.password_input_SignUp_EditText)
        val passwordInputLayout : TextInputLayout = findViewById(R.id.password_input_SignUp_layout)
        passwordEditText.doOnTextChanged { text, start, before, count ->
            if (text!!.isEmpty()){
                passwordInputLayout.error = "Enter password please"
            }else if (text!!.length < 7){
                passwordInputLayout.error = "Password must be 8 characters or more"
            }
                else{
                passwordInputLayout.error = null

            }
        }


        val CpasswordEditText : TextInputEditText = findViewById(R.id.Cpassword_input_SignUp_EditText)
        val CpasswordInputLayout : TextInputLayout = findViewById(R.id.Cpassword_input_SignUp_layout)


        CpasswordEditText.doOnTextChanged { text, start, before, count ->
            if (text!!.isEmpty()){
                CpasswordInputLayout.error = "Enter password please"
            }else if (text!!.length < 7){
                CpasswordInputLayout.error = "Password must be 8 characters or more"

            }else{
                CpasswordInputLayout.error = null
            }
        }














        val registerButton: Button = findViewById(R.id.register_button)
        registerButton.setOnClickListener {
            fullName = fullNameEditText.text.toString()
            email = emailEditText.text.toString()
            phone = intent.getStringExtra("phoneNumber").toString()
            password = passwordEditText.text.toString()
            confirmPass = CpasswordEditText.text.toString()
            signUp()

        }







    }


    private fun signUp() {
        //


        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPass.isEmpty()||phone.isEmpty()) {
            Toast.makeText(this, "Fill all the fields please", Toast.LENGTH_SHORT).show()
            return
        }
        if (password.length < 7) {
            Toast.makeText(this, "Password must be 8 characters or more", Toast.LENGTH_SHORT).show()
            return
        }
        if (password!= confirmPass) {
            Toast.makeText(this, "Password not the same", Toast.LENGTH_SHORT).show()
            return
        }
        if(!isEmailValid(email)){
            Toast.makeText(this, "Email is not correct", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this){

            if (it.isSuccessful){
                mRef = database.reference.child("Clients").child(auth.currentUser!!.uid)
                val clients : ClientData = ClientData(auth.currentUser!!.uid , fullName,password,email,phone)

                mRef!!.setValue(clients).addOnCompleteListener(){
                    if(it.isSuccessful){
                        Toast.makeText(this, "Successfully Signed Up", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }else{
                        Toast.makeText(this, "Something wrong try again later ", Toast.LENGTH_SHORT).show()

                    }

                }
            }else{
                Toast.makeText(this, "Signed Up Failed!", Toast.LENGTH_SHORT).show()

            }

        }




    }


    fun isEmailValid(email: CharSequence?): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}