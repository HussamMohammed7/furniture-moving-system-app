package com.example.senior_project2

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class HistoryRepository {

    private var auth = Firebase.auth
    private var database = Firebase.database
    private var mRef : DatabaseReference? = database.reference.child("Carrier").child(auth.currentUser!!.uid).child("OrdersHistory")
    private var INSTANCE: HistoryRepository? = null

    fun getInstance(): HistoryRepository {
        return INSTANCE ?: synchronized(this) {

            val instance = HistoryRepository()
            INSTANCE = instance
            instance
        }


    }


    fun loadUsers(userList: MutableLiveData<List<HistoryData>>) {

        mRef?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {



                val _userList : List<HistoryData> = snapshot.children.map { dataSnapshot ->

                    dataSnapshot.getValue(HistoryData::class.java)!!

                }

                userList.postValue(_userList)
                Log.w("TAG", "loadPost:ondone")


            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "loadPost:onCancelled", error.toException())

            }


        })


    }
}