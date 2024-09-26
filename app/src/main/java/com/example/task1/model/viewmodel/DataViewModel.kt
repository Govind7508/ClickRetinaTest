package com.example.task1.model.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.task1.network.BaseResponse
import com.example.task1.repository.DataRepository
import com.google.gson.JsonObject
import kotlinx.coroutines.launch

class DataViewModel: ViewModel() {
    var userRepo = DataRepository()
    val _dataResponse: MutableLiveData<BaseResponse<JsonObject>> = MutableLiveData()


    fun getMessageData() {
        _dataResponse.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                    val DataResp =  userRepo.titleAndDescrption()

                    if (DataResp?.code() == 200) {
                        _dataResponse.value =  BaseResponse.Success(DataResp.body())
                    } else {
                        _dataResponse.value = BaseResponse.Error(DataResp?.message())
                    }

            } catch (e: Exception) {
                Log.e("Aaradhya", "Error" + e)
            }
        }
    }



}