package com.example.senior_project2

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.bottom_order_details.*
import kotlinx.android.synthetic.main.bottom_sheet.*


private lateinit var storage: FirebaseStorage
private lateinit var database: FirebaseDatabase
private lateinit var auth: FirebaseAuth
private var mRef : DatabaseReference? = null
private  var orderID : String? = null



class BottomViewOrder : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        orderID =  arguments?.getString("orderID")
        return inflater.inflate(R.layout.bottom_order_details,container,false)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        storage = Firebase.storage
        auth = Firebase.auth
        database = Firebase.database


        mRef = database.reference.child("ClientOrders").child(orderID!!)




        mRef?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                customerName_text.text = "${snapshot.child("clientName").getValue(String::class.java)!!} Order"
                dateOfArrival_text_data.text = snapshot.child("date").getValue(String::class.java)!!
                //pick_up_floors_Text_number.text= snapshot.child("date").getValue(String::class.java)!!
                pick_up_floors_Text_condition.text = snapshot.child("switchPickUpElevator").getValue(Boolean::class.java)
                    .toString()!!
                //destination_floors_Text_number.text= snapshot.child("date").getValue(String::class.java)!!
                destination_floors_Text_condition.text = snapshot.child("switchDestinationElevator").getValue(Boolean::class.java)
                    .toString()!!
                Description_Text.text = snapshot.child("description").getValue(String::class.java)!!






            }


            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "loadPost:onCancelled", error.toException())

            }

        })




    }


}