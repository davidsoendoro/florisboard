package com.kokatto.kobold.api.model.basemodel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ContactAddressModel(
    var postalCode: String = "",
    var province: String = "",
    var city: String = "",
    var district: String = "",
    var fullAddress: String = "",
    var latCoordinate: Double = 0.0,
    var longCoordinate: Double = 0.0,
) : Parcelable
