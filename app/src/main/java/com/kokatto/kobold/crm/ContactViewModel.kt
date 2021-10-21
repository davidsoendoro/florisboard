package com.kokatto.kobold.crm

import com.kokatto.kobold.api.Network
import com.kokatto.kobold.api.model.basemodel.BankModel
import com.kokatto.kobold.api.model.basemodel.ContactModel
import com.kokatto.kobold.api.model.basemodel.MerchantModel
import com.kokatto.kobold.api.model.request.PostBulkContactRequest
import com.kokatto.kobold.api.model.request.PostContactRequest
import com.kokatto.kobold.api.model.request.PostCreateMerchantRequest
import com.kokatto.kobold.api.model.response.BaseResponse
import com.kokatto.kobold.api.model.response.GetBankResponse
import com.kokatto.kobold.api.model.response.GetContactBulkResponse
import com.kokatto.kobold.api.model.response.GetContactResponse
import com.kokatto.kobold.api.model.response.GetPaginatedContactResponse
import com.skydoves.sandwich.message
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.onSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ContactViewModel {
    val scope = CoroutineScope(Job() + Dispatchers.Main)

    fun getPaginated(
        page: Int = 1,
        pageSize: Int = 10,
        sort: String = "",
        search: String = "",
        onLoading: (Boolean) -> Unit,
        onSuccess: (GetPaginatedContactResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        scope.launch {
            onLoading.invoke(true)
            val response = Network.contactApi.getPaginated(page, pageSize, sort, search)
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
        request: PostContactRequest,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        scope.launch {
            val response = Network.contactApi.postCreate(request)
            response.onSuccess {
                onSuccess.invoke(this.data.statusMessage)
            }.onError {
                onError.invoke(this.message())
            }.onException {
                onError.invoke(this.message ?: "Unknown Error")
            }
        }
    }

    fun createBulk(
        request: List<PostBulkContactRequest>,
        onSuccess: (GetContactBulkResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        scope.launch {
            val response = Network.contactApi.postBulk(request)
            response.onSuccess {
                onSuccess.invoke(this.data)
            }.onError {
                onError.invoke(this.message())
            }.onException {
                onError.invoke(this.message ?: "Unknown Error")
            }
        }
    }

    fun findById(
        id: String,
        onSuccess: (ContactModel) -> Unit,
        onError: (String) -> Unit
    ) {
        scope.launch {
            val response = Network.contactApi.findContactById(id)

            response.onSuccess {
                onSuccess.invoke(this.data.data)
            }.onError {
                onError.invoke(this.message())
            }.onException {
                onError.invoke(this.message())
            }
        }
    }

    fun update(
        id: String,
        request: PostContactRequest,
        onSuccess: (BaseResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        scope.launch {
            val response = Network.contactApi.postUpdate(id, request)
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
