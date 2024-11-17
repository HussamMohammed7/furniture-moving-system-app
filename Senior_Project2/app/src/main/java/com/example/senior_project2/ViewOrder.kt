package com.example.senior_project2

import android.app.Application
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.example.senior_project2.databinding.ActivityViewOrderBinding
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.model.DirectionsResult
import com.google.maps.model.DirectionsRoute
import kotlinx.android.synthetic.main.activity_view_order.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.lang.Double
import java.util.ArrayList
import java.util.HashSet

class ViewOrder : AppCompatActivity(), OnMapReadyCallback {

    private var getLocationLatitudePickUp: String? = null
    private var getLocationLongitudePickUp: String? = null
    private var getLocationLatitudeDestination: String? = null
    private var getLocationLongitudeDestination: String? = null
    private  var orderID : String? = null



    // Database
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private var mRef : DatabaseReference? = null


    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityViewOrderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        storage = Firebase.storage
        auth = Firebase.auth
        database = Firebase.database


        binding = ActivityViewOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
         orderID = intent.getStringExtra("orderID").toString()


        Toast.makeText(this,orderID,Toast.LENGTH_SHORT).show()



        mRef = database.reference.child("ClientOrders").child(orderID!!)
        mRef?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {



                            getLocationLatitudePickUp = snapshot.child("pickUpLocationLati").getValue(String::class.java)!!
                            getLocationLongitudePickUp= snapshot.child("pickUpLocationLong").getValue(String::class.java)!!
                            getLocationLatitudeDestination = snapshot.child("destinationLocationLati").getValue(String::class.java)!!
                            getLocationLongitudeDestination = snapshot.child("destinationLocationLong").getValue(String::class.java)!!
                val mapFragment = supportFragmentManager
                    .findFragmentById(R.id.map) as SupportMapFragment
                mapFragment.getMapAsync(this@ViewOrder)

                        }











            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "loadPost:onCancelled", error.toException())

            }

        })


        val bottomSheetFragment = BottomViewOrder()
        val bundle = Bundle()
        bundle.putString("orderID", orderID)
        bottomSheetFragment.arguments = bundle

        viewOrder_bottom_sheet_line.setOnClickListener{

            bottomSheetFragment.show(supportFragmentManager,"bottomSheet")

        }
            back_orderDetails.setBackgroundColor(resources.getColor(android.R.color.transparent))
              back_orderDetails.setOnClickListener{
                  val intent = Intent(applicationContext, MainActivity::class.java)
                  startActivity(intent)
              }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


        val locationPickUp = LatLng(
            Double.parseDouble(getLocationLatitudePickUp),
            Double.parseDouble(getLocationLongitudePickUp)
        )

        val locationDestination = LatLng(
            Double.parseDouble(getLocationLatitudeDestination),
            Double.parseDouble(getLocationLongitudeDestination)
        )




        val distance = drawRoute(googleMap,locationPickUp,locationDestination,"AIzaSyD0K8PrwKndXACQUzfowlK-t9RkZXzQpOY")

       swipeUpViewOrder.text = "Distance is $distance"





    }


    private fun drawRoute(map: GoogleMap, origin: LatLng, destination: LatLng, apiKey: String): String {
        val geoApiContext = try {
            GeoApiContext.Builder()
                .apiKey(apiKey)
                .build()
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException("Error initializing GeoApiContext: " + e.message)
        }

        val directionsResult: DirectionsResult = try {
            DirectionsApi.newRequest(geoApiContext)
                .origin(origin.latitude.toString() + "," + origin.longitude.toString())
                .destination(destination.latitude.toString() + "," + destination.longitude.toString())
                .await()
        } catch (e: ApiException) {
            e.printStackTrace()
            throw RuntimeException("Directions API error: " + e.message)
        } catch (e: InterruptedException) {
            e.printStackTrace()
            throw RuntimeException("Directions API error: " + e.message)
        } catch (e: IOException) {
            e.printStackTrace()
            throw RuntimeException("Directions API error: " + e.message)
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException("Error calling Directions API: " + e.message)
        }

        if (directionsResult.routes.isEmpty()) {
            throw RuntimeException("No routes found")
        }

        val directionsRoute: DirectionsRoute = directionsResult.routes[0]
        val decodedPath = directionsRoute.overviewPolyline.decodePath()

        val polylineOptions = PolylineOptions()
        polylineOptions.color(Color.RED)
        polylineOptions.width(12f)

        val convertedPath = mutableListOf<LatLng>()
        for (latLng in decodedPath) {
            val convertedLatLng = LatLng(latLng.lat, latLng.lng)
            polylineOptions.add(convertedLatLng)
            convertedPath.add(convertedLatLng)
        }

        // Add the polyline to the map
        map.addPolyline(polylineOptions)

        // Add markers for the origin and destination
        map.addMarker(MarkerOptions().position(origin).title("Origin").icon(
            BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_GREEN)))
        map.addMarker(MarkerOptions().position(destination).title("Destination"))

        // Zoom in on the route
        val builder = LatLngBounds.builder()
        builder.include(origin)
        builder.include(destination)
        val bounds = builder.build()
        val padding = 100 // padding in pixels
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))

        // Return the distance between the origin and destination
        return directionsRoute.legs[0].distance.humanReadable
    }
}