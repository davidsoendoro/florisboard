package com.kokatto.kobold.api.model.request

import com.kokatto.kobold.api.model.basemodel.ContactAddressModel
import com.kokatto.kobold.api.model.basemodel.ContactChannelModel

data class PostUpdateContactByTransactionIdRequest(
    var name: String = "",
    var phoneNumber: String = "",
    var email: String = "",
    var address: String = "",
    var debt: Double = 0.0,
    var channels: ArrayList<ContactChannelModel> = arrayListOf(),
    var shippingAddress: List<ContactAddressModel> = listOf(),

)
