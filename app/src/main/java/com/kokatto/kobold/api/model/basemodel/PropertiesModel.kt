package com.kokatto.kobold.api.model.basemodel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PropertiesModel (
    val _id: String,
    val typeProperty: String,
    val assetUrl: String,
    val assetDesc: String,
): Parcelable
