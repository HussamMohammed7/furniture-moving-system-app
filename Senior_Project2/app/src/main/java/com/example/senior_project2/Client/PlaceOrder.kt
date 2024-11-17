package com.example.senior_project2.Client

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.senior_project2.R
import com.google.android.gms.maps.model.LatLng
import java.lang.Double.parseDouble
import java.util.*


class PlaceOrder : AppCompatActivity() {
    private lateinit var pickUpLocationText: TextView
    private lateinit var destinationLocationText : TextView
    private lateinit var choseImagesText :TextView
    private lateinit var descriptionText : EditText

    private var request : String? = null
    private var getLocationLatitudePickUp: String? = null
    private var getLocationLongitudePickUp: String? = null
    private var getLocationLatitudeDestination: String? = null
    private var getLocationLongitudeDestination: String? = null

    private var sharedPreferences : SharedPreferences? = null
    private var images : ArrayList<Uri>? =null
    private val set: MutableSet<String> = HashSet()

    private var PICK_IMAGES_CODE =0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_order)
       images = ArrayList()

        // This to save the current state of the order when you go to the location page
        sharedPreferences = getSharedPreferences("PlaceOrderDetails", Context.MODE_PRIVATE)
         val editor = sharedPreferences!!.edit()


        pickUpLocationText = findViewById(R.id.chose_pick_location_text)
        destinationLocationText = findViewById(R.id.chose_destination_location_text)



        descriptionText = findViewById(R.id.Description_Text)










        val floorNumber = resources.getStringArray(R.array.number_of_floor)
        val array = ArrayAdapter(this,R.layout.dropdown_item,floorNumber)
        val autoCompleteAdapter = findViewById<AutoCompleteTextView>(R.id.auto_complete_placeOrder)
        autoCompleteAdapter.setAdapter(array)

        val choseImagesButton :ImageButton = findViewById(R.id.Please_upload_pictures_photos)
         choseImagesText = findViewById(R.id.Please_upload_pictures_Text)



        choseImagesButton.setOnClickListener{
            pickImages()




        }

        val placeOrderButton : Button = findViewById(R.id.place_order_button)
        placeOrderButton.setOnClickListener{


            sharedPreferences!!.edit().clear().commit()





        }







        val chosePickupLocationButton : ImageButton =findViewById(R.id.chose_pick_location_button)

        chosePickupLocationButton.setOnClickListener{
            editor.putString("saveDescription",descriptionText.text.toString())
            editor.apply()
            val intent = Intent(this, PlaceOrderMap::class.java)
            intent.putExtra("request", "1")

            startActivity(intent)

        }

        val choseDestinationLocationButton : ImageButton =findViewById(R.id.chose_pick_Destination_button)
        choseDestinationLocationButton.setOnClickListener{

            editor.putString("saveDescription",descriptionText.text.toString())
            editor.apply()


            val intent = Intent(this, PlaceOrderMap::class.java)
            intent.putExtra("request", "2")


            startActivity(intent)

        }




        if( intent.extras != null) {
             request = intent.getStringExtra("request")
            Toast.makeText(this,request.toString(),Toast.LENGTH_SHORT).show()

           if(request =="1") {
               getLocationLatitudePickUp = intent.getStringExtra("locationLatitude")!!
               getLocationLongitudePickUp = intent.getStringExtra("locationLongitude")!!
               val location = LatLng(
                   parseDouble(getLocationLatitudePickUp),
                   parseDouble(getLocationLongitudePickUp)
               )
               editor.putString("LocationPickUpLati",getLocationLatitudePickUp)
               editor.putString("LocationPickUpLong",getLocationLongitudePickUp)
               editor.apply()




           }

            if (request == "2"){
                getLocationLatitudeDestination = intent.getStringExtra("locationLatitude")!!
                getLocationLongitudeDestination = intent.getStringExtra("locationLongitude")!!

                val location = LatLng(
                    parseDouble(getLocationLatitudeDestination),
                    parseDouble(getLocationLongitudeDestination)
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
            Toast.makeText(this,"no",Toast.LENGTH_SHORT).show()

            return
        }
        val saveDescription = sharedPreferences!!.getString("saveDescription",null)
        descriptionText.setText(saveDescription)







         if(request == "1") {
             if (savePickUpLocationLati != null && savePickUpLocationLong != null ) {
                 val locationPickUp = LatLng(
                     parseDouble(savePickUpLocationLati),
                     parseDouble(savePickUpLocationLong)
                 )

                 locationDetails(locationPickUp, "1")
                 if (saveDestinationLocationLati != null && saveDestinationLocationLong != null){
                     val locationDestination = LatLng(
                         parseDouble(saveDestinationLocationLati),
                         parseDouble(saveDestinationLocationLong)
                     )
                     locationDetails(locationDestination, "2")
                 }

             }
         }

        if(request == "2") {
            if (saveDestinationLocationLati != null && saveDestinationLocationLong != null) {
                val locationDestination = LatLng(
                    parseDouble(saveDestinationLocationLati),
                    parseDouble(saveDestinationLocationLong)
                )
                locationDetails(locationDestination, "2")
                if (savePickUpLocationLati != null && savePickUpLocationLong != null){
                    val locationPickUp = LatLng(
                        parseDouble(savePickUpLocationLati),
                        parseDouble(savePickUpLocationLong)
                    )
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
                        set.add(images!!.toString())


                    }


                }else{
                    val imageUri = data.data
                    images!!.add(imageUri!!)
                    set.add(images!!.toString())


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
      Toast.makeText(this,"meaw",Toast.LENGTH_LONG).show()

  }
}
