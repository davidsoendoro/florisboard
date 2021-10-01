package com.kokatto.kobold.checkshippingcost

import com.kokatto.kobold.api.Network
import com.kokatto.kobold.api.model.response.GetListDeliveryFeeResponse
import com.kokatto.kobold.api.model.response.GetPaginationDeliveryAddressResponse
import com.skydoves.sandwich.message
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.onSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class ShippingCostViewModel {
    val scope = CoroutineScope(Job() + Dispatchers.Main)

    fun getPaginatedDeliveryAddress(
        page: Int = 1,
        pageSize: Int = 10,
        search: String = "",
        onLoading: (Boolean) -> Unit,
        onSuccess: (GetPaginationDeliveryAddressResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        scope.launch {
            onLoading.invoke(true)
            val response = Network.deliveryApi.getPaginatedDeliveryAddress(page, pageSize, search)
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

    fun getListDeliveryFee(
        fromCity: String,
        fromDistrict: String,
        fromPostalcode: String,
        toCity: String,
        toDistrict: String,
        toPostalcode: String,
        weight: Int,
        onSuccess: (GetListDeliveryFeeResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        scope.launch {
            val response = Network.deliveryApi.getListDeliveryFee(fromCity, fromDistrict, fromPostalcode, toCity
                ,toDistrict, toPostalcode, weight)
            response.onSuccess {
                onSuccess.invoke(this.data)
            }.onError {
                println("ERROR Response :: ${this.errorBody}")
                println("ERROR Response :: ${this.response.errorBody()}")
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
