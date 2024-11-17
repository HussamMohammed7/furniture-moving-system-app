package com.example.senior_project2_client.MainActivitys.order

import android.Manifest
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.senior_project2_client.R
import com.example.senior_project2_client.databinding.ActivityPlaceOrderMapBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.Marker
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import java.util.*

class PlaceOrderMap : AppCompatActivity(), OnMapReadyCallback , EasyPermissions.PermissionCallbacks{

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityPlaceOrderMapBinding
    private lateinit var currentLocation : Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var addressText: TextView
    private lateinit var confirmOrder : Button
    private lateinit var my_location_button: ImageButton
    private var request : String? = null

    companion object {
        const val PERMISSION_LOCATION_REQUEST_CODE = 101
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlaceOrderMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.



        request = intent.getStringExtra("request")
        Toast.makeText(this,request.toString(), Toast.LENGTH_SHORT).show()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        val backButton : ImageButton =findViewById(R.id.back_Place_order_map)
        addressText = findViewById(R.id.address_map_text)
        confirmOrder = findViewById(R.id.place_order_button)
        my_location_button = findViewById(R.id.my_location_button)

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


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val riyadh = LatLng(24.774265, 46.738586)

        val DragLocation = mMap.addMarker(
            MarkerOptions()
                .position(riyadh)
                .draggable(true)
        )
        mMap.animateCamera(CameraUpdateFactory.newLatLng(riyadh))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(riyadh,15f))



        my_location_button.setOnClickListener{
            val currentUserLocation = LatLng(currentLocation.latitude, currentLocation.longitude)

            mMap.animateCamera(CameraUpdateFactory.newLatLng(currentUserLocation))
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentUserLocation,15f))
            DragLocation!!.position = currentUserLocation
            locationDetails(DragLocation!!)

        }





        mMap.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
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
            if (Integer.parseInt(request) == 1) {
                request = "1"
            }
            if(Integer.parseInt(request) == 2){
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
            PERMISSION_LOCATION_REQUEST_CODE,
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