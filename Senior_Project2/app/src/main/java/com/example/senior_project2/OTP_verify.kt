package com.example.senior_project2

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.database.FirebaseDatabase
import java.util.concurrent.TimeUnit

class OTP_verify : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var progressBar : ProgressBar
    private lateinit var OTP: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var resendText : TextView
    private lateinit var phoneOTB : String


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_verify)

        OTP = intent.getStringExtra("OTP").toString()
        resendToken = intent.getParcelableExtra("resendToken")!!
         phoneOTB = intent.getStringExtra("phoneNumber").toString()
        val mobileText : TextView = findViewById(R.id.mobileNumber_text_OTB_Verify)
        progressBar = findViewById(R.id.progressbar_OTB_Verify)
        val verifyButton : Button = findViewById(R.id.verify_button_verifyOTB)
         resendText  = findViewById(R.id.Resend_OTP_OTB_Verify)



        mobileText.text = phoneOTB
        verifyButton.setOnClickListener(){
            setUp()
        }


        resendText.setOnClickListener {
            resendVerificationCode()
            resendOTPTvVisibility()
        }

    }

    private fun setUp(){

        val otb1 : TextView=findViewById(R.id.inputCode1_OTB_Verify)
        val otb2 : TextView=findViewById(R.id.inputCode2_OTB_Verify)
        val otb3 : TextView=findViewById(R.id.inputCode3_OTB_Verify)
        val otb4 : TextView=findViewById(R.id.inputCode4_OTB_Verify)
        val otb5 : TextView=findViewById(R.id.inputCode5_OTB_Verify)

        otb1.addTextChangedListener(object : TextWatcher
        {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if(!s.toString().trim().isEmpty()){
                    otb2.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
        otb2.addTextChangedListener(object : TextWatcher
        {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if(!s.toString().trim().isEmpty()){
                    otb3.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
        otb3.addTextChangedListener(object : TextWatcher
        {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if(!s.toString().trim().isEmpty()){
                    otb4.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
        otb4.addTextChangedListener(object : TextWatcher
        {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if(!s.toString().trim().isEmpty()){
                    otb5.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })





    }
    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                Log.d("TAG", "onVerificationFailed: ${e.toString()}")
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                Log.d("TAG", "onVerificationFailed: ${e.toString()}")
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
            OTP = verificationId
            resendToken = token
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information

                    Toast.makeText(this, "Authenticate Successfully", Toast.LENGTH_SHORT).show()
                    sendToMain()
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.d("TAG", "signInWithPhoneAuthCredential: ${task.exception.toString()}")
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
                progressBar.visibility = View.VISIBLE
            }
    }
    private fun sendToMain() {
        startActivity(Intent(this, MainActivity::class.java))
    }
    private fun resendOTPTvVisibility() {
        val otb1 : TextView=findViewById(R.id.inputCode1_OTB_Verify)
        val otb2 : TextView=findViewById(R.id.inputCode2_OTB_Verify)
        val otb3 : TextView=findViewById(R.id.inputCode3_OTB_Verify)
        val otb4 : TextView=findViewById(R.id.inputCode4_OTB_Verify)
        val otb5 : TextView=findViewById(R.id.inputCode5_OTB_Verify)

        otb1.setText("")
        otb2.setText("")
        otb3.setText("")
        otb4.setText("")
        otb5.setText("")
        resendText.visibility = View.INVISIBLE
        resendText.isEnabled = false

        Handler(Looper.myLooper()!!).postDelayed(Runnable {
            resendText.visibility = View.VISIBLE
            resendText.isEnabled = true
        }, 60000)
    }

    private fun resendVerificationCode() {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneOTB)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)
            .setForceResendingToken(resendToken)// OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
}