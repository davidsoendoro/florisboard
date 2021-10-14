package com.kokatto.kobold.api.model.response

import com.kokatto.kobold.api.model.basemodel.ContactModel
import java.io.Serializable

data class GetPaginatedContactResponse(
    val data: Data,
    val statusCode: Int,
    val statusMessage: String
) : Serializable {
    data class Data(
        val contents: List<ContactModel>,
        val page: Int,
        val totalPages: Int,
        val totalRecord: Int
    ) : Serializable
}
