package com.kokatto.kobold.api.model.response

import com.kokatto.kobold.api.model.basemodel.TutorialModel

data class GetTutorialPaginatedResponse(
    val data: Data,
    val statusCode: Int,
    val statusMessage: String
) {
    data class Data(
        val contents: List<TutorialModel>,
        val complete: Int,
        val total: Int
    )
}
