package com.kokatto.kobold.api.model.basemodel

data class TransactionModel(
    val _id: String,
    val address: String,
    val buyer: String,
    val channel: String,
    val deliveryFee: Int,
    val latestStatus: String,
    val logistic: String,
    val notes: String,
    val payingMethod: String,
    val phone: String,
    val price: Int,
    val createdAt: Long
)
