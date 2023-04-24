package com.example.sqldemo.store

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class State: ViewModel() {
    private val _amount= MutableStateFlow("")
    val amount: StateFlow<String>
    get()=_amount.asStateFlow()
    //update amount
    fun updateAmount(price:String){
        _amount.value=price
    }
}