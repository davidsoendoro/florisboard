package com.kokatto.kobold.dashboard

import android.util.Log
import com.kokatto.kobold.api.Network
import com.kokatto.kobold.api.model.request.PostOTPVerificationRequest
import com.kokatto.kobold.api.model.response.BaseResponse
import com.kokatto.kobold.api.model.response.GetTutorialPaginatedResponse
import com.kokatto.kobold.api.model.response.PostOTPVerificationResponse
import com.skydoves.sandwich.message
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.onSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class TutorialViewModel {
    val scope = CoroutineScope(Job() + Dispatchers.Main)

    fun getTutorialProgress(
        onSuccess: (GetTutorialPaginatedResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        scope.launch {
//            delay(5000)
            val response = Network.tutorialApi.getTutorialProgress()
            response.onSuccess {
                onSuccess.invoke(this.data)
            }.onError {
                onError.invoke(this.message())
            }.onException {
                onError.invoke(this.message ?: "Unknown Error")
            }
        }
    }

    fun updateTutorialProgress(
        tutorId: String,
        onSuccess: (BaseResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        scope.launch {
//            delay(5000)
            val response = Network.tutorialApi.updateTutorialProgress(tutorId)
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
