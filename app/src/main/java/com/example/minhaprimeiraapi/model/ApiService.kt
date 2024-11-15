package com.example.minhaprimeiraapi.model

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @GET("car")
    suspend fun getItems(): List<Item>

    @GET("car/{id}")
    suspend fun getItem(@Path("id") id: String): Item

    @DELETE("car/{id}")
    suspend fun deleteItem(@Path("id") id: String)

    @POST("car")
    suspend fun addItem(@Body item: ItemValue): Item

    @PATCH("car/{id}")
    suspend fun updItem(@Path("id") id: String, @Body item: ItemValue): Item
}
