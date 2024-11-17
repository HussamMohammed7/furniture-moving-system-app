package com.example.senior_project2

import android.Manifest
import android.app.Application
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.beust.klaxon.*
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.model.DirectionsResult
import com.google.maps.model.DirectionsRoute
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import kotlinx.android.synthetic.main.fragment_home_.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.*
import java.io.IOException
import java.lang.Double
import kotlin.Array
import kotlin.Exception
import kotlin.Int
import kotlin.IntArray
import kotlin.RuntimeException
import kotlin.String


var currentLocation: Location? = null
private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
private var mRef : DatabaseReference? = null
private var orderRef : DatabaseReference? = null
private  var orderID : String? = null
private lateinit var mMap: GoogleMap


private lateinit var database: FirebaseDatabase
private lateinit var auth: FirebaseAuth
private var getLocationLatitudePickUp: String? = null
private var getLocationLongitudePickUp: String? = null
private var getLocationLatitudeDestination: String? = null
private var getLocationLongitudeDestination: String? = null

  class Home_Frag : Fragment() ,EasyPermissions.PermissionCallbacks  {
    companion object {
        const val PERMISSION_LOCATION_REQUEST_CODE = 101

    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        auth = Firebase.auth
        database = Firebase.database

        mRef = database.reference.child("Carrier").child(auth.currentUser!!.uid).child("OrdersAccepted")
        GlobalScope.launch(Dispatchers.IO) {
            mRef?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.getValue(String::class.java) != null && snapshot.getValue(String::class.java) != "") {
                        orderID = snapshot.getValue(String::class.java)!!


                        orderRef = database.reference.child("ClientOrders").child(orderID!!)
                        orderRef?.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {

                                getLocationLatitudePickUp = snapshot.child("pickUpLocationLati")
                                    .getValue(String::class.java)!!
                                getLocationLongitudePickUp = snapshot.child("pickUpLocationLong")
                                    .getValue(String::class.java)!!
                                getLocationLatitudeDestination =
                                    snapshot.child("destinationLocationLati")
                                        .getValue(String::class.java)!!
                                getLocationLongitudeDestination =
                                    snapshot.child("destinationLocationLong")
                                        .getValue(String::class.java)!!


                                // add the current location
                                orderRef!!.child("currentLocationLait")
                                    .setValue(currentLocation?.latitude)
                                orderRef!!.child("currentLocationLong")
                                    .setValue(currentLocation?.longitude)



                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.w("TAG", "loadPost:onCancelled", error.toException())

                            }

                        })


                    } else {
                        orderID = null
                        getLocationLatitudePickUp = null
                        getLocationLongitudePickUp = null
                        getLocationLatitudeDestination = null
                        getLocationLongitudeDestination = null
                    }

                }


                override fun onCancelled(error: DatabaseError) {
                    Log.w("TAG", "loadPost:onCancelled", error.toException())

                }

            })

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?


    ): View? {
        // Inflate the layout for this fragment
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        return inflater.inflate(R.layout.fragment_home_, container, false)
    }




    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */


        if (getLocationLatitudePickUp != null && getLocationLongitudePickUp != null &&
            getLocationLatitudeDestination != null && getLocationLongitudeDestination != null
        ) {
            val locationPickUp = LatLng(
                Double.parseDouble(getLocationLatitudePickUp),
                Double.parseDouble(getLocationLongitudePickUp)
            )

            val locationDestination = LatLng(
                Double.parseDouble(getLocationLatitudeDestination),
                Double.parseDouble(getLocationLongitudeDestination)
            )




             val distance = drawRoute(googleMap,locationPickUp,locationDestination,"AIzaSyD0K8PrwKndXACQUzfowlK-t9RkZXzQpOY")
            home_bottom_sheet_line.setOnClickListener{
                val bottomSheetFragment = BottomSheetFragment()

                bottomSheetFragment.show(childFragmentManager,"bottomSheet")


            }
             stateOfOrder_home.text = "State of Order:"
             stateOfOrder_text_home.text ="Order Accepted"



            }else{
                if (currentLocation!= null ) {
                    val currentUserLocation =
                        LatLng(currentLocation!!.latitude, currentLocation!!.longitude)
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(currentUserLocation))
                    googleMap.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            currentUserLocation,
                            15f
                        )
                    )
                    googleMap.addMarker(
                        MarkerOptions().position(currentUserLocation).title("You are here")
                    )
                }

            }


        }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




//////////////////////////////////////////////////////////////
        if(checkPermission()) {
            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        // use your location objecty
                        // get latitude , longitude and other info from this
                        currentLocation = location
                        Toast.makeText(
                            requireActivity(),
                            currentLocation!!.latitude.toString() + " " + currentLocation!!.longitude.toString(),
                            Toast.LENGTH_SHORT
                        ).show()


                    }else{
                        Toast.makeText(
                            requireActivity(),
                            "location is eroro",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }
            ///////////////////////////////////

        }else{
            requestPermission()
            SettingsDialog.Builder(requireActivity()).build().show()
        }








    }

      override fun onStart() {
          super.onStart()
          val mapFragment =
              childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
          mapFragment?.getMapAsync(callback)
      }



      override fun onResume() {
          super.onResume()

          if (orderRef!= null) {



              val locationRequest = LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 10000)
                  .setWaitForAccurateLocation(false)

                  .build()
              val locationCallback = object : LocationCallback() {
                  override fun onLocationResult(p0: LocationResult) {
                      p0?.lastLocation?.let { location ->
                          for (location in p0.locations) {
                              val viewModel  = ViewModelProvider(requireActivity())[MyViewModel::class.java]
                              viewModel.updateLocation(location.latitude,location.longitude, orderRef!!)

                          }

                      }
                  }
              }
              if(checkPermission())
                  fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
          }


      }

      override fun onPause() {
          super.onPause()
          val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
          val locationCallback = object : LocationCallback() {
              override fun onLocationResult(p0: LocationResult) {

              }
          }
          fusedLocationClient.removeLocationUpdates(locationCallback)
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
        map.addMarker(MarkerOptions().position(origin).title("Origin").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))
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










    private fun checkPermission()=
        EasyPermissions.hasPermissions(
            requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION
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






