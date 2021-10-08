package com.kokatto.kobold.api.model.basemodel

import android.os.Bundle
import android.os.Parcelable
import androidx.core.os.bundleOf
import kotlinx.parcelize.Parcelize

@Parcelize
data class BusinessTypeModel(
    val title: String,
    val subTitle: String,

    var isSelected: Boolean = false
) : Parcelable

fun ArrayList<BusinessTypeModel>.toBundle(): Bundle {
    return bundleOf(
        "businessTypeList" to this
    )
}

fun ArrayList<BusinessTypeModel>.fromBundle(bundle: Bundle?) {
    this.clear()
    (bundle?.getParcelableArrayList<BusinessTypeModel>("businessTypeList") as ArrayList<BusinessTypeModel>).forEach { businessTypeModel ->
        this.add(businessTypeModel.copy())
    }
}

fun List<BusinessTypeModel>.toTextFormat(): String {
    if (this.size == 1)
        return this[0].title
    else {
        return ""
    }
}
