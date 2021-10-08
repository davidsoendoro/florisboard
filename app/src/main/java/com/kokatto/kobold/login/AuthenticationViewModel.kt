package com.kokatto.kobold.login

import android.util.Log
import com.kokatto.kobold.api.Network
import com.kokatto.kobold.api.model.request.PostOTPVerificationRequest
import com.kokatto.kobold.api.model.response.BaseResponse
import com.kokatto.kobold.api.model.response.PostOTPVerificationResponse
import com.skydoves.sandwich.message
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.onSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AuthenticationViewModel {
    val scope = CoroutineScope(Job() + Dispatchers.Main)

    fun requestOTP(
        phone: String,
        media: String = "wa",
        onSuccess: (BaseResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        scope.launch {
//            delay(5000)
            val response = Network.authenticationApi.login(phone, media)
            response.onSuccess {
                onSuccess.invoke(this.data)
            }.onError {
                onError.invoke(this.message())
            }.onException {
                onError.invoke(this.message ?: "Unknown Error")
            }
        }
    }

    fun verifyOTP(
        request: PostOTPVerificationRequest,
        onSuccess: (PostOTPVerificationResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        scope.launch {
//            delay(5000)
            val response = Network.authenticationApi.otpVerification(request)
            response.onSuccess {
                onSuccess.invoke(this.data)
            }.onError {
                onError.invoke(this.message())
            }.onException {
                onError.invoke(this.message ?: "Unknown Error")
            }
        }
    }
}
