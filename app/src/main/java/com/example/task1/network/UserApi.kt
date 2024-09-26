package com.example.task1.network

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.GET

interface UserApi {
    @GET("b/6HBE")
    suspend fun validateData(): Response<JsonObject?>?

    companion object{
        fun getApi() : UserApi? {
            return ApiClient.client?.create(UserApi ::class.java)
        }
    }







}