package com.example.senior_project2

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class UserRepository {

    private var database = Firebase.database
    private var mRef : DatabaseReference ? = database.reference.child("ClientOrders")
    private var INSTANCE: UserRepository? = null

    fun getInstance(): UserRepository {
        return INSTANCE ?: synchronized(this) {

            val instance = UserRepository()
            INSTANCE = instance
            instance
        }


    }


    fun loadUsers(userList: MutableLiveData<List<Orders>>) {

        mRef?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {



                val _userList: MutableList<Orders> = mutableListOf()

                for (dataSnapshot in snapshot.children) {
                    val order = dataSnapshot.getValue(Orders::class.java)
                    if (order?.condetion == "New") { // check if the order has the specific string value
                        _userList.add(order)
                    }
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