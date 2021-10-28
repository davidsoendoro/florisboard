package com.kokatto.kobold.api.model.response

import com.kokatto.kobold.api.model.basemodel.ContactModel

data class GetContactResponse(
    val data: ContactModel,
    val statusCode: Int,
    val statusMessage: String,
)
//ResponseAddContactModel

