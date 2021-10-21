package com.kokatto.kobold.api

import com.kokatto.kobold.api.annotation.RequiredAuth
import com.kokatto.kobold.api.model.basemodel.AutoTextModel
import com.kokatto.kobold.api.model.basemodel.BankModel
import com.kokatto.kobold.api.model.basemodel.TransactionModel
import com.kokatto.kobold.api.model.request.PostBulkContactRequest
import com.kokatto.kobold.api.model.request.PostContactRequest
import com.kokatto.kobold.api.model.request.PostCreateMerchantRequest
import com.kokatto.kobold.api.model.request.PostOTPVerificationRequest
import com.kokatto.kobold.api.model.request.PostTokenRefreshRequest
import com.kokatto.kobold.api.model.response.BaseResponse
import com.kokatto.kobold.api.model.response.GetAutoTextResponse
import com.kokatto.kobold.api.model.response.GetBankResponse
import com.kokatto.kobold.api.model.response.GetBusinessFieldResponse
import com.kokatto.kobold.api.model.response.GetBusinessTypeResponse
import com.kokatto.kobold.api.model.response.GetContactBulkResponse
import com.kokatto.kobold.api.model.response.GetListDeliveryFeeResponse
import com.kokatto.kobold.api.model.response.GetMerchantResponse
import com.kokatto.kobold.api.model.response.GetPaginatedAutoTextResponse
import com.kokatto.kobold.api.model.response.GetPaginatedContactResponse
import com.kokatto.kobold.api.model.response.GetPaginationBankResponse
import com.kokatto.kobold.api.model.response.GetPaginationDeliveryAddressResponse
import com.kokatto.kobold.api.model.response.GetPaginationTransactionResponse
import com.kokatto.kobold.api.model.response.GetStandardPropertiesResponse
import com.kokatto.kobold.api.model.response.GetStandartListAutoTextResponse
import com.kokatto.kobold.api.model.response.GetTransactionResponse
import com.kokatto.kobold.api.model.response.GetTutorialPaginatedResponse
import com.kokatto.kobold.api.model.response.PostOTPVerificationResponse
import com.kokatto.kobold.api.model.response.PostTokenRefreshResponse
import com.skydoves.sandwich.ApiResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
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

//    @POST(authenticationUrl + "refresh-access")
//    suspend fun refresh(
//        @Body request: PostTokenRefreshRequest
//    ): ApiResponse<PostTokenRefreshResponse>
}

private const val autoTextUrl: String = "api/v1/autotext/"

interface ChatTemplateApi {
    @RequiredAuth
    @GET(autoTextUrl + "filter")
    suspend fun getPaginatedChatTemplateList(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Query("search") search: String
    ): ApiResponse<GetPaginatedAutoTextResponse>

    @RequiredAuth
    @GET(autoTextUrl + "standard")
    suspend fun getStandardListChatTemplateList(
    ): ApiResponse<GetStandartListAutoTextResponse>

    @RequiredAuth
    @POST(autoTextUrl + "create")
    suspend fun postCreateAutoText(
        @Body createAutoTextRequest: AutoTextModel
    ): ApiResponse<BaseResponse>

    @RequiredAuth
    @GET(autoTextUrl + "detail/{id}")
    suspend fun findAutotextById(
        @Path("id") autoTextId: String
    ): ApiResponse<GetAutoTextResponse>

    @RequiredAuth
    @PATCH(autoTextUrl + "update/{id}")
    suspend fun updateAutotextById(
        @Path("id") autoTextId: String,
        @Body updateAutoTextRequest: AutoTextModel
    ): ApiResponse<GetAutoTextResponse>

    @RequiredAuth
    @PATCH(autoTextUrl + "delete/{id}")
    suspend fun deleteAutotextById(
        @Path("id") autoTextId: String
    ): ApiResponse<GetAutoTextResponse>
}

private const val transactionUrl: String = "api/v1/transaction/"

interface TransactionApi {
    @RequiredAuth
    @GET(transactionUrl + "filter")
    suspend fun getPaginatedTransactionList(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Query("status") status: String,
        @Query("search") search: String,
        @Query("contact") contact: String
    ): ApiResponse<GetPaginationTransactionResponse>

    @RequiredAuth
    @POST(transactionUrl + "create")
    suspend fun postCreateTransaction(
        @Body createTransactionRequest: TransactionModel
    ): ApiResponse<BaseResponse>

    @RequiredAuth
    @GET(transactionUrl + "detail/{id}")
    suspend fun findTransactionById(
        @Path("id") transactionId: String
    ): ApiResponse<GetTransactionResponse>

    @RequiredAuth
    @PATCH(transactionUrl + "update/{id}")
    suspend fun updateTransactionById(
        @Path("id") transactionId: String,
        @Body updateTransactionRequest: TransactionModel
    ): ApiResponse<BaseResponse>

    @RequiredAuth
    @PATCH(transactionUrl + "delete/{id}")
    suspend fun deleteTransactionById(
        @Path("id") transactionId: String
    ): ApiResponse<BaseResponse>

    @RequiredAuth
    @PATCH(transactionUrl + "status/pending/{id}")
    suspend fun pendingTransactionById(
        @Path("id") transactionId: String
    ): ApiResponse<BaseResponse>

    @RequiredAuth
    @PATCH(transactionUrl + "status/paid/{id}")
    suspend fun paidTransactionById(
        @Path("id") transactionId: String
    ): ApiResponse<BaseResponse>

