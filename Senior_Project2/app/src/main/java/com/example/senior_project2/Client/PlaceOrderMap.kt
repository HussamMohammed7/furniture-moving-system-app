package com.example.senior_project2.Client

import android.Manifest
import android.app.Activity
import android.app.VoiceInteractor.PickOptionRequest
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.senior_project2.Home_Frag
import com.example.senior_project2.R
import com.example.senior_project2.databinding.ActivityPlaceOrderMapBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import kotlinx.android.synthetic.main.activity_place_order_map.*
import java.lang.Integer.parseInt
import java.util.*
import kotlin.collections.ArrayList


class PlaceOrderMap : AppCompatActivity(), OnMapReadyCallback , EasyPermissions.PermissionCallbacks {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityPlaceOrderMapBinding
    private lateinit var currentLocation : Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var addressText: TextView
    private lateinit var confirmOrder : Button
    private var request : String? = null




    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)


        binding = ActivityPlaceOrderMapBinding.inflate(layoutInflater)
        setContentView(binding.root)


        request = intent.getStringExtra("request")
        Toast.makeText(this,request.toString(),Toast.LENGTH_SHORT).show()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        val backButton : ImageButton =findViewById(R.id.back_Place_order_map)
            addressText = findViewById(R.id.address_map_text)
            confirmOrder = findViewById(R.id.place_order_button)

        backButton.setBackgroundColor(resources.getColor(android.R.color.transparent)) // This to make the background of the back button transparent

        backButton.setOnClickListener{
            val intent = Intent(this, PlaceOrder::class.java)
            startActivity(intent)

        }


        if(checkPermission()) {
            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        // use your location objecty
                        // get latitude , longitude and other info from this
                        currentLocation = location
                       Toast.makeText(
                            this,
                            currentLocation.latitude.toString() + " " +currentLocation.longitude.toString(),
                            Toast.LENGTH_SHORT
                        ).show()




                    }else{
                        Toast.makeText(
                           this,
                            "location is eroro",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }else{
            requestPermission()
            SettingsDialog.Builder(this).build().show()
        }


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
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



        val riyadh = LatLng(24.774265, 46.738586)

        val DragLocation = mMap.addMarker(
            MarkerOptions()
                .position(riyadh)
                .draggable(true)
        )
        mMap.animateCamera(CameraUpdateFactory.newLatLng(riyadh))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(riyadh,15f))




        // this is for the My location button , when you press it the
        // it will zoom and pin to where you are now

        my_location_button.setOnClickListener{
            val currentUserLocation = LatLng(currentLocation.latitude, currentLocation.longitude)

            mMap.animateCamera(CameraUpdateFactory.newLatLng(currentUserLocation))
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentUserLocation,15f))
            DragLocation!!.position = currentUserLocation
            locationDetails(DragLocation!!)

        }




        mMap.setOnMarkerDragListener(object : OnMarkerDragListener {
            override fun onMarkerDragStart(marker: Marker) {}
            override fun onMarkerDrag(marker: Marker) {
                confirmOrder.background.alpha = 128
                confirmOrder.isEnabled = false
                confirmOrder.isClickable = false

            }
            override fun onMarkerDragEnd(marker: Marker) {
                confirmOrder.background.alpha = 255
                confirmOrder.isEnabled = true
                confirmOrder.isClickable = true
                locationDetails(DragLocation!!)
            }
        })



        confirmOrder.setOnClickListener{

            val intent = Intent(this,PlaceOrder::class.java);
            intent.putExtra("locationLatitude", DragLocation!!.position.latitude.toString())
            intent.putExtra("locationLongitude", DragLocation!!.position.longitude.toString())
            if (parseInt(request) == 1) {
                request = "1"
            }
            if(parseInt(request) == 2){
                request = "2"
            }
            intent.putExtra("request",request)
            startActivity(intent);
        }
    }


     private fun locationDetails(marker: Marker){

         val geocoder: Geocoder
         val addresses: List<Address>?
         geocoder = Geocoder(applicationContext, Locale.getDefault())

         addresses = geocoder.getFromLocation(
             marker.position.latitude,
             marker.position.longitude,
             1
         ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5


         val address =
             addresses!![0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

         val city = addresses!![0].locality
         val state = addresses!![0].adminArea
         val country = addresses!![0].countryName
         val postalCode = addresses!![0].postalCode
         val knownName = addresses!![0].featureName // Only if available else return NULL

         addressText.text = "$city $address"
     }











    private fun checkPermission()=
        EasyPermissions.hasPermissions(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        )
    private fun requestPermission() {
        EasyPermissions.requestPermissions(
            this,
            "The app will not work until you give permission to location",
            Home_Frag.PERMISSION_LOCATION_REQUEST_CODE,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }
    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if(EasyPermissions.somePermissionDenied(this ,perms.first())){

        }else{
            requestPermission()
        }

    }
    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this)
    }
}