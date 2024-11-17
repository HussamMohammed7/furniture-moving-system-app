package com.example.senior_project2_client.MainActivitys

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.senior_project2_client.R
import com.example.senior_project2_client.dataClass.HistoryData
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.lang.Double
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.Int
import kotlin.String
import kotlin.toString


private var carrierLocationLatitude: String? = null
private var carrierLocationLongitude: String? = null
private var orderID : String? = null
private var condition : String? = null
private var phone : String? = null
private var carrierID : String? = null


private lateinit var database: FirebaseDatabase
private lateinit var auth: FirebaseAuth

private var mRef : DatabaseReference? = null
private var orderRef : DatabaseReference? = null
private var pinLocation: Marker? = null


class Orders : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        database = Firebase.database






        mRef = database.reference.child("Clients").child(auth.currentUser!!.uid).child("Orders")
        mRef?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.getValue(String::class.java) != null) {
                    orderID = snapshot.getValue(String::class.java)

                }


            }


            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "loadPost:onCancelled", error.toException())

            }

        })
/////////////////////////////////////////
        if (carrierID!= null) {
            val carrierRef = database.reference.child("Carrier").child(carrierID!!).child("phone")
            carrierRef?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                        phone = snapshot.getValue(String::class.java)



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
        return inflater.inflate(R.layout.fragment_orders, container, false)
    }


    private val callback =
        OnMapReadyCallback { googleMap ->

            if (orderID != null) {



                orderRef = database.reference.child("ClientOrders").child(orderID!!)


                orderRef?.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

            if (snapshot.child("condetion").getValue(String::class.java) != "New") {
                if (snapshot.child("currentLocationLait").getValue(Double::class.java) != null
                    && snapshot.child("currentLocationLong").getValue(Double::class.java)!=null
                ) {
                    carrierLocationLatitude =
                        snapshot.child("currentLocationLait").getValue(Double::class.java)
                            .toString()!!
                    carrierLocationLongitude =
                        snapshot.child("currentLocationLong").getValue(Double::class.java)
                            .toString()!!
                    condition=
                        snapshot.child("condetion").getValue(String::class.java)
                    carrierID=
                        snapshot.child("idCarrier").getValue(String::class.java)

                    val carrierLocation = LatLng(
                        Double.parseDouble(carrierLocationLatitude),
                        Double.parseDouble(carrierLocationLongitude)
                    )


                    if (pinLocation == null) {
                        pinLocation = googleMap.addMarker(
                            MarkerOptions()
                                .position(carrierLocation)
                                .title("Driver location")
                                .icon(bitMapFromVector(R.drawable.ic_baseline_directions_car_24))
                        )
                    } else {
                        // If the marker already exists, update its position
                        pinLocation!!.position = carrierLocation
                    }




                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(pinLocation!!.position))
                    googleMap.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            pinLocation!!.position,
                            17f
                        )
                    )

                }
            }

                    }


                    override fun onCancelled(error: DatabaseError) {
                        Log.w("TAG", "loadPost:onCancelled", error.toException())

                    }

                })
            }


        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val textCancel  =view.findViewById<TextView>(R.id.bottom_sheet_Cancel)
        val card = view.findViewById<CardView>(R.id.card_details_order)
        val conditionText = view.findViewById<TextView>(R.id.Order_case_text_data)
        val message = view.findViewById<TextView>(R.id.bottom_sheet_message)
        val call = view.findViewById<TextView>(R.id.bottom_sheet_call)

        if(phone == null){
            message.visibility = View.GONE
            call.visibility = View.GONE
        }

        if (orderID == null){
            card.visibility = View.GONE

        }else{
            card.visibility = View.VISIBLE
            val mapFragment =
                childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
            mapFragment?.getMapAsync(callback)
            if (condition!=null) {
                conditionText.text = condition
            }
            if (condition == "Finished"||condition == "Cancelled"){
                textCancel.text="Finish Order"

            }
        }

        textCancel.setOnClickListener{
            val builder = AlertDialog.Builder(requireActivity())
            if (condition == "Finished"){
                builder.setTitle("Order Finish")
                    .setMessage("Finish the order ? ")
                    .setCancelable(true)
                    .setNegativeButton("No"){dialogInterface,it ->



                    }
                    .setPositiveButton("Yes"){dialogInterface,it  ->

                        mRef?.removeValue()!!.addOnSuccessListener {
                            findNavController()
                                .navigate(R.id.homeFragment,
                                    null,
                                    NavOptions.Builder()
                                        .setPopUpTo(R.id.orders, true)
                                        .build()
                                )

                            orderID = null
                        }

                    }
                    .show()

            }else{



            builder.setTitle("Order Cancel")
                .setMessage("Are you sure you want to cancel the order ? ")
                .setCancelable(true)
                .setNegativeButton("No"){dialogInterface,it ->



                }
                .setPositiveButton("Yes"){dialogInterface,it  ->

                    mRef?.removeValue()!!.addOnSuccessListener {
                        findNavController()
                            .navigate(R.id.homeFragment,
                                null,
                                NavOptions.Builder()
                                    .setPopUpTo(R.id.orders, true)
                                    .build()
                            )
                        val conditionRef = database.reference.child("ClientOrders").child(orderID!!).child("condetion")
                        val carrierRef = database.reference.child("Carrier").child(carrierID!!).child("OrdersAccepted")
                        val reference =
                            database.reference.child("Carrier").child(carrierID!!)
                                .child("OrdersHistory")
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                        val current = LocalDateTime.now().format(formatter)

                        val finished = HistoryData(orderID, "Cancelled", current.toString())

                        reference.child(orderID!!).setValue(finished)
                        carrierRef.removeValue()!!

                        conditionRef.setValue("Cancelled")
                        orderID = null
                    }

                }
                .show()
            }
        }

        message.setOnClickListener{
        if (phone!= null){
            message.visibility = View.VISIBLE
            call.visibility = View.VISIBLE
            val url = "https://api.whatsapp.com/send?phone=$phone"
            try {
                val pm: PackageManager = requireActivity().getPackageManager()
                pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                requireActivity().startActivity(i)
            } catch (e: PackageManager.NameNotFoundException) {
                requireActivity().startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            }
        }
        }
        call.setOnClickListener{
            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse("tel:$phone")
            startActivity(dialIntent)
        }

    }


    private fun bitMapFromVector(vectorResID: Int): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(requireActivity()!!, vectorResID)
        vectorDrawable!!.setBounds(
            0,
            0,
            vectorDrawable!!.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )
        val bitmap =
            Bitmap.createBitmap(
                vectorDrawable.intrinsicWidth,
                vectorDrawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)


    }


    override fun onStart() {
        super.onStart()
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

}