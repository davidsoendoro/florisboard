package com.kokatto.kobold.api.model.response

import android.os.Bundle
import androidx.core.os.bundleOf
import com.kokatto.kobold.api.model.basemodel.AutoTextModel
import java.io.Serializable

data class GetPaginatedAutoTextResponse(
    val data: Data,
    val statusCode: Int,
    val statusMessage: String
): Serializable {
    data class Data(
        val contents: List<AutoTextModel>,
        val page: Int,
        val totalPages: Int,
        val totalRecord: Int
    ): Serializable
}
