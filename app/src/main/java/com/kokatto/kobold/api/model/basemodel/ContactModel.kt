package com.kokatto.kobold.api.model.basemodel

import android.content.ContentResolver
import android.content.Context
import android.os.Parcelable
import android.provider.ContactsContract
import kotlinx.parcelize.Parcelize


@Parcelize
data class ContactModel(
    var _id: String = "",
    var name: String,
    var phoneNumber: String,
    var email: String = "",
    var address: String = "",
    var debt: Double = 0.0,
    var lastTransaction: Long? = null,
    var channels: List<ContactChannelModel> = listOf(),
    var shippingAddress: List<ContactAddressModel> = listOf(),
    var isFromBackend: Boolean = false
) : Parcelable

private val PROJECTION = arrayOf(
    ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
    ContactsContract.Contacts.DISPLAY_NAME,
    ContactsContract.CommonDataKinds.Phone.NUMBER
)

fun getContactList(context: Context): ArrayList<ContactModel> {
    var contactList: ArrayList<ContactModel> = ArrayList()

    val cr: ContentResolver = context.contentResolver
    val cursor = cr.query(
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
        PROJECTION,
        null,
        null,
        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
    )
    if (cursor != null) {
        val mobileNoSet = HashSet<String>()
        try {
            val nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            var name: String
            var number: String
            while (cursor.moveToNext()) {
                name = cursor.getString(nameIndex)
                number = cursor.getString(numberIndex)
                number = number.replace(" ", "")
                if (!mobileNoSet.contains(number)) {
                    contactList.add(ContactModel("", name, number))
                    mobileNoSet.add(number)
//                        Log.d(
//                            "phone", "Phone Number: name = " + name
//                                + " No = " + number
//                        )
                }
            }

        } catch (e: SecurityException) {
            return contactList
        } finally {
            cursor.close()
        }
    }
    return contactList
}



