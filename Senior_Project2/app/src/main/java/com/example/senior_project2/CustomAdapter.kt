package com.example.senior_project2

import android.content.ContentValues.TAG
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class CustomAdapter : RecyclerView.Adapter<CustomAdapter.MyViewHolder>() {

    private val userList = ArrayList<Orders>()
    private var mRef : DatabaseReference? = null
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    class  MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val clientNameText : TextView = itemView.findViewById(R.id.customer_name_data)
        val dateOfArrive : TextView = itemView.findViewById(R.id.date_order_data)
        val viewOrder : Button = itemView.findViewById(R.id.view_location_button)
        val acceptOrder : Button = itemView.findViewById(R.id.accept_order_button)





    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.list_adapter,
            parent,false


        )

        return MyViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        auth = Firebase.auth
        database = Firebase.database
        mRef = database.reference.child("Carrier").child(auth.currentUser!!.uid).child("OrdersAccepted")
        val currentItem = userList[position]
        val orderID = currentItem.idOrder

        holder.clientNameText.text = currentItem.clientName
        holder.dateOfArrive.text = currentItem.date
        holder.viewOrder.setOnClickListener{

            Log.d(TAG, "onBindViewHolder: $orderID ")
            val intent = Intent(holder.acceptOrder.context, ViewOrder::class.java)
            intent.putExtra("orderID" , orderID)

            holder.acceptOrder.context.startActivity(intent)
        }

        holder.acceptOrder.setOnClickListener {
            var orderAccept: String? = null
            mRef?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.getValue(String::class.java) != null) {
                        orderAccept = snapshot.getValue(String::class.java)!!
                    }


                }


                override fun onCancelled(error: DatabaseError) {
                    Log.w("TAG", "loadPost:onCancelled", error.toException())

                }

            })
            if (orderAccept == null) {
                mRef!!.setValue(orderID).addOnCompleteListener() {
                    if (it.isSuccessful) {
                        val carrierRef = database.reference.child("ClientOrders").child(orderID!!)
                            .child("idCarrier")

                        val conditionRef = database.reference.child("ClientOrders").child(orderID!!)
                            .child("condetion")

                        carrierRef.setValue(auth.currentUser!!.uid)
                        conditionRef.setValue("Accepted")
                        Toast.makeText(
                            holder.acceptOrder.context,
                            "Successfully placed in client ref",
                            Toast.LENGTH_SHORT
                        ).show()

                    }

                }
            }else{
                Toast.makeText(
                    holder.acceptOrder.context,
                    "You already Accepted order ! ",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    override fun getItemCount(): Int {
        return userList.size
    }

    fun updateUserList(userList : List<Orders>){

        this.userList.clear()
        this.userList.addAll(userList)
        notifyDataSetChanged()

    }



}