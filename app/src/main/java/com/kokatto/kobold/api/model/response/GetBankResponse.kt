package com.kokatto.kobold.api.model.response

import com.kokatto.kobold.api.model.basemodel.BankModel

data class GetBankResponse(
    val data: Data,
    val statusCode: Int,
    val statusMessage: String
) {
    data class Data(
        val contents: BankModel
    )
}
