package com.kokatto.kobold.api.model.basemodel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BankModel(
    val _id: String,
    val bank: String,
    val accountNo: String,
    val accountHolder: String,
    val asset: String
) : Parcelable
