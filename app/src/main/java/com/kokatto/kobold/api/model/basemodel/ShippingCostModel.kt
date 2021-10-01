package com.kokatto.kobold.api.model.basemodel

data class ShippingCostModel(
    var senderAddress: DeliveryAddressModel = DeliveryAddressModel(),
    var receiverAddress: DeliveryAddressModel = DeliveryAddressModel(),
    var packageWeight: Int = 1
)
