package com.kokatto.kobold.api

import com.kokatto.kobold.api.annotation.RequiredAuth
import com.kokatto.kobold.api.model.GetRestaurantResponse
import com.kokatto.kobold.api.model.basemodel.AutoTextModel
import com.kokatto.kobold.api.model.request.PostOTPVerificationRequest
import com.kokatto.kobold.api.model.request.PostTokenRefreshRequest
import com.kokatto.kobold.api.model.response.BaseResponse
import com.kokatto.kobold.api.model.response.GetAutoTextResponse
import com.kokatto.kobold.api.model.response.GetPaginatedAutoTextResponse
import com.kokatto.kobold.api.model.response.GetStandartListAutoTextResponse
import com.kokatto.kobold.api.model.response.PostOTPVerificationResponse
import com.kokatto.kobold.api.model.response.PostTokenRefreshResponse
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

private const val authenticationUrl: String = "api/v1/auth/"
interface AuthenticationApi {
    @GET(authenticationUrl + "request")
    suspend fun login(
        @Query("phone") phone: String,
        @Query("media") media: String
    ): ApiResponse<BaseResponse>

    @GET(authenticationUrl + "resend")
    suspend fun resendOtp(
        @Query("phone") phone: String,
        @Query("media") media: String
    ): ApiResponse<BaseResponse>

    @POST(authenticationUrl + "verification")
    suspend fun otpVerification(
        @Body request: PostOTPVerificationRequest
    ): ApiResponse<PostOTPVerificationResponse>

    @POST(authenticationUrl + "refresh-access")
    suspend fun refreshOTP(
        @Body request: PostTokenRefreshRequest
    ): ApiResponse<PostTokenRefreshResponse>
}

private const val autoTextUrl: String = "api/v1/autotext/"
interface ChatTemplateApi {
//    @RequiredAuth
    @GET(autoTextUrl + "filter")
    suspend fun getPaginatedChatTemplateList(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): ApiResponse<GetPaginatedAutoTextResponse>

//    @RequiredAuth
    @GET(autoTextUrl + "standard")
    suspend fun getStandardListChatTemplateList(
    ): ApiResponse<GetStandartListAutoTextResponse>

//    @RequiredAuth
    @POST(autoTextUrl + "create")
    suspend fun postCreateAutoText(
        @Body createAutoTextRequest: AutoTextModel
    ): ApiResponse<BaseResponse>

//    @RequiredAuth
    @GET(autoTextUrl + "detail/{id}")
    suspend fun findAutotextById(
        @Path("id") autoTextId: String
    ): ApiResponse<GetAutoTextResponse>

//    @RequiredAuth
    @PATCH(autoTextUrl + "update/{id}")
    suspend fun updateAutotextById(
    @Path("id") autoTextId: String,
    @Body updateAutoTextRequest: AutoTextModel
    ): ApiResponse<GetAutoTextResponse>

//    @RequiredAuth
    @PATCH(autoTextUrl + "delete/{id}")
    suspend fun deleteAutotextById(
        @Path("id") autoTextId: String
    ): ApiResponse<GetAutoTextResponse>
}
