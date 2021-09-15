package com.kokatto.kobold.chattemplate

import com.kokatto.kobold.api.Network
import com.kokatto.kobold.api.model.basemodel.AutoTextModel
import com.kokatto.kobold.api.model.response.GetAutoTextResponse
import com.kokatto.kobold.api.model.response.GetPaginatedAutoTextResponse
import com.skydoves.sandwich.message
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.onSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

class ChatTemplateViewModel() {
    val scope = CoroutineScope(Job() + Dispatchers.Main)


    fun getChatTemplateList(
        page: Int = 1,
        pageSize: Int = 10,
        onLoading: (Boolean) -> Unit,
        onSuccess: (GetPaginatedAutoTextResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        scope.launch {
//            delay(5000)
            onLoading.invoke(true)
            val response = Network.chatTemplateApi.getPaginatedChatTemplateList(page, pageSize)
            response.onSuccess {
                onLoading.invoke(false)
                onSuccess.invoke(this.data)
            }.onError {
                onLoading.invoke(false)
                onError.invoke(this.message())
            }.onException {
                onLoading.invoke(false)
                onError.invoke(this.message ?: "Unknown Error")
            }
        }

    }

    fun createChatTemplate(
        createAutoTextRequest: AutoTextModel,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        scope.launch {
//            delay(5000)
            val response = Network.chatTemplateApi.postCreateAutoText(createAutoTextRequest)
            response.onSuccess {
                onSuccess.invoke(this.data.statusMessage)
            }.onError {
                onError.invoke(this.message())
            }.onException {
                onError.invoke(this.message ?: "Unknown Error")
            }
        }
    }

    fun findAutotextById(
        id: String,
        onSuccess: (GetAutoTextResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        scope.launch {
//            delay(5000)
            val response = Network.chatTemplateApi.findAutotextById(id)
            response.onSuccess {
                onSuccess.invoke(this.data)
            }.onError {
                onError.invoke(this.message())
            }.onException {
                onError.invoke(this.message ?: "Unknown Error")
            }
        }
    }

    fun updateAutotextById(
        id: String,
        onSuccess: (GetAutoTextResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        scope.launch {
//            delay(5000)
            val response = Network.chatTemplateApi.updateAutotextById(id)
            response.onSuccess {
                onSuccess.invoke(this.data)
            }.onError {
                onError.invoke(this.message())
            }.onException {
                onError.invoke(this.message ?: "Unknown Error")
            }
        }
    }

    fun deleteAutotextById(
        id: String,
        onSuccess: (GetAutoTextResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        scope.launch {
//            delay(5000)
            val response = Network.chatTemplateApi.deleteAutotextById(id)
            response.onSuccess {
                onSuccess.invoke(this.data)
            }.onError {
                onError.invoke(this.message())
            }.onException {
                onError.invoke(this.message ?: "Unknown Error")
            }
        }
    }

    fun onDestroy() {
        scope.cancel()
    }
}


