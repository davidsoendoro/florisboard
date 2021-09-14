package com.kokatto.kobold.chattemplate

import com.kokatto.kobold.api.Network
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

class ChatTemplateViewModel {
    val scope = CoroutineScope(Job() + Dispatchers.Main)

    fun getChatTemplateList(
        onSuccess: (GetPaginatedAutoTextResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        scope.launch {
//            delay(5000)
            val response = Network.chatTemplateApi.getPaginatedChatTemplateList()
            response.onSuccess {
                onSuccess.invoke(this.data)
            }.onError {
                onError.invoke(this.message())
            }.onException {
                onError.invoke(this.message?:"Unknown Error")
            }
        }
    }

    fun onDestroy() {
        scope.cancel()
    }
}
