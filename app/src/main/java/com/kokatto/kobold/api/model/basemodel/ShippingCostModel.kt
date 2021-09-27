package com.kokatto.kobold.api.model.basemodel

data class ShippingCostModel(
    val _id: String,
    val senderAddress: String,
    val receiverAddress: String,
    val packageWeight: Int
)
