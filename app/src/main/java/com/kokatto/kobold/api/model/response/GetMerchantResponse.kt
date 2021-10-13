package com.kokatto.kobold.api.model.response

import com.kokatto.kobold.api.model.basemodel.MerchantModel

data class GetMerchantResponse(
    val statusCode: String,
    val statusMessage: String,
    val data: MerchantModel
)
