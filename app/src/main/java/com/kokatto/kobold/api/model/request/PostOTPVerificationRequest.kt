package com.kokatto.kobold.api.model.request

data class PostOTPVerificationRequest (
    val phone: String,
    val otp: String,
)
