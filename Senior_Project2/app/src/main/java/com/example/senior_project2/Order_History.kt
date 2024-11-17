package com.example.senior_project2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_order_frag.*



class Order_History : Fragment() {
    private lateinit var viewModel : HistoryViewModel
    private lateinit var userRecyclerView: RecyclerView
    lateinit var adapter: HistoryAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =inflater.inflate(R.layout.fragment_order__history, container, false)
        val button = view.findViewById<Button>(R.id.order_available_order_history)
        button.setOnClickListener{
            findNavController().navigate(R.id.action_order_History_to_order_frag)
        }
        return view
    }



    override fun onStart() {
        super.onStart()

        userRecyclerView = recycle_view_order
        userRecyclerView.layoutManager = LinearLayoutManager(context)
        userRecyclerView.setHasFixedSize(true)
        adapter = HistoryAdapter()
        userRecyclerView.adapter = adapter

        viewModel = ViewModelProvider(this)[HistoryViewModel::class.java]

        viewModel.allUsers.observe(viewLifecycleOwner, Observer {

            adapter.updateUserList(it)

        })







    }

}