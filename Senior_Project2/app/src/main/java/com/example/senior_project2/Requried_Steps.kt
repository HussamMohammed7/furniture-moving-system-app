package com.example.senior_project2

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.senior_project2.Carrier.ClientPhotos
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class Requried_Steps : AppCompatActivity() {

    private lateinit var idPhotoUri: Uri
    private lateinit var vehicleRegistrationUri: Uri
    private lateinit var profilePhotoUri: Uri
    private lateinit var storage: FirebaseStorage
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    private lateinit var progressBar: ProgressBar
    private lateinit var progressBarText: TextView

    private lateinit var idPhotoCondition : TextView
    private lateinit var vehicleRegistrationCondition : TextView
    private lateinit var profilePhotoCondition : TextView

    private lateinit var idButton: ImageButton
    private lateinit var vehicleRegistrationButton: ImageButton
    private lateinit var profilePictureButton: ImageButton

    private var mRef : DatabaseReference? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        idPhotoUri = Uri.EMPTY
        vehicleRegistrationUri = Uri.EMPTY
        profilePhotoUri = Uri.EMPTY


        storage = Firebase.storage
        auth = Firebase.auth
        database = Firebase.database

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_requried_steps)

        mRef = database.reference.child("Carrier").child(auth.currentUser!!.uid)

        mRef?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child("CarrierID").child("condition").value.toString() == "true"&&
                    snapshot.child("VehicleRegistration").child("condition").value.toString() == "true"&&
                    snapshot.child("CarrierProfilePicture").child("condition").value.toString() == "true")
                {
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)

                }
                if(
                    snapshot.child("CarrierID").child("condition").value.toString() == "null"&&
                    snapshot.child("VehicleRegistration").child("condition").value.toString() == "null"&&
                    snapshot.child("CarrierProfilePicture").child("condition").value.toString() == "null")
                {
                    return

                }
                if (snapshot.child("CarrierID").child("condition").value.toString() == "true"){
                    idPhotoCondition.setTextColor((Color.parseColor("#8de169")))
                }
                if (snapshot.child("VehicleRegistration").child("condition").value.toString() == "true"){
                    vehicleRegistrationCondition.setTextColor((Color.parseColor("#8de169")))

                }
                if (snapshot.child("CarrierProfilePicture").child("condition").value.toString() == "true"){
                    profilePhotoCondition.setTextColor((Color.parseColor("#8de169")))

                }


                idPhotoCondition.text = snapshot.child("CarrierID").child("condition").value.toString()
                vehicleRegistrationCondition.text  = snapshot.child("VehicleRegistration").child("condition").value.toString()
                profilePhotoCondition.text  =   snapshot.child("CarrierProfilePicture").child("condition").value.toString()






            }


            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "loadPost:onCancelled", error.toException())

            }

        })





         idButton = findViewById(R.id.ID_button)
         vehicleRegistrationButton = findViewById(R.id.Vehicle_Registration_button)
         profilePictureButton = findViewById(R.id.Driver_photo_button)

        idPhotoCondition = findViewById(R.id.ID_text_condition)
        vehicleRegistrationCondition  =findViewById(R.id.Vehicle_Registration_condition)
        profilePhotoCondition  = findViewById(R.id.Driver_photo_condition)

        progressBar = findViewById(R.id.progressBar_requiredSteps)
        progressBarText = findViewById(R.id.progressBar_Text_requiredSteps)

        progressBar.visibility = View.GONE
        progressBarText.visibility = View.GONE

        idButton.setOnClickListener {

            selectPhotoId()


        }
        vehicleRegistrationButton.setOnClickListener{

            selectPhotoVes ()


        }
        profilePictureButton.setOnClickListener{
            selectProfilePicture ()

        }



    }

    fun  selectPhotoId (){
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 1)

    }
    fun  selectPhotoVes (){
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 2)

    }
    fun  selectProfilePicture (){
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 3)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent? ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (idPhotoUri == Uri.EMPTY) {
            if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
                idPhotoUri = data?.data!!
                uploadPhoto(idPhotoUri,"CarrierID",idPhotoCondition,idButton)

                return

            }
        }
        if (profilePhotoUri == Uri.EMPTY) {
            if (requestCode == 3 && resultCode == RESULT_OK && data != null) {
                profilePhotoUri = data?.data!!
                uploadPhoto(profilePhotoUri,"CarrierProfilePicture",profilePhotoCondition,profilePictureButton)

                return

            }
        }
        if (vehicleRegistrationUri == Uri.EMPTY) {
            if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
                vehicleRegistrationUri = data?.data!!
                uploadPhoto(vehicleRegistrationUri,"VehicleRegistration",vehicleRegistrationCondition,vehicleRegistrationButton)
                return

            }
        }

    }




    fun uploadPhoto(uri : Uri ,typeName : String, condition :TextView , sendButton : ImageButton) {
        progressBar.visibility = View.VISIBLE
        progressBarText.visibility = View.VISIBLE
        mRef = database.reference.child("Carrier").child(auth.currentUser!!.uid).child(typeName)


        var storageRef = storage.reference.child("images").child(typeName)
            .child(System.currentTimeMillis().toString())
        storageRef.putFile(uri).addOnSuccessListener {
            it.metadata!!.reference!!.downloadUrl
            val mapImage = mapOf(
                "url" to it.toString()
            )
            condition.text = "Under the process"
            condition.setTextColor((Color.parseColor("#ffd700")))


            val photoDetails = ClientPhotos(mapImage.toString(),condition.text.toString())

            mRef!!.setValue(photoDetails).addOnCompleteListener() {
                if (it.isSuccessful) {

                    sendButton.visibility = View.GONE
                    progressBar.visibility = View.GONE
                    progressBarText.visibility = View.GONE
                    Toast.makeText(this, "Photo have uploaded Successfully", Toast.LENGTH_SHORT).show()

                } else {
                    Toast.makeText(this, "Something wrong try again later ", Toast.LENGTH_SHORT)
                        .show()

                }

            }
        }.addOnFailureListener {
                progressBar.visibility = View.GONE
                progressBarText.visibility = View.GONE
                Toast.makeText(this, "Error , try again later", Toast.LENGTH_SHORT).show()

            }.addOnProgressListener {

                progressBarText.setText("Uploading")


            }
        }
    
    }

