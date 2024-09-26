package com.example.task1.network

import com.example.task1.common.AppConst
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    var httpIterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    var httpClient = OkHttpClient
        .Builder()
        .addInterceptor(httpIterceptor)
        .build()

    var retrofit : Retrofit? = null
    var client : Retrofit? = null
        get(){
            if (retrofit == null){
                retrofit = Retrofit.Builder()
                    .baseUrl(AppConst.BASE_URL)
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit
        }


}