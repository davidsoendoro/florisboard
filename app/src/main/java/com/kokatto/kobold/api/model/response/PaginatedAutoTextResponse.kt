package com.kokatto.kobold.api.model.response

data class PaginatedAutoTextResponse(
    val data: Data,
    val statusCode: Int,
    val statusMessage: String
) {
    data class Data(
        val contents: List<Content>,
        val page: String,
        val totalRecord: Int
    )

    data class Content(
        val _id: String,
        val content: String,
        val template: String,
        val title: String
    )
}
