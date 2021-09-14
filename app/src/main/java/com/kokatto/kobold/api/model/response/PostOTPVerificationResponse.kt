package com.kokatto.kobold.api.model.response

data class PostOTPVerificationResponse (
    val statusCode: String,
    val statusMessage: String,
    val data: Data
) {
    data class Data(
        val token: String,
        val refreshToken: String
    )
}
