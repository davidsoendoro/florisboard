package com.kokatto.kobold.api.model.basemodel

data class ShippingCostModel(
    val senderAddress: DeliveryAddressModel = DeliveryAddressModel(),
    val receiverAddress: DeliveryAddressModel = DeliveryAddressModel(),
    var packageWeight: Int = 1
)
