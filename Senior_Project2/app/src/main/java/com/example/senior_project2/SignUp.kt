package com.example.senior_project2

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SignUp : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private var mRef : DatabaseReference ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        // Initialize Firebase Auth
        auth = Firebase.auth
        database = Firebase.database

        val registerButton: Button = findViewById(R.id.register_button)
        registerButton.setOnClickListener {
            signUp()

        }
        val backButton : ImageButton = findViewById(R.id.back_SignUp)

        backButton.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }


    private fun signUp() {
        //
        val fullName = findViewById<EditText>(R.id.full_name_register)
        val email = findViewById<EditText>(R.id.email_register)
        val phone = findViewById<EditText>(R.id.phone_register)
        val password = findViewById<EditText>(R.id.password_register)
        val confirmPass = findViewById<EditText>(R.id.confirm_password_register)




        if (fullName.text.isEmpty() || email.text.isEmpty() || password.text.isEmpty() || confirmPass.text.isEmpty()||phone.text.isEmpty()) {
            Toast.makeText(this, "Fill all the fields please", Toast.LENGTH_SHORT).show()
            return
        }
        if (password.text.toString().length < 7) {
            Toast.makeText(this, "Password must be 8 characters or more", Toast.LENGTH_SHORT).show()
            return
        }
        if (password.text.toString() != confirmPass.text.toString()) {
            Toast.makeText(this, "Password not the same", Toast.LENGTH_SHORT).show()
            return
        }
        if(phone.text.toString().isEmpty()){
            Toast.makeText(this, "Enter your number please", Toast.LENGTH_SHORT).show()
            return
        }
        if(!phone.text.toString().startsWith("5")){
            Toast.makeText(this, "Enter a valid number", Toast.LENGTH_SHORT).show()
            return
        }
        if(phone.text.toString().length != 9 ){
            Toast.makeText(this, "Phone number should be 9 digit", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email.text.toString(),password.text.toString()).addOnCompleteListener(this){

            if (it.isSuccessful){
                 mRef = database.reference.child("Carrier").child(auth.currentUser!!.uid)
                val clients :  CarrierUsers = CarrierUsers(auth.currentUser!!.uid , fullName.text.toString(),password.text.toString(),email.text.toString(),phone.text.toString())

                mRef!!.setValue(clients).addOnCompleteListener(){
                    if(it.isSuccessful){
                        Toast.makeText(this, "Successfully Signed Up", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, Requried_Steps::class.java)
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



}