    @RequiredAuth
    @PATCH(transactionUrl + "status/sent/{id}")
    suspend fun sentTransactionById(
        @Path("id") transactionId: String
    ): ApiResponse<BaseResponse>

    @RequiredAuth
    @PATCH(transactionUrl + "status/cancel/{id}")
    suspend fun cancelTransactionById(
        @Path("id") transactionId: String
    ): ApiResponse<BaseResponse>

    @RequiredAuth
    @PATCH(transactionUrl + "status/complete/{id}")
    suspend fun completeTransactionById(
        @Path("id") transactionId: String
    ): ApiResponse<BaseResponse>

    @RequiredAuth
    @GET(transactionUrl + "properties/{type}")
    suspend fun getStandardListProperties(
        @Path("type") typeAsset: String
    ): ApiResponse<GetStandardPropertiesResponse>
}

private const val bankUrl: String = "api/v1/banks/"

interface BankApi {
    @RequiredAuth
    @GET(bankUrl + "filter")
    suspend fun getPaginatedBank(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Query("search") search: String
    ): ApiResponse<GetPaginationBankResponse>

    @RequiredAuth
    @POST(bankUrl + "create")
    suspend fun postCreateBank(
        @Body createBankRequest: BankModel
    ): ApiResponse<BaseResponse>

    @RequiredAuth
    @GET(bankUrl + "detail/{id}")
    suspend fun findBankById(
        @Path("id") bankId: String
    ): ApiResponse<GetBankResponse>

    @RequiredAuth
    @PATCH(bankUrl + "update/{id}")
    suspend fun updateBankById(
        @Path("id") bankId: String,
        @Body updateBankRequest: BankModel
    ): ApiResponse<BaseResponse>

    @RequiredAuth
    @PATCH(bankUrl + "delete/{id}")
    suspend fun deleteBankById(
        @Path("id") bankId: String
    ): ApiResponse<BaseResponse>

    @GET(transactionUrl + "properties/{type}")
    suspend fun getStandardListProperties(
        @Path("type") typeAsset: String
    ): ApiResponse<GetStandardPropertiesResponse>
}

private const val deliveryUrl: String = "api/v1/courier/"

interface DeliveryApi {
    @RequiredAuth
    @GET(deliveryUrl + "address")
    suspend fun getPaginatedDeliveryAddress(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Query("search") search: String
    ): ApiResponse<GetPaginationDeliveryAddressResponse>

    @RequiredAuth
    @GET(deliveryUrl + "delivery-fee")
    suspend fun getListDeliveryFee(
        @Query("from_city") fromCity: String,
        @Query("from_district") fromDistrict: String,
        @Query("from_postalcode") fromPostalcode: String,
        @Query("to_city") toCity: String,
        @Query("to_district") toDistrict: String,
        @Query("to_postalcode") toPostalcode: String,
        @Query("weight") weight: Int,
    ): ApiResponse<GetListDeliveryFeeResponse>

}

private const val merchantUrl: String = "api/v1/merchant/"

interface MerchantApi {
    @RequiredAuth
    @GET(merchantUrl + "business-type")
    suspend fun getBusinessType(): ApiResponse<GetBusinessTypeResponse>

    @RequiredAuth
    @GET(merchantUrl + "business-field")
    suspend fun getBusinessField(): ApiResponse<GetBusinessFieldResponse>

    @RequiredAuth
    @POST(merchantUrl + "create")
    suspend fun postCreateMerchant(@Body request: PostCreateMerchantRequest): ApiResponse<BaseResponse>

    @RequiredAuth
    @GET(merchantUrl + "info")
    suspend fun getMerchantInfo(): ApiResponse<GetMerchantResponse>
}

/**
 * Synchronously send the request and return its response.
 */
interface RefreshTokenApi {
    @RequiredAuth
    @POST(authenticationUrl + "refresh-access")
    fun refreshToken(
        @Body request: PostTokenRefreshRequest
    ): Call<PostTokenRefreshResponse>
}

private const val tutorialUrl: String = "api/v1/tutorial/"

interface TutorialApi {
    @GET(tutorialUrl + "progress")
    suspend fun getTutorialProgress(): ApiResponse<GetTutorialPaginatedResponse>

    @POST(tutorialUrl + "access/{tutorId}")
    suspend fun updateTutorialProgress(@Path("tutorId") tutorId: String): ApiResponse<BaseResponse>
}

private const val contactUrl: String = "api/v1/contact/"

interface ContactApi {

    @RequiredAuth
    @GET(contactUrl + "filter")
    suspend fun getPaginated(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Query("sort") sort: String,
        @Query("search") search: String
    ): ApiResponse<GetPaginatedContactResponse>

    @RequiredAuth
    @POST(contactUrl + "create")
    suspend fun postCreate(@Body request: PostContactRequest): ApiResponse<BaseResponse>

    @RequiredAuth
    @POST(contactUrl + "update/{id}")
    suspend fun postUpdate(
        @Path("id") bankId: String,
        @Body request: PostContactRequest
    ): ApiResponse<BaseResponse>

    @RequiredAuth
    @POST(contactUrl + "create/bulk")
    suspend fun postBulk(
        @Body request: List<PostBulkContactRequest>
    ): ApiResponse<GetContactBulkResponse>

}
