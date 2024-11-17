package com.example.senior_project2_client.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.senior_project2_client.MainActivitys.MainActivity
import com.example.senior_project2_client.MainActivitys.order.PlaceOrder
import com.example.senior_project2_client.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class Login : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var password : String
    private lateinit var email : String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        auth = Firebase.auth
        database = Firebase.database

     // When click on SignUp text
        val signUpText = findViewById<TextView>(R.id.create_account_login)

        signUpText.setOnClickListener {
            val intent = Intent(this, OTB_Send::class.java)
            startActivity(intent)
        }



        var loginButton : Button = findViewById(R.id.login_button_login)
        loginButton.setOnClickListener{
            login()
        }







    }

    private fun login() {
         email = findViewById<TextInputEditText>(R.id.Email_number_input_login_EditText).text.toString()
         password = findViewById<TextInputEditText>(R.id.password_input_login_EditText).text.toString()
        if ( email.isEmpty() || password.isEmpty() ) {
            Toast.makeText(this, "Fill all the fields please", Toast.LENGTH_SHORT).show()
            return
        }
        // calling signInWithEmailAndPassword(email, pass)
        // function using Firebase auth object
        // On successful response Display a Toast

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Successfully LoggedIn", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                } else
                    Toast.makeText(this, "Log In failed ", Toast.LENGTH_SHORT).show()
            }




    }







    }
















