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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class HistoryAdapter: RecyclerView.Adapter<HistoryAdapter.MyViewHolder>() {

    private val userList = ArrayList<HistoryData>()
    private var mRef : DatabaseReference? = null
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    class  MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val conditionText : TextView = itemView.findViewById(R.id.condition_data)
        val historyDateOfArrive : TextView = itemView.findViewById(R.id.history_order_data)





    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.list_history,
            parent,false


        )

        return MyViewHolder(itemView)

    }



    override fun getItemCount(): Int {
        return userList.size
    }

    fun updateUserList(userList : List<HistoryData>){

        this.userList.clear()
        this.userList.addAll(userList)
        notifyDataSetChanged()

    }



    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        auth = Firebase.auth
        database = Firebase.database


        val currentItem = userList[position]

        holder.conditionText.text = currentItem.condition
        holder.historyDateOfArrive.text = currentItem.date

        }
    }


