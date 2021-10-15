package com.kokatto.kobold.api.model.basemodel

import android.content.Intent
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MerchantModel(
    val name: String? = "-",
    val businessField: ArrayList<String>? = arrayListOf(),
    val businessType: String? = "",
    val phone: String? = "",
) : Parcelable {

    companion object {
        val MERCHANT_MODEL_ID = "merchantModel"
//        fun MerchantModel.toIntent(): Intent {
//            return
//        }

        fun fromIntent(intent: Intent): MerchantModel? {
            return intent.getParcelableExtra(MERCHANT_MODEL_ID)
//            return bundle.getParcelable<MerchantModel>("merchantModel")
        }
    }
}
