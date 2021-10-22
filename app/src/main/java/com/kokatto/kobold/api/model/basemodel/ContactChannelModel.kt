package com.kokatto.kobold.api.model.basemodel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ContactChannelModel(
    var type: String = "",
    var account: String = "",
    var asset: String = "",
) : Parcelable
