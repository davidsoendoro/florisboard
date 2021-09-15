package com.kokatto.kobold.api.model.response

import com.kokatto.kobold.api.model.basemodel.AutoTextModel

data class GetAutoTextResponse(
    val data: Data,
    val statusCode: Int,
    val statusMessage: String
) {
    data class Data(
        val contents: AutoTextModel
    )
}