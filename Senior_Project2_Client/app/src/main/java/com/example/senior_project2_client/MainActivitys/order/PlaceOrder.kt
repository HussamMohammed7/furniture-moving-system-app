package com.example.senior_project2_client.MainActivitys.order

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.example.senior_project2_client.MainActivitys.HomeFragment
import com.example.senior_project2_client.MainActivitys.MainActivity
import com.example.senior_project2_client.R
import com.example.senior_project2_client.auth.OTB_Send
import com.example.senior_project2_client.dataClass.OrderData
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.lang.Double
import java.util.*
import kotlin.Int
import kotlin.String
import kotlin.toString

class PlaceOrder : AppCompatActivity() {

    private lateinit var pickUpLocationText: TextView
    private lateinit var destinationLocationText : TextView
    private lateinit var choseImagesText :TextView
    private lateinit var descriptionText : EditText
    private lateinit var dateText : TextView
    private lateinit var pickUpSwitch: SwitchCompat
    private lateinit var destinationSwitch: SwitchCompat
    private lateinit var autoCompleteAdapter  :AutoCompleteTextView


    private var name : String? = null



    private var request : String? = null
    private var getLocationLatitudePickUp: String? = null
    private var getLocationLongitudePickUp: String? = null
    private var getLocationLatitudeDestination: String? = null
    private var getLocationLongitudeDestination: String? = null
    private var position = -1

    private var sharedPreferences : SharedPreferences? = null
    private var images : ArrayList<Uri>? =null
    private val set: MutableSet<String> = HashSet()

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private var mRef : DatabaseReference? = null

