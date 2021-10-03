package com.kokatto.kobold.api.model.basemodel

import android.os.Parcelable
import com.kokatto.kobold.utility.CurrencyUtility
import kotlinx.parcelize.Parcelize

@Parcelize
class DeliveryFeeModel(
    var service_name: String = "",
    var service: String = "",
    var service_logo: String = "",
    var type: String = "",
    var price: Int = 0,
    var availability: Boolean = false,
    var price_original: Int = 0,
    var price_adjustment: Int = 0,
    var price_adjustment_percentage: Int = 0,
    var highlight: Boolean = false,
    var eta: String = "",
    var error_message: String = "",
    var need_geolocation: Boolean = false,
    var tnc_html: String = "",
    var max_weight: Int = 0,
    var delivery_insurances: Array<Insurance>? = null,

    var isSelected: Boolean = false
) : Parcelable

@Parcelize
class Insurance(
    var type: String = "",
    var amount: Int = 0,
    var information: String = "",
    var title: String = "",
) : Parcelable

fun ArrayList<DeliveryFeeModel>.format(): ArrayList<DeliveryFeeModel> {
    val formattedList = arrayListOf<DeliveryFeeModel>()

    try {
        for (i in 0..this.size) {
            if (i == 0) {
                formattedList.add(
                    DeliveryFeeModel(
                        service = this[i + 1].service,
                        service_logo = this[i + 1].service_logo
                    )
                )
                formattedList.add(this[i])
            } else {
                if (this[i].service == this[i - 1].service) {
                    formattedList.add(this[i])
                } else {
                    formattedList.add(
                        DeliveryFeeModel(
                            service = this[i].service,
                            service_logo = this[i + 1].service_logo
                        )
                    )
                    formattedList.add(this[i])
                }
            }
        }
    } catch (e: Exception) {

    }
    return formattedList
}

fun ArrayList<DeliveryFeeModel>.toText(): String {
    var tempString = ""

    this.filter { it.service_name != "" && it.isSelected }
        .forEach {
            if (tempString == "") {
                tempString =
                    "${it.service} - ${CurrencyUtility.currencyFormatter(it.price)}" +
                        "\n${it.service_name} (${it.eta})"
            } else {
                tempString += "\n\n" +
                    "${it.service} - ${CurrencyUtility.currencyFormatter(it.price)}" +
                    "\n${it.service_name} (${it.eta})"
            }
        }

    return tempString
}

