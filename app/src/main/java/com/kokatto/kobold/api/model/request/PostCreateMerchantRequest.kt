package com.kokatto.kobold.api.model.request


data class PostCreateMerchantRequest(
    val name: String,
    val businessField: ArrayList<String>,
    val businessType: String
)
