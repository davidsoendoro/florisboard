package com.kokatto.kobold.api.model.response

import com.kokatto.kobold.api.model.basemodel.DeliveryAddressModel

data class GetPaginationDeliveryAddressResponse(
    val data: Data,
    val statusCode: Int,
    val statusMessage: String
) {
    data class Data(
        val contents: List<DeliveryAddressModel>,
        val page: Int,
        val totalPages: Int,
        val totalRecord: Int
    )
}
