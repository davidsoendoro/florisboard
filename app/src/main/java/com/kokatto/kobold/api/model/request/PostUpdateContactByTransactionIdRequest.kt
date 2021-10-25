package com.kokatto.kobold.api.model.request

import com.kokatto.kobold.api.model.basemodel.ContactAddressModel
import com.kokatto.kobold.api.model.basemodel.ContactChannelModel

data class PostUpdateContactByTransactionIdRequest(
    var transactionId: String
)
