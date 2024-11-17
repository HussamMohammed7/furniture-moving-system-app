package com.example.senior_project2_client.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.senior_project2_client.R
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class OTB_Verify : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var progressBar : ProgressBar
    private lateinit var OTP: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var resendText : TextView
    private lateinit var phoneOTB : String
    private lateinit var otb1 : TextView
    private lateinit var otb2 : TextView
    private lateinit var otb3 : TextView
    private lateinit var otb4 : TextView
    private lateinit var otb5 : TextView
    private lateinit var otb6 : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otb_verify)
        // Initialize Firebase Auth
        auth = Firebase.auth
        database = Firebase.database

       //Set progressBar to INVISIBLE
        progressBar  = findViewById(R.id.progressbar_OTB_Verify)
        progressBar.visibility= View.INVISIBLE


        otb1 =findViewById(R.id.inputCode1_OTB_Verify)
        otb2 =findViewById(R.id.inputCode2_OTB_Verify)
        otb3 =findViewById(R.id.inputCode3_OTB_Verify)
        otb4 =findViewById(R.id.inputCode4_OTB_Verify)
        otb5 =findViewById(R.id.inputCode5_OTB_Verify)
        otb6 =findViewById(R.id.inputCode6_OTB_Verify)



        OTP = intent.getStringExtra("OTP").toString()
        resendToken = intent.getParcelableExtra("resendToken")!!
        phoneOTB = intent.getStringExtra("phoneNumber").toString()

        val mobileText: TextView = findViewById(R.id.mobileNumber_text_OTB_Verify)
        mobileText.text = "+966$phoneOTB"


        resendText = findViewById(R.id.Resend_OTP_OTB_Verify)

        addTextChangeListener()
        resendOTPTvVisibility()
        val verifyButton: Button = findViewById(R.id.verify_button_verifyOTB)

        verifyButton.setOnClickListener() {
            //collect otp from all the edit texts
            val typedOTP =
                (otb1.text.toString() + otb2.text.toString() + otb3.text.toString()
                        + otb4.text.toString() + otb5.text.toString()+ otb6.text.toString())

            if (typedOTP.isNotEmpty()) {
                if (typedOTP.length == 6) {
                    val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(
                        OTP, typedOTP
                    )
                    progressBar.visibility = View.VISIBLE
                    signInWithPhoneAuthCredential(credential)
                } else {
                    Toast.makeText(this, "Please Enter Correct OTP", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please Enter OTP", Toast.LENGTH_SHORT).show()
            }
        }


        resendText.setOnClickListener {
            resendVerificationCode()
            resendOTPTvVisibility()
        }
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
                        val intent = Intent(this, SignUp::class.java)
                        intent.putExtra("phoneNumber" , phoneOTB)

                        startActivity(intent)
                    } else {
                        // Sign in failed, display a message and update the UI
                        Log.d("TAG", "signInWithPhoneAuthCredential: ${task.exception.toString()}")
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                            Toast.makeText(this,"NOT THE SAME",Toast.LENGTH_SHORT).show()
                        }
                        // Update UI
                    }
                    progressBar.visibility = View.VISIBLE
                }
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






    private fun addTextChangeListener() {
        otb1.addTextChangedListener(EditTextWatcher(otb1))
        otb2.addTextChangedListener(EditTextWatcher(otb2))
        otb3.addTextChangedListener(EditTextWatcher(otb3))
        otb4.addTextChangedListener(EditTextWatcher(otb4))
        otb5.addTextChangedListener(EditTextWatcher(otb5))
    }

    inner class EditTextWatcher(private val view: View) : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(p0: Editable?) {

            val text = p0.toString()
            when (view.id) {
                R.id.inputCode1_OTB_Verify -> if (text.length == 1) otb2.requestFocus()
                R.id.inputCode2_OTB_Verify -> if (text.length == 1) otb3.requestFocus() else if (text.isEmpty()) otb1.requestFocus()
                R.id.inputCode3_OTB_Verify -> if (text.length == 1) otb4.requestFocus() else if (text.isEmpty()) otb2.requestFocus()
                R.id.inputCode4_OTB_Verify -> if (text.length == 1) otb5.requestFocus() else if (text.isEmpty()) otb3.requestFocus()
                R.id.inputCode5_OTB_Verify -> if (text.length == 1) otb6.requestFocus() else if (text.isEmpty()) otb4.requestFocus()
                R.id.inputCode6_OTB_Verify -> if (text.isEmpty()) otb5.requestFocus()


            }
        }

    }

    }
