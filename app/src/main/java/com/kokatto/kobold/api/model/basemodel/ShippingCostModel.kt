package com.kokatto.kobold.api.model.basemodel

data class ShippingCostModel(
    val _id: String = "",
    var senderAddress: String = "",
    var receiverAddress: String = "",
    var packageWeight: Int = 1
)
