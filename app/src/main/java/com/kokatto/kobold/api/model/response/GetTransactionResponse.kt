package com.kokatto.kobold.api.model.response

import com.kokatto.kobold.api.model.basemodel.TransactionModel

data class GetTransactionResponse(
    val data: TransactionModel,
    val statusCode: Int,
    val statusMessage: String
)
