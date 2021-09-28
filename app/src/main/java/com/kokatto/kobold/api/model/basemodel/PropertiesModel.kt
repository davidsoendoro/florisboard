package com.kokatto.kobold.api.model.basemodel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PropertiesModel (
    val _id: String,
    var typeProperty: String,
    var assetUrl: String,
    var assetDesc: String,
    var param1: String = "",
): Parcelable
