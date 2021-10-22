package com.kokatto.kobold.dashboardcreatetransaction.autocompleteadapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.ContactChannelModel
import com.kokatto.kobold.api.model.basemodel.ContactModel
import com.kokatto.kobold.crm.ContactViewModel
import timber.log.Timber

class ContactAutocompleteAdapter(context: Context, contactList: List<ContactModel>) :
    ArrayAdapter<ContactModel>(context, 0, contactList) {
    private var contactListFull = arrayListOf<ContactModel>()
    private val contactViewModel = ContactViewModel()
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
            contactViewModel.getPaginated(1,100, "", query.toString(), {}, {
                val contacts = it.data.contents
                contacts.map { contact ->
                    contact.isFromBackend = true
                    contact.channels = arrayListOf(ContactChannelModel("Belum Ada", "12341234"))
                }
                contactList.addAll(contacts)
                Timber.d("Adding contacts: ${contacts}")
                val filterResultsBackend = FilterResults()
                filterResultsBackend.values = contactList
                filterResultsBackend.count = contactList.size
                publishResults(query, filterResultsBackend)
            }, {

            })

            result.values = contactList
            result.count = contactList.size
            return result
        }

        override fun publishResults(query: CharSequence?, filterResults: FilterResults?) {
            clear()
            filterResults?.let {
                addAll(it.values as List<ContactModel>)
                Timber.d("Publish contact result size: ${(it.values as ArrayList<ContactModel>).size}")
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
            if(it.isFromBackend){
                view.setBackgroundColor(context.getColor(R.color.kobold_yellow_fff2e0))
            } else {
                view.setBackgroundColor(context.getColor(R.color.colorWhite))
            }
        }

        return view
    }
}
