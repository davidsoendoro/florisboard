package com.kokatto.kobold.api.model.response

import com.kokatto.kobold.api.model.basemodel.ResponseContactModel

data class GetResponseContactResponse (
    val data: ResponseContactModel,
    val statusCode: Int,
    val statusMessage: String,
)
