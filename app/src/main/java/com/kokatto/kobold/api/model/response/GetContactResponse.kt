package com.kokatto.kobold.api.model.response

import com.kokatto.kobold.api.model.basemodel.ContactModel
import com.kokatto.kobold.api.model.basemodel.MerchantModel

data class GetContactResponse(
    val data: ContactModel,
    val statusCode: Int,
    val statusMessage: String
)

