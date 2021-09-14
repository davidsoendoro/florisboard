package com.kokatto.kobold.api.model.response

data class PostTokenRefreshResponse(
    val statusCode: String,
    val statusMessage: String,
    val data: Data
) {
    data class Data(
        val token: String
    )
}
