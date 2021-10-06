package com.kokatto.kobold.registration.viewmodel

import com.kokatto.kobold.api.Network
import com.kokatto.kobold.api.model.request.PostCreateMerchantRequest
import com.kokatto.kobold.api.model.response.BaseResponse
import com.kokatto.kobold.api.model.response.GetBusinessFieldResponse
import com.kokatto.kobold.api.model.response.GetBusinessTypeResponse
import com.skydoves.sandwich.message
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.onSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MerchantViewModel {
    val scope = CoroutineScope(Job() + Dispatchers.Main)

    fun getMerchantBusinessType(
        onLoading: (Boolean) -> Unit,
        onSuccess: (GetBusinessTypeResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        scope.launch {
            onLoading.invoke(true)

            val response = Network.merchantApi.getBusinessType()
            response.onSuccess {
                onLoading.invoke(false)
                onSuccess.invoke(this.data)
            }
            response.onError {
                onLoading.invoke(false)
                onError.invoke(this.message())
            }
            response.onException {
                onLoading.invoke(false)
                onError.invoke(this.message ?: "Unknwon Error")
            }
        }
    }

    fun getMerchantBusinessField(
        onLoading: (Boolean) -> Unit,
        onSuccess: (GetBusinessFieldResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        scope.launch {
            onLoading.invoke(true)

            val response = Network.merchantApi.getBusinessField()
            response.onSuccess {
                onLoading.invoke(false)
                onSuccess.invoke(this.data)
            }
            response.onError {
                onLoading.invoke(false)
                onError.invoke(this.message())
            }
            response.onException {
                onLoading.invoke(false)
                onError.invoke(this.message ?: "Unknwon Error")
            }
        }
    }

    fun createMerchant(
        request: PostCreateMerchantRequest,
        onLoading: (Boolean) -> Unit,
        onSuccess: (BaseResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        scope.launch {
            onLoading.invoke(true)

            val response = Network.merchantApi.postCreateMerchant(request)
            response.onSuccess {
                onLoading.invoke(false)
                onSuccess.invoke(this.data)
            }
            response.onError {
                onLoading.invoke(false)
                onError.invoke(this.message())
            }
            response.onException {
                onLoading.invoke(false)
                onError.invoke(this.message ?: "Unknwon Error")
            }
        }
    }

    fun onDestroy() {
        scope.cancel()
    }
}
