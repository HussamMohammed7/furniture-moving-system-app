package com.example.senior_project2

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.bottom_sheet.*
import kotlinx.android.synthetic.main.fragment_edit__profile_frag.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private lateinit var storage: FirebaseStorage
private lateinit var database: FirebaseDatabase
private lateinit var auth: FirebaseAuth
private  var orderID : String? = null
private  var phone : String? = null
private  var condition : String? = null


private var mRef : DatabaseReference? = null
private var orderRef: DatabaseReference? = null
private var sharedPreferences : SharedPreferences? = null

class BottomSheetFragment  : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet,container,false)


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBar_bottomSheet.visibility = View.GONE
        cancel_button_bottomSheet.visibility = View.GONE
        bottom_sheet_message.visibility = View.GONE
        bottom_sheet_call.visibility = View.GONE
        bottom_sheet_date.visibility = View.GONE
        bottom_sheet_name.visibility = View.GONE
        noOrder_text.visibility = View.GONE

        storage = Firebase.storage
        auth = Firebase.auth
        database = Firebase.database


        mRef = database.reference.child("Carrier").child(auth.currentUser!!.uid).child("OrdersAccepted")


        sharedPreferences = this.requireActivity().getSharedPreferences("bottomSheet", Context.MODE_PRIVATE);
        val editor = sharedPreferences!!.edit()

        val name = sharedPreferences!!.getString("name",null)
        val date = sharedPreferences!!.getString("date",null)
        val phoneNumber =sharedPreferences!!.getString("phone",null)
        orderID = sharedPreferences!!.getString("orderID",null)

        if (name != null && date != null && phoneNumber != null && orderID!= null){
            bottom_sheet_name.text = name
            bottom_sheet_dateOfArrive.text = date
            bottom_sheet_call.text = phoneNumber
            progressBar_bottomSheet.visibility = View.GONE
            cancel_button_bottomSheet.visibility = View.VISIBLE
            bottom_sheet_message.visibility = View.VISIBLE
            bottom_sheet_call.visibility = View.VISIBLE
            bottom_sheet_date.visibility = View.VISIBLE
            bottom_sheet_name.visibility = View.VISIBLE
        }else {


            mRef?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value == null) {


                        noOrder_text.visibility = View.VISIBLE
                        progressBar_bottomSheet.visibility = View.GONE

                    } else {


                        orderID = snapshot.getValue(String::class.java)!!
                        editor.putString(
                            "orderID",
                            orderID
                        )
                        Log.e("orderID", orderID.toString())






                        orderRef = database.reference.child("ClientOrders").child(orderID!!)


                        orderRef?.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {


                                val clientID =
                                    snapshot.child("idClient").getValue(String::class.java)!!
                                database.reference.child("Clients").child(clientID).child("phone")
                                    .get()
                                    .addOnSuccessListener {

                                        phone = it.value.toString()
                                    }.addOnFailureListener {
                                        Log.e("firebase", "Error getting data", it)

                                    }

                                bottom_sheet_name.text =
                                    snapshot.child("clientName").getValue(String::class.java)!!
                                bottom_sheet_dateOfArrive.text =
                                    snapshot.child("date").getValue(String::class.java)!!
                                bottom_sheet_call.text = phone

                                editor.putString(
                                    "name",
                                    snapshot.child("clientName").getValue(String::class.java)
                                )
                                editor.putString(
                                    "date",
                                    snapshot.child("date").getValue(String::class.java)
                                )
                                editor.putString(
                                    "phone",
                                    phone
                                )

                                editor.apply()


                                progressBar_bottomSheet.visibility = View.GONE
                                cancel_button_bottomSheet.visibility = View.VISIBLE
                                bottom_sheet_message.visibility = View.VISIBLE
                                bottom_sheet_call.visibility = View.VISIBLE
                                bottom_sheet_date.visibility = View.VISIBLE
                                bottom_sheet_name.visibility = View.VISIBLE

                            }


                            override fun onCancelled(error: DatabaseError) {
                                Log.w("TAG", "loadPost:onCancelled", error.toException())
                                progressBar_bottomSheet.visibility = View.GONE

                            }


                        })


                    }
                }


                override fun onCancelled(error: DatabaseError) {
                    Log.w("TAG", "loadPost:onCancelled", error.toException())
                    progressBar_bottomSheet.visibility = View.GONE

                }


            })
        }




                cancel_button_bottomSheet.setOnClickListener{
                    val builder = AlertDialog.Builder(requireActivity())

                    builder.setTitle("Order Cancel")
                        .setMessage("Are you sure you want to cancel the order ? ")
                        .setCancelable(true)
                        .setNegativeButton("No"){dialogInterface,it ->


                        }
                        .setPositiveButton("Yes"){dialogInterface,it  ->
                            mRef?.removeValue()?.addOnSuccessListener {
                                val intent = Intent(requireActivity(), MainActivity::class.java)
                                sharedPreferences!!.edit().clear().commit()

                                startActivity(intent)
                            }

                        }
                        .show()
                  }



if (orderID != null) {
    val orderReference = database.reference.child("ClientOrders").child(orderID!!)
    orderReference?.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {

            condition = snapshot.child("condetion").getValue(String::class.java)!!


        }

        override fun onCancelled(error: DatabaseError) {
            Log.w("TAG", "loadPost:onCancelled", error.toException())

        }

    })

    if (condition == "At destination location") {
        confirm_button_bottomSheet.text = "Finish the order"
    }


    confirm_button_bottomSheet.setOnClickListener {
        if (condition == "At destination location") {
            val builder = AlertDialog.Builder(requireActivity())

            builder.setTitle("Finished Order ")
                .setMessage("Are You sure You Finished ? (Order will closed) ")
                .setCancelable(true)
                .setNegativeButton("No") { dialogInterface, it ->


                }.setPositiveButton("Yes") { dialogInterface, it ->


                    if (condition == "At destination location") {
                        orderReference!!.child("condetion").setValue("Finished")


                        val reference =
                            database.reference.child("Carrier").child(auth.currentUser!!.uid)
                                .child("OrdersHistory")
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                        val current = LocalDateTime.now().format(formatter)

                        val finished = HistoryData(orderID, condition, current.toString())

                        reference.child(orderID!!).setValue(finished)

                        mRef?.removeValue()?.addOnSuccessListener {
                            val intent = Intent(requireActivity(), MainActivity::class.java)
                            sharedPreferences!!.edit().clear().commit()

                            startActivity(intent)
                        }

                    }


                }
                .show()

        } else {
            val builder = AlertDialog.Builder(requireActivity())

            builder.setTitle("Confirm Location")
                .setMessage("Are you at the current location? ")
                .setCancelable(true)
                .setNegativeButton("No") { dialogInterface, it ->


                }
                .setPositiveButton("Yes") { dialogInterface, it ->


                    if (condition == "Accepted") {

                        orderReference!!.child("condetion").setValue("At pick location")
                        val intent = Intent(requireActivity(), MainActivity::class.java)
                        startActivity(intent)

                    }
                    if (condition == "At pick location") {
                        orderReference!!.child("condetion").setValue("At destination location")


                        val intent = Intent(requireActivity(), MainActivity::class.java)
                        startActivity(intent)


                    }


                }
                .show()
        }
    }

}


    }




 }


