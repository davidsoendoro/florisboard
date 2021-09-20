package com.kokatto.kobold.bank

import com.kokatto.kobold.api.Network
import com.kokatto.kobold.api.model.basemodel.BankModel
import com.kokatto.kobold.api.model.response.BaseResponse
import com.kokatto.kobold.api.model.response.GetBankResponse
import com.kokatto.kobold.api.model.response.GetPaginationBankResponse
import com.skydoves.sandwich.message
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.onSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class BankViewModel {
    val scope = CoroutineScope(Job() + Dispatchers.Main)


    fun getPaginated(
        page: Int = 1,
        pageSize: Int = 10,
        search: String = "",
        onLoading: (Boolean) -> Unit,
        onSuccess: (GetPaginationBankResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        scope.launch {
//            delay(5000)

            onLoading.invoke(true)
            val response = Network.bankApi.getPaginatedBank(page, pageSize, search)
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

    fun create(
        createBankRequest: BankModel,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        scope.launch {
//            delay(5000)
            val response = Network.bankApi.postCreateBank(createBankRequest)
            response.onSuccess {
                onSuccess.invoke(this.data.statusMessage)
            }.onError {
                onError.invoke(this.message())
            }.onException {
                onError.invoke(this.message ?: "Unknown Error")
            }
        }
    }

    fun findById(
        id: String,
        onSuccess: (GetBankResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        scope.launch {
//            delay(5000)
            val response = Network.bankApi.findBankById(id)
            response.onSuccess {
                onSuccess.invoke(this.data)
            }.onError {
                onError.invoke(this.message())
            }.onException {
                onError.invoke(this.message ?: "Unknown Error")
            }
        }
    }

    fun updateById(
        id: String,
        requestBody: BankModel,
        onSuccess: (BaseResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        scope.launch {
//            delay(5000)
            val response = Network.bankApi.updateBankById(id, requestBody)
            response.onSuccess {
                onSuccess.invoke(this.data)
            }.onError {
                onError.invoke(this.message())
            }.onException {
                onError.invoke(this.message ?: "Unknown Error")
            }
        }
    }

    fun deleteById(
        id: String,
        onSuccess: (BaseResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        scope.launch {
//            delay(5000)
            val response = Network.bankApi.deleteBankById(id)
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
