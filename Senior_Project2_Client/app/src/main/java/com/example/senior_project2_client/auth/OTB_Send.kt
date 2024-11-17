package com.example.senior_project2_client.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.example.senior_project2_client.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class OTB_Send : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var progressBar : ProgressBar
    private lateinit var nextButton : Button




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otb_send)
        // Initialize Firebase Auth
        auth = Firebase.auth
        database = Firebase.database

        //Set progressBar to INVISIBLE
        progressBar  = findViewById(R.id.progressbar_OTB_Send)
        progressBar.visibility= View.INVISIBLE


        // Initialize
        nextButton  = findViewById(R.id.next_button_SendOTB)
        val editText : TextInputEditText = findViewById(R.id.phone_number_SendOTB_editText)
        val inputLayout : TextInputLayout = findViewById(R.id.phone_number_SendOTB)



        // Set The checkers
        editText.doOnTextChanged { text, start, before, count ->
            if (text!!.isEmpty()){
                inputLayout.error = "Enter your phone number please"
            }
            else if (text!!.length != 9 ){
                inputLayout.error = "Phone number should be 9 digit only"
            }
            else if (text!!.startsWith("5") == false){
                inputLayout.error = "Phone number should start with 5"
            }else{
                inputLayout.error = null

            }
        }





        nextButton.setOnClickListener{
            numberCode(editText)


        }









    }


    private fun numberCode(editText : TextInputEditText ){

        if(editText.text!!.isEmpty()){
            Toast.makeText(this, "Enter your number please", Toast.LENGTH_SHORT).show()
            return
        }
        if(editText.text!!.startsWith("5") == false){
            Toast.makeText(this, "Phone number should start with 5", Toast.LENGTH_SHORT).show()
            return
        }
        if(editText.text!!.length != 9 ){
            Toast.makeText(this, "Phone number should be 9 digit only", Toast.LENGTH_SHORT).show()
            return
        }




        var phone  = editText.text.toString()



        progressBar.visibility= View.VISIBLE
        nextButton.visibility = View.INVISIBLE

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                signInWithPhoneAuthCredential(credential)
                Toast.makeText(this@OTB_Send, "failed1", Toast.LENGTH_SHORT).show()

            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Log.d("TAG", "onVerificationFailed: ${e.toString()}")
                    Toast.makeText(this@OTB_Send, "failed1", Toast.LENGTH_SHORT).show()

                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Log.d("TAG", "onVerificationFailed: ${e.toString()}")
                    Toast.makeText(this@OTB_Send, "failed2", Toast.LENGTH_SHORT).show()

                }
                progressBar.visibility = View.VISIBLE
                // Show a message and update the UI
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                // Save verification ID and resending token so we can use them later

                val intent = Intent(this@OTB_Send, OTB_Verify::class.java)
                intent.putExtra("OTP" , verificationId)
                intent.putExtra("resendToken" , token)
                intent.putExtra("phoneNumber" , phone)
                startActivity(intent)
                progressBar.visibility = View.INVISIBLE
            }
        }
        auth.useAppLanguage()
        val  options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+966$phone")       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)         // OnVerificationStateChangedCallbacks
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)

    }
















    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(this , "Authenticate Successfully" , Toast.LENGTH_SHORT).show()

                } else {
                    // Sign in failed, display a message and update the UI
                    Log.d("TAG", "signInWithPhoneAuthCredential: ${task.exception.toString()}")
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
                progressBar.visibility = View.INVISIBLE
            }
    }


}

