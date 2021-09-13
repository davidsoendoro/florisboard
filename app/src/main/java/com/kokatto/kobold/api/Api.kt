package com.kokatto.kobold.api

import com.kokatto.kobold.api.model.GetRestaurantResponse
import com.kokatto.kobold.api.model.response.PaginatedAutoTextResponse
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

const val BaseUrl: String = "https://xnuhystuwl.execute-api.ap-southeast-1.amazonaws.com/dev/"
const val Test: String = "https://random-data-api.com/api/"

interface IdentityApi {
    @GET("restaurant/random_restaurant")
    suspend fun getRestaurant(): ApiResponse<GetRestaurantResponse>
}

interface ChatTemplateApi {
    @GET(BaseUrl + "api/v1/autotext/filter")
    suspend fun getPaginatedChatTemplateList(
        @Query("page") page: Int = 0,
        @Query("pageSize") pageSize: Int = 10
    ): ApiResponse<PaginatedAutoTextResponse>
}
