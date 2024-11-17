package com.example.senior_project2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyViewModel: ViewModel() {


    fun updateLocation(latitude: kotlin.Double, longitude: kotlin.Double, ref: DatabaseReference) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                ref.child("currentLocationLait").setValue(latitude)
                ref.child("currentLocationLong").setValue(longitude)
            }
        }
    }
}