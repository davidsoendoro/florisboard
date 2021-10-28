package com.kokatto.kobold.api.model.basemodel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ResponseContactModel(
    var contact : ContactModel? = null,
    var isProfileUpdated : Boolean = false,

) : Parcelable

