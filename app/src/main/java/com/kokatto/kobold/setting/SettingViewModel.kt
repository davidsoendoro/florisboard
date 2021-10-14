package com.kokatto.kobold.setting

import com.kokatto.kobold.api.Network
import com.kokatto.kobold.api.model.basemodel.MerchantModel
import com.skydoves.sandwich.message
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.onSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class SettingViewModel {
    val scope = CoroutineScope(Job() + Dispatchers.Main)

    fun getMerchantInfo(
        onLoading: (Boolean) -> Unit,
        onSuccess: (MerchantModel) -> Unit,
        onError: (String) -> Unit
    ) {
        scope.launch {
            onLoading.invoke(true)

            val response = Network.merchantApi.getMerchantInfo()

            response.onSuccess {
                onLoading.invoke(false)
                onSuccess.invoke(this.data.data)
            }
            response.onError {
                onLoading.invoke(false)
                onError.invoke(this.message())
            }
            response.onException {
                onLoading.invoke(false)
                onError.invoke(this.message())
            }
        }
    }

    fun onDestroy() {
        scope.cancel()
    }
}
