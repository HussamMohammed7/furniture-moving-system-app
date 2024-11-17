package com.example.senior_project2_client.MainActivitys

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.senior_project2_client.R
import com.google.android.material.bottomnavigation.BottomNavigationView

private lateinit var navController : NavController

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val nav = supportFragmentManager.findFragmentById(R.id.frame_layout) as NavHostFragment
        navController = nav.navController

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        NavigationUI.setupWithNavController(bottomNav, navController)

    }
}