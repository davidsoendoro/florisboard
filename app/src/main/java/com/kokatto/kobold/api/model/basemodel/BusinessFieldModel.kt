package com.kokatto.kobold.api.model.basemodel

data class BusinessFieldModel(
    var fieldName: String,
    val iconAsset: String,

    val isSelected: Boolean = false
)
