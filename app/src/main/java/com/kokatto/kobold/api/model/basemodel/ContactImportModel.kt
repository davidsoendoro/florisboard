package com.kokatto.kobold.api.model.basemodel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ContactImportModel (
    var contactId: String,
    var header: String,
    var name: String,
    var phoneNumber: String,
    var isSelected: Boolean = false
): Parcelable

