package com.kokatto.kobold.api.model.request


data class PostCreateMerchantRequest(
    var name: String = "",
    var businessField: List<String> = listOf(),
    var businessType: String = ""
) {
    fun convertFromMerchantInfo() {

    }
}
