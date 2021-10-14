package com.kokatto.kobold.api.model.response

data class GetContactBulkResponse(
    val data: Data,
    val statusCode: Int,
    val statusMessage: String
) {
    data class Data(
        val totalRecord: Int
    )
}
