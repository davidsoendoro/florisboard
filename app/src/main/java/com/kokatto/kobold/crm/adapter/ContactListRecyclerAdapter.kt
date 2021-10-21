package com.kokatto.kobold.crm.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.ContactModel
import com.kokatto.kobold.extension.addRipple
import com.kokatto.kobold.extension.toStringDate
import com.kokatto.kobold.utility.CurrencyUtility
import java.text.SimpleDateFormat
import java.util.*

class ContactListRecyclerAdapter(
    val dataList: MutableList<ContactModel> = arrayListOf(),
) : RecyclerView.Adapter<ContactListRecyclerAdapter.ViewHolder>() {

    var onItemClick: ((item: ContactModel) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return (ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_contacts_crm, parent, false)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindViewHolder(dataList[position], position)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ViewHolder(val item: View) : RecyclerView.ViewHolder(item) {
        private val layoutItem = item.findViewById<MaterialCardView>(R.id.layout_row_item)
        private val layoutDebt = item.findViewById<LinearLayoutCompat>(R.id.layout_debt)
        private val header = item.findViewById<TextView>(R.id.contact_header)
        private val contactName = item.findViewById<TextView>(R.id.contact_name)
        private val contactDebtAmount = item.findViewById<TextView>(R.id.contact_debt)
        private val contactLastTransaction = item.findViewById<TextView>(R.id.contact_last_transaction)

        fun bindViewHolder(data: ContactModel, position: Int) {
            layoutItem.addRipple()

            if (data.name.isNullOrBlank().not()) {
                header.text = data.name.get(0).toString().uppercase(Locale.getDefault())
                contactName.text = data.name
            } else {
                header.text = "#"
                contactName.text = data.phoneNumber
            }

            if (data.debt > 0) {
                layoutDebt.visibility = View.VISIBLE
                contactDebtAmount.text = CurrencyUtility.parseValueToIdr(data.debt)
            } else {
                layoutDebt.visibility = View.GONE
            }

            if (data.lastTransaction !== null) {
                contactLastTransaction.text = String.format("", parseDateTransaction(data.lastTransaction!!))
            } else {
                contactLastTransaction.text = "Belum ada riwayat transaksi"
            }

            layoutItem.setOnClickListener {
                onItemClick?.invoke(data)
            }
        }
    }

    private fun parseDateTransaction(dateMillis: Long): String {
        val lastTrxDate = dateMillis.toStringDate("ddMMyyyy")
        val lastTime = String.format("%s WIB", dateMillis.toStringDate("HH:mm"))
        val lastDate = dateMillis.toStringDate("dd MMM yyyy")

        if (lastTrxDate == SimpleDateFormat("ddMMyyyy", Locale.getDefault()).format(Calendar.getInstance().time)) {
            return lastTime
        } else {
            return lastDate
        }
    }


}
