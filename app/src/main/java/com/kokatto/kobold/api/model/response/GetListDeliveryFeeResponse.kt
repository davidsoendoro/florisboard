package com.kokatto.kobold.api.model.response

import com.kokatto.kobold.api.model.basemodel.DeliveryFeeModel

data class GetListDeliveryFeeResponse(
    val data: ArrayList<DeliveryFeeModel>,
    val statusCode: Int,
    val statusMessage: String
)
