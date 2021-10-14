package com.kokatto.kobold.api.model.basemodel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ContactModel(
    var _id: String,
    var name: String,
    var phoneNumber: String,
    var email: String,
    var address: String,
    var debt: Double = 0.0,
    var channels: List<ContactChannelModel>,
    var shippingAddress: List<ContactAddressModel>,
) : Parcelable