    private var PICK_IMAGES_CODE =0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_order)

       // Initialize Firebase Auth
        auth = Firebase.auth
        database = Firebase.database
        storage = Firebase.storage

        database.reference.child("Clients").child(auth.currentUser!!.uid).child("name").get()
            .addOnSuccessListener {

                name = it.value.toString()
            }.addOnFailureListener {
                Log.e("firebase", "Error getting data", it)
                Toast.makeText(this,"Error getting data", Toast.LENGTH_LONG).show()

            }



        // This to save the current state of the order when you go to the location page
        sharedPreferences = getSharedPreferences("PlaceOrderDetails", Context.MODE_PRIVATE)
        val editor = sharedPreferences!!.edit()




        // Date button and text
        val dateButton : ImageButton = findViewById(R.id.chose_date_button)
         dateText  = findViewById(R.id.chose_date_text)

        //calender
        val c = Calendar.getInstance()
        val mYear = c.get(Calendar.YEAR)
        val mMonth = c.get(Calendar.MONTH)
        val mDay = c.get(Calendar.DAY_OF_MONTH)

        // set view to chose the calender
        dateButton.setOnClickListener{
            val dbd = DatePickerDialog(this , DatePickerDialog.OnDateSetListener{view, year, month, dayOfMonth ->
                //set text

                dateText.text = "$dayOfMonth/${month+1}/$year"

            },mYear,mMonth,mDay)
            // Disable the ability to chose from the past
            dbd.datePicker.minDate = System.currentTimeMillis() - 1000



            dbd.show()
        }

        pickUpSwitch = findViewById(R.id.switch_pickUp_location)
        destinationSwitch = findViewById(R.id.switch_delivery_location)

        pickUpSwitch.setOnClickListener{
            if (pickUpSwitch.isChecked()) {
                // When switch checked

                editor.putBoolean("savePickUpSwitch", true)
                editor.apply()
                pickUpSwitch.isChecked = true
            } else {
                // When switch unchecked

                editor.putBoolean("savePickUpSwitch", false)
                editor.apply()
                pickUpSwitch.isChecked = false
            }
        }

        destinationSwitch.setOnClickListener{
            if (destinationSwitch.isChecked()) {
                // When switch checked

                editor.putBoolean("saveDestinationSwitch", true)
                editor.apply()
                destinationSwitch.isChecked = true
            } else {
                // When switch unchecked

                editor.putBoolean("saveDestinationSwitch", false)
                editor.apply()
                destinationSwitch.isChecked = false
            }
        }



        images = ArrayList()







        pickUpLocationText = findViewById(R.id.chose_pick_location_text)
        destinationLocationText = findViewById(R.id.chose_destination_location_text)



        descriptionText = findViewById(R.id.Description_Text)











        val backButton :ImageButton = findViewById(R.id.back_Place_order)



        backButton.setOnClickListener{


            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

        }





        val choseImagesButton :ImageButton = findViewById(R.id.Please_upload_pictures_photos)
        choseImagesText = findViewById(R.id.Please_upload_pictures_Text)



        choseImagesButton.setOnClickListener{
            pickImages()




        }

        val placeOrderButton : Button = findViewById(R.id.place_order_button)
        placeOrderButton.setOnClickListener{

            uploadOrder()





        }







        val chosePickupLocationButton : ImageButton =findViewById(R.id.chose_pick_location_button)

        chosePickupLocationButton.setOnClickListener{
            //Save the Data in the sharedPreferences

            editor.putString("saveDate",dateText.text.toString())
            editor.putString("saveDescription",descriptionText.text.toString())

            editor.apply()
            val intent = Intent(this, PlaceOrderMap::class.java)
            intent.putExtra("request", "1")

            startActivity(intent)

        }

        val choseDestinationLocationButton : ImageButton =findViewById(R.id.chose_pick_Destination_button)
        choseDestinationLocationButton.setOnClickListener{
            editor.putString("saveDate",dateText.text.toString())
            editor.putString("saveDescription",descriptionText.text.toString())

            editor.apply()


            val intent = Intent(this, PlaceOrderMap::class.java)
            intent.putExtra("request", "2")


            startActivity(intent)

        }




        if( intent.extras != null) {
            request = intent.getStringExtra("request")
            //Toast.makeText(this,request.toString(),Toast.LENGTH_SHORT).show()

            if(request =="1") {
                getLocationLatitudePickUp = intent.getStringExtra("locationLatitude")!!
                getLocationLongitudePickUp = intent.getStringExtra("locationLongitude")!!
                val location = LatLng(
                    Double.parseDouble(getLocationLatitudePickUp),
                    Double.parseDouble(getLocationLongitudePickUp)
                )
                editor.putString("LocationPickUpLati",getLocationLatitudePickUp)
                editor.putString("LocationPickUpLong",getLocationLongitudePickUp)
                editor.apply()




            }

            if (request == "2"){
                getLocationLatitudeDestination = intent.getStringExtra("locationLatitude")!!
                getLocationLongitudeDestination = intent.getStringExtra("locationLongitude")!!

                val location = LatLng(
                    Double.parseDouble(getLocationLatitudeDestination),
                    Double.parseDouble(getLocationLongitudeDestination)
                )
                editor.putString("LocationDestinationLati",getLocationLatitudeDestination)
                editor.putString("LocationDestinationLong",getLocationLongitudeDestination)
                editor.apply()

            }


        }


        show()


        if(sharedPreferences!!.getStringSet("uriList", null) !=null) {
            showPictures()

        }













        }

    private fun locationDetails(latLng: LatLng , request : String){
        if( intent.extras == null) {
            return

        }



        val geocoder: Geocoder
        val addresses: List<Address>?
        geocoder = Geocoder(applicationContext, Locale.getDefault())

        addresses = geocoder.getFromLocation(
            latLng.latitude,
            latLng.longitude,
            1
        ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5


        val address =
            addresses!![0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

        val city = addresses!![0].locality
        val state = addresses!![0].adminArea
        val country = addresses!![0].countryName
        val postalCode = addresses!![0].postalCode
        val knownName = addresses!![0].featureName // Only if available else return NULL


        if(request == "1"){
            pickUpLocationText.text ="$city $address"
        }
        if(request == "2"){

            destinationLocationText.text ="$city $address"
        }

    }

    fun show (){

        val savePickUpLocationLati =  sharedPreferences!!.getString("LocationPickUpLati",null)
        val savePickUpLocationLong =   sharedPreferences!!.getString("LocationPickUpLong",null)

        val saveDestinationLocationLati =  sharedPreferences!!.getString("LocationDestinationLati",null)
        val saveDestinationLocationLong =   sharedPreferences!!.getString("LocationDestinationLong",null)

        if(savePickUpLocationLati == null && savePickUpLocationLong==null && saveDestinationLocationLati == null && saveDestinationLocationLong==null){
            //Toast.makeText(this,"no",Toast.LENGTH_SHORT).show()

            return
        }
        val saveDescription = sharedPreferences!!.getString("saveDescription",null)
        val saveDate = sharedPreferences!!.getString("saveDate",null)
        val savePickUpSwitch = sharedPreferences!!.getBoolean("savePickUpSwitch", null == true)
        val saveDestinationSwitch = sharedPreferences!!.getBoolean("saveDestinationSwitch", null == true)
        val savePickUpPosition = sharedPreferences!!.getInt("savePickUpPosition", -1)


         Toast.makeText(this,"$savePickUpPosition",Toast.LENGTH_SHORT).show()

        descriptionText.setText(saveDescription)
        dateText.setText(saveDate)
        pickUpSwitch.isChecked = savePickUpSwitch
        destinationSwitch.isChecked = saveDestinationSwitch
        if(savePickUpPosition!=-1) {
            autoCompleteAdapter.adapter.getItem(savePickUpPosition)
        }




        if(request == "1") {
            if (savePickUpLocationLati != null && savePickUpLocationLong != null ) {
                val locationPickUp = LatLng(
                    Double.parseDouble(savePickUpLocationLati),
                    Double.parseDouble(savePickUpLocationLong)
                )
                getLocationLatitudePickUp =savePickUpLocationLati
                getLocationLongitudePickUp = savePickUpLocationLong

                locationDetails(locationPickUp, "1")

                if (saveDestinationLocationLati != null && saveDestinationLocationLong != null){
                    val locationDestination = LatLng(
                        Double.parseDouble(saveDestinationLocationLati),
                        Double.parseDouble(saveDestinationLocationLong)
                    )
                    getLocationLatitudeDestination = saveDestinationLocationLati
                    getLocationLongitudeDestination =saveDestinationLocationLong
                    locationDetails(locationDestination, "2")
                }

            }
        }

        if(request == "2") {
            if (saveDestinationLocationLati != null && saveDestinationLocationLong != null) {
                val locationDestination = LatLng(
                    Double.parseDouble(saveDestinationLocationLati),
                    Double.parseDouble(saveDestinationLocationLong)
                )
                getLocationLatitudeDestination = saveDestinationLocationLati
                getLocationLongitudeDestination =saveDestinationLocationLong
                locationDetails(locationDestination, "2")

                if (savePickUpLocationLati != null && savePickUpLocationLong != null){
                    val locationPickUp = LatLng(
                        Double.parseDouble(savePickUpLocationLati),
                        Double.parseDouble(savePickUpLocationLong)
                    )
                    getLocationLatitudePickUp =savePickUpLocationLati
                    getLocationLongitudePickUp = savePickUpLocationLong
                    locationDetails(locationPickUp, "1")
                }
            }
        }









    }


    private fun pickImages (){

        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action= Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent,"Select Images"),PICK_IMAGES_CODE)



    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var editor = sharedPreferences!!.edit()
        if (requestCode == PICK_IMAGES_CODE){


            if(resultCode == Activity.RESULT_OK){

                if (data!!.clipData != null) {
                    val count = data.clipData!!.itemCount
                    for (i in 0 until count) {
                        val imageUri = data.clipData!!.getItemAt(i).uri

                        images!!.add(imageUri)
                        set.add(imageUri.toString())


                    }


                }else{
                    val imageUri = data.data
                    images!!.add(imageUri!!)
                    set.add(imageUri.toString())


                }
            }


        }



        editor.putStringSet("uriList", set)
        editor.commit()


        if(set!!.size != 0) {
            choseImagesText.text = "${set?.size} Images selected"
        }








    }






    fun showPictures (){
        val uriList = sharedPreferences!!.getStringSet("uriList", null)

        if (uriList != null) {
            set.addAll(uriList)
        }
        if(uriList!!.size != 0) {
            choseImagesText.text = "${uriList?.size} Images selected"
        }

    }

    fun uploadOrder() {


        if (dateText.text.toString().isEmpty() ||
            getLocationLatitudePickUp!!.isEmpty() ||
            getLocationLongitudePickUp!!.isEmpty() ||
            getLocationLatitudeDestination!!.isEmpty() ||
            getLocationLongitudeDestination!!.isEmpty()
        ) {
            Toast.makeText(this, "fill all the details please", Toast.LENGTH_LONG).show()

            return

        }


   if (name == null){
       Toast.makeText(this,"Name is empty ", Toast.LENGTH_LONG).show()
       return
   }








        mRef = database.reference.child("ClientOrders").child("orderID")
        val idOrder = mRef!!.push().key


        mRef = database.reference.child("ClientOrders").child(idOrder.toString())

        val refIdOrder = database.reference.child("Clients").child(auth.currentUser!!.uid).child("Orders")
        refIdOrder.setValue(idOrder.toString())



        for (i in set) {
            val uri = Uri.parse(i)

            var storageRef = storage.reference.child("images").child("orderID").child(idOrder.toString()).child(uri.lastPathSegment.toString())


            storageRef.putFile(uri).addOnSuccessListener {

                Toast.makeText(this, "ok", Toast.LENGTH_LONG).show()





            }.addOnFailureListener {

                Toast.makeText(this, "Error , try again later", Toast.LENGTH_SHORT).show()

            }.addOnProgressListener {
                Toast.makeText(this, "wait ${i.count()}", Toast.LENGTH_LONG).show()


            }
        }





        val OrderData = OrderData(auth.currentUser!!.uid,idOrder.toString() , name,dateText.text.toString(),getLocationLatitudePickUp
            ,getLocationLongitudePickUp,getLocationLatitudeDestination,getLocationLongitudeDestination,
            pickUpSwitch.isChecked,destinationSwitch.isChecked,descriptionText.text.toString(),"New")

        mRef!!.setValue(OrderData).addOnCompleteListener(){
            if(it.isSuccessful){
                Toast.makeText(this, "Order Uploaded !", Toast.LENGTH_SHORT).show()
                val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                Toast.makeText(this, "Something wrong try again later ", Toast.LENGTH_SHORT).show()

            }

        }


        sharedPreferences!!.edit().clear().commit()

    }



    }





