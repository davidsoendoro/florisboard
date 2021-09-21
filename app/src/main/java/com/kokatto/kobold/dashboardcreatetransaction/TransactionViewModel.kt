package com.kokatto.kobold.dashboardcreatetransaction

import com.kokatto.kobold.api.Network
import com.kokatto.kobold.api.model.basemodel.TransactionModel
import com.kokatto.kobold.api.model.response.BaseResponse
import com.kokatto.kobold.api.model.response.GetPaginationTransactionResponse
import com.kokatto.kobold.api.model.response.GetTransactionResponse
import com.skydoves.sandwich.message
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.onSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class TransactionViewModel {
    val scope = CoroutineScope(Job() + Dispatchers.Main)


    fun getTransactionList(
        page: Int = 1,
        pageSize: Int = 10,
        status: String = "",
        search: String = "",
        onLoading: (Boolean) -> Unit,
        onSuccess: (GetPaginationTransactionResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        scope.launch {
//            delay(5000)

            onLoading.invoke(true)
            val response = Network.transactionApi.getPaginatedTransactionList(page, pageSize, status, search)
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

    fun createTransaction(
        createTransactionRequest: TransactionModel,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        scope.launch {
//            delay(5000)
            val response = Network.transactionApi.postCreateTransaction(createTransactionRequest)
            response.onSuccess {
                onSuccess.invoke(this.data.statusMessage)
            }.onError {
                onError.invoke(this.message())
            }.onException {
                onError.invoke(this.message ?: "Unknown Error")
            }
        }
    }

    fun findTransactionById(
        id: String,
        onSuccess: (GetTransactionResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        scope.launch {
//            delay(5000)
            val response = Network.transactionApi.findTransactionById(id)
            response.onSuccess {
                onSuccess.invoke(this.data)
            }.onError {
                onError.invoke(this.message())
            }.onException {
                onError.invoke(this.message ?: "Unknown Error")
            }
        }
    }

    fun updateTransactionById(
        id: String,
        requestBody: TransactionModel,
        onSuccess: (BaseResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        scope.launch {
//            delay(5000)
            val response = Network.transactionApi.updateTransactionById(id, requestBody)
            response.onSuccess {
                onSuccess.invoke(this.data)
            }.onError {
                onError.invoke(this.message())
            }.onException {
                onError.invoke(this.message ?: "Unknown Error")
            }
        }
    }

    fun deleteTransactionById(
        id: String,
        onSuccess: (BaseResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        scope.launch {
//            delay(5000)
            val response = Network.transactionApi.deleteTransactionById(id)
            response.onSuccess {
                onSuccess.invoke(this.data)
            }.onError {
                onError.invoke(this.message())
            }.onException {
                onError.invoke(this.message ?: "Unknown Error")
            }
        }
    }

    fun pendingTransactionById(
        id: String,
        onSuccess: (BaseResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        scope.launch {
//            delay(5000)
            val response = Network.transactionApi.pendingTransactionById(id)
            response.onSuccess {
                onSuccess.invoke(this.data)
            }.onError {
                onError.invoke(this.message())
            }.onException {
                onError.invoke(this.message ?: "Unknown Error")
            }
        }
    }

    fun paidTransactionById(
        id: String,
        onSuccess: (BaseResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        scope.launch {
//            delay(5000)
            val response = Network.transactionApi.paidTransactionById(id)
            response.onSuccess {
                onSuccess.invoke(this.data)
            }.onError {
                onError.invoke(this.message())
            }.onException {
                onError.invoke(this.message ?: "Unknown Error")
            }
        }
    }

    fun sentTransactionById(
        id: String,
        onSuccess: (BaseResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        scope.launch {
//            delay(5000)
            val response = Network.transactionApi.sentTransactionById(id)
            response.onSuccess {
                onSuccess.invoke(this.data)
            }.onError {
                onError.invoke(this.message())
            }.onException {
                onError.invoke(this.message ?: "Unknown Error")
            }
        }
    }

    fun cancelTransactionById(
        id: String,
        onSuccess: (BaseResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        scope.launch {
//            delay(5000)
            val response = Network.transactionApi.cancelTransactionById(id)
            response.onSuccess {
                onSuccess.invoke(this.data)
            }.onError {
                onError.invoke(this.message())
            }.onException {
                onError.invoke(this.message ?: "Unknown Error")
            }
        }
    }

    fun completeTransactionById(
        id: String,
        onSuccess: (BaseResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        scope.launch {
//            delay(5000)
            val response = Network.transactionApi.completeTransactionById(id)
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
