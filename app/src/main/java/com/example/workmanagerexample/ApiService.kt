package com.example.workmanagerexample

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("/api/v1.0/random")
    suspend fun getRandomNumbers(
        @Query("min") min: Int,
        @Query("max") max: Int,
        @Query("count") count: Int
    ): List<Int>
}