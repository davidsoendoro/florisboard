package com.kokatto.kobold.api.model.response

import com.kokatto.kobold.api.model.basemodel.BusinessTypeModel


data class GetBusinessTypeResponse(
    val statusCode: String,
    val statusMessage: String,
    val data: ArrayList<BusinessTypeModel>
)
