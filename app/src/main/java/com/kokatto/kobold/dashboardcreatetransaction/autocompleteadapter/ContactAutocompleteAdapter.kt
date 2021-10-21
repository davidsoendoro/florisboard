package com.kokatto.kobold.dashboardcreatetransaction.autocompleteadapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.ContactModel
import timber.log.Timber

class ContactAutocompleteAdapter(context: Context, contactList: List<ContactModel>) :
    ArrayAdapter<ContactModel>(context, 0, contactList) {
    private var contactListFull = arrayListOf<ContactModel>()

    init {
        contactListFull = ArrayList(contactList)
    }

    private val filter = object : Filter() {
        override fun performFiltering(query: CharSequence?): FilterResults {
            val result = FilterResults()
            var contactList = arrayListOf<ContactModel>()

            if (query.isNullOrBlank()) {
                contactList = contactListFull
            } else {
                contactList = ArrayList(contactListFull.filter { contact -> contact.name.contains(query, true) })
                Timber.d("Queried contactList: $contactList")
                Timber.d("Queried contactList size: ${contactList.size}")

            }

            result.values = contactList
            result.count = contactList.size
            return result
        }

        override fun publishResults(query: CharSequence?, filterResults: FilterResults?) {
            clear()
            filterResults?.let {
                addAll(it.values as List<ContactModel>)
            }
        }
    }

    override fun getFilter(): Filter {
        return filter
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView?: LayoutInflater.from(context).inflate(R.layout.contact_item_layout, parent, false)
        val nameTextView = view.findViewById<TextView>(R.id.contactItemNameText)
        val phoneTextView = view.findViewById<TextView>(R.id.contactItemPhoneText)

        Timber.d("[CONTACT] contact: ${getItem(position)}")
        Timber.d("[CONTACT] position: $position")
        val contact = getItem(position)
        contact?.let {
            nameTextView.text = contact.name
            phoneTextView.text = contact.phoneNumber
        }

        return view
    }
}
