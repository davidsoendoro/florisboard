package com.kokatto.kobold.api.model.response

import com.kokatto.kobold.api.model.basemodel.TransactionModel

data class GetPaginationTransactionResponse(
    val data: Data,
    val statusCode: Int,
    val statusMessage: String
) {
    data class Data(
        val contents: List<TransactionModel>,
        val page: Int,
        val totalPages: Int,
        val totalRecord: Int
    )
}
