package com.kokatto.kobold.api.model.response

import com.kokatto.kobold.api.model.basemodel.PropertiesModel

data class GetStandardPropertiesResponse(
    val data: List<PropertiesModel>,
    val statusCode: Int,
    val statusMessage: String
)
