package com.example.task1.repository

import android.util.Log
import com.example.task1.network.UserApi
import com.google.gson.JsonObject
import retrofit2.Response

class DataRepository {
    suspend fun titleAndDescrption() : Response<JsonObject?>? {
        val response = UserApi.getApi()?.validateData()
        Log.e(  "Aaradhya","Response code: ${response?.code()}")
        Log.e(  "Aaradhya", "Response body: ${response?.body()}")
        return  response
    }
}