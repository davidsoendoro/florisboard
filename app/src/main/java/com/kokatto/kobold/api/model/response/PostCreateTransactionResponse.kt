package com.kokatto.kobold.api.model.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PostCreateTransactionResponse(
    val statusCode: String,
    val statusMessage: String,
    val data: Data
) : Parcelable {

    @Parcelize
    data class Data(
        val _id: String,
        val isProfileChange: Boolean
    ) : Parcelable
}
