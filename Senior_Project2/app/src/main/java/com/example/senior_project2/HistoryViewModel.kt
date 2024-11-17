package com.example.senior_project2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HistoryViewModel : ViewModel() {



    private val repository : HistoryRepository
    private val _allUsers = MutableLiveData<List<HistoryData>>()
    val allUsers : LiveData<List<HistoryData>> = _allUsers


    init {

        repository =  HistoryRepository().getInstance()
        repository.loadUsers(_allUsers)

    }

}