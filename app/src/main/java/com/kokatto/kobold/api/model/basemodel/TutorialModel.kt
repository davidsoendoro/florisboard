package com.kokatto.kobold.api.model.basemodel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TutorialModel(
    var type: String = "",
    var topic: String = "",
    var logoAsset:String = "",
    var title: String = "",
    var description: String = "",
    var videoAsset: String = "",
    var status: String = "",
    var targetType: String = "",
    var targetActivity: String = ""
): Parcelable
