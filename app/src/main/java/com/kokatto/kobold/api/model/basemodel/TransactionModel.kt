package com.kokatto.kobold.api.model.basemodel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TransactionModel(
    val _id: String = "",
    val address: String,
    val buyer: String,
    val channel: String,
    val channelAsset: String = "",
    val deliveryFee: Int,
    val latestStatus: String = "",
    val logistic: String,
    val logisticAsset: String = "",
    val notes: String,
    val payingMethod: String,
    val bankType: String,
    val bankAccountNo: String,
    val bankAccountName: String,
    val bankAsset: String,
    val bankAccountNo: String = "",
    val bankAccountName: String = "",
    val bankAsset: String = "",
    val phone: String,
    val price: Int,
    val createdAt: Long = 0,
    val updatedAt: Long = 0,
) : Parcelable
