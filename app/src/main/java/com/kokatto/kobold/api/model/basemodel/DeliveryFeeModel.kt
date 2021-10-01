package com.kokatto.kobold.api.model.basemodel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class DeliveryFeeModel (
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
): Parcelable

@Parcelize
class Insurance (
    var type: String = "",
    var amount: Int = 0,
    var information: String = "",
    var title: String = "",
): Parcelable
