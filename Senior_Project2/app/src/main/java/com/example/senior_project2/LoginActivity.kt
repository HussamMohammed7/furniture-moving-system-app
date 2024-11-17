package com.example.senior_project2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private var mRef : DatabaseReference? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth
        database = Firebase.database

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        var registerText: TextView = findViewById(R.id.create_account_login)
        registerText.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }
        var loginButton : Button = findViewById(R.id.login_button_login)
        loginButton.setOnClickListener{
            login()
        }
    }


    private fun login() {
        val email = findViewById<EditText>(R.id.Email_number_input_login)
        val password = findViewById<EditText>(R.id.password_input_login)
        if ( email.text.isEmpty() || password.text.isEmpty() ) {
            Toast.makeText(this, "Fill all the fields please", Toast.LENGTH_SHORT).show()
            return
        }
        // calling signInWithEmailAndPassword(email, pass)
        // function using Firebase auth object
        // On successful response Display a Toast

        auth.signInWithEmailAndPassword(email.text.toString(), password.text.toString())
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {

                    mRef = database.reference.child("Carrier").child(auth.currentUser!!.uid)
                    mRef?.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.child("CarrierID").child("condition").value.toString() == "true"&&
                                snapshot.child("VehicleRegistration").child("condition").value.toString() == "true"&&
                                snapshot.child("CarrierProfilePicture").child("condition").value.toString() == "true")
                            {
                                val intent = Intent(applicationContext, MainActivity::class.java)
                                Toast.makeText(applicationContext, "Successfully LoggedIn", Toast.LENGTH_SHORT).show()

                                startActivity(intent)

                            }


                        }


                        override fun onCancelled(error: DatabaseError) {
                            Log.w("TAG", "loadPost:onCancelled", error.toException())

                        }

                    })


                    val intent = Intent(this, Requried_Steps::class.java)
                    startActivity(intent)
                } else
                    Toast.makeText(this, "Log In failed ", Toast.LENGTH_SHORT).show()
            }




    }
}