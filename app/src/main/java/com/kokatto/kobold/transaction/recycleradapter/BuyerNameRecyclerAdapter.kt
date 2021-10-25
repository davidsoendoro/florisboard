package com.kokatto.kobold.transaction.recycleradapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.ContactModel
import com.kokatto.kobold.extension.addRipple

class BuyerNameRecyclerAdapter(
    val dataList: ArrayList<ContactModel> = arrayListOf(),
) : RecyclerView.Adapter<BuyerNameRecyclerAdapter.ViewHolder>() {

    var onItemClick: ((item: ContactModel) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return (ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_buyer_name, parent, false)))
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        private val layout_row_item = item.findViewById<LinearLayout>(R.id.layout_row_item)
        private val nameText = item.findViewById<TextView>(R.id.name_text)
        private val phoneText = item.findViewById<TextView>(R.id.phone_text)

        fun bindViewHolder(data: ContactModel) {

            nameText.text = "${data.name}"
            phoneText.text = "${data.phoneNumber}"

            layout_row_item.setOnClickListener {
                onItemClick?.invoke(data)
            }

            layout_row_item.addRipple()
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindViewHolder(dataList[position])
    }
}
