package com.kokatto.kobold.identity

import com.kokatto.kobold.api.Network
import com.skydoves.sandwich.message
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.onSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginViewModel {
    val scope = CoroutineScope(Job() + Dispatchers.Main)

    fun getRestaurant(
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        scope.launch {
            delay(5000)

            val response = Network.clienApi.getRestaurant()
            response.onSuccess {
                onSuccess.invoke(this.data.toString())
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
