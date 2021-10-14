package com.kokatto.kobold.api.model.basemodel

import android.os.Bundle
import android.os.Parcelable
import androidx.core.os.bundleOf
import kotlinx.parcelize.Parcelize

@Parcelize
data class MerchantModel(
    val name: String? = "-",
    val businessField: ArrayList<String>? = arrayListOf(),
    val businessType: String? = "",
    val phone: String? = "",
) : Parcelable {

    fun MerchantModel.toBundle(): Bundle {
        return bundleOf(
            "merchantModel" to this
        )
    }

    fun MerchantModel.fromBundle(bundle: Bundle): MerchantModel? {
        return bundle.getParcelable<MerchantModel>("merchantModel")
    }

}
