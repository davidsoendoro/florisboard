package com.kokatto.kobold.api.model.basemodel

import android.os.Parcelable
import android.widget.EditText
import android.widget.TextView
import com.kokatto.kobold.R
import kotlinx.parcelize.Parcelize

@Parcelize
data class PropertiesModel(
    val _id: String,
    var typeProperty: String,
    var assetUrl: String,
    var assetDesc: String,
    var param1: String = "",
) : Parcelable {
    companion object {
        fun PropertiesModel.setTitleText(textView: TextView, editText: EditText) {
            if (this.assetDesc == "Belum ada") {
                editText.isFocusable = false
                editText.isCursorVisible = false
                editText.setText("")

                textView.text = "Nomor telepon"
            } else {
                editText.isFocusableInTouchMode = true
                editText.isCursorVisible = true
                var string = textView.context.resources.getString(R.string.form_trx_phone)

                if (this.assetDesc == "WhatsApp")
                    string = "Nomor WhatsApp"
                else if (this.assetDesc == "WhatsApp Business")
                    string = "Nomor WhatsApp"
                else if (this.assetDesc == "Line")
                    string = "Akun Line"
                else if (this.assetDesc == "Facebook Messenger")
                    string = "Nama Profil"
                else if (this.assetDesc == "Instagram")
                    string = "Akun Instagram"
                else if (this.assetDesc == "Bukalapak Chat")
                    string = "Akun Bukalapak"
                else if (this.assetDesc == "Tokopedia Chat")
                    string = "Akun Tokopedia"
                else if (this.assetDesc == "Shopee Chat")
                    string = "Akun Shopee"

                textView.text = string
            }
        }
    }
}
