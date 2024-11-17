package com.example.senior_project2_client.MainActivitys

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.senior_project2_client.MainActivitys.order.PlaceOrder
import com.example.senior_project2_client.R
import com.example.senior_project2_client.auth.OTB_Send
import com.google.android.gms.maps.SupportMapFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private lateinit var database: FirebaseDatabase
private lateinit var auth: FirebaseAuth

private var mRef : DatabaseReference? = null
private  var orderID : String? = null

class HomeFragment : Fragment() {






    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        database = Firebase.database
        mRef = database.reference.child("Clients").child(auth.currentUser!!.uid).child("Orders")


        mRef?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.getValue(String::class.java) != null) {
                    orderID = snapshot.getValue(String::class.java)
                }else{
                    orderID = null
                }


            }


            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "loadPost:onCancelled", error.toException())

            }

        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val homeOrder  =view.findViewById<TextView>(R.id.home_Order_Button)

        homeOrder.setOnClickListener{
           if( orderID == null) {
               val intent = Intent(requireActivity(), PlaceOrder::class.java)
               startActivity(intent)

           }else{
               Toast.makeText(requireActivity(),"You Already have order! ",Toast.LENGTH_SHORT).show()
           }

        }



    }

}