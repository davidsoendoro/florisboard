package com.kokatto.kobold.api.model.basemodel

import android.os.Bundle
import android.os.Parcelable
import androidx.core.os.bundleOf
import kotlinx.parcelize.Parcelize

@Parcelize
data class BusinessFieldModel(
    var filedName: String,
    val iconAsset: String,

    var isSelected: Boolean = false
) : Parcelable

fun ArrayList<BusinessFieldModel>.toBundle(): Bundle {
    return bundleOf(
        "businessFieldList" to this
    )
}

fun ArrayList<BusinessFieldModel>.fromBundle(bundle: Bundle?) {
    this.clear()
    (bundle?.getParcelableArrayList<BusinessFieldModel>("businessFieldList") as ArrayList<BusinessFieldModel>).forEach { businessFieldModel ->
        this.add(businessFieldModel.copy())
    }
}

fun List<BusinessFieldModel>.toTextFormat(): String {
    if (this.size == 1)
        return this[0].filedName
    else if (this.size > 1)
        return this[0].filedName + ", ${this.size - 1} lainnya"
    else {
        return ""
    }
}
