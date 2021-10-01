package com.kokatto.kobold.api.model.basemodel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class DeliveryAddressModel(
    var country: String = "Indonesia",
    var province: String = "",
    var city: String = "",
    var district: String = "",
    var postalcode: String = "",
) : Parcelable
