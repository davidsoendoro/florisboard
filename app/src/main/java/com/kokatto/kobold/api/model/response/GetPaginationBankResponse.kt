package com.kokatto.kobold.api.model.response

import com.kokatto.kobold.api.model.basemodel.BankModel

data class GetPaginationBankResponse(
    val data: Data,
    val statusCode: Int,
    val statusMessage: String
) {
    data class Data(
        val contents: List<BankModel>,
        val page: Int,
        val totalPages: Int,
        val totalRecord: Int
    )
}
