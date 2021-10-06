package com.kokatto.kobold.api.model.response

import com.kokatto.kobold.api.model.basemodel.BusinessFieldModel

data class GetBusinessFieldResponse(
    val statusCode: String,
    val statusMessage: String,
    val data: ArrayList<BusinessFieldModel>
)
