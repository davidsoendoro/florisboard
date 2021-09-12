package com.kokatto.kobold.api

import com.kokatto.kobold.api.model.GetRestaurantResponse
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.GET

const val Url: String = "https://www.google.com"
const val Test: String = "https://random-data-api.com/api/"

interface IdentityApi {
    @GET("restaurant/random_restaurant")
    suspend fun getRestaurant(): ApiResponse<GetRestaurantResponse>
}
