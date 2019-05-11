package com.example.fitday.retrofit

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface InspirationAPI {
    @GET("qod.json")
    fun getQuote(): Call<InspirationDTO>

}