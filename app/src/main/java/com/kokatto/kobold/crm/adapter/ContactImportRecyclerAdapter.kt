package com.kokatto.kobold.crm.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.BankModel
import com.kokatto.kobold.api.model.basemodel.ContactImportModel
import com.kokatto.kobold.extension.addRipple

class ContactImportRecyclerAdapter(
    val dataList: MutableList<ContactImportModel> = arrayListOf(),
    val selectedList: MutableList<ContactImportModel> = arrayListOf(),
) : RecyclerView.Adapter<ContactImportRecyclerAdapter.ViewHolder>() {

    interface OnClick {
        fun onClicked(data: BankModel)
    }

    var onItemClick: ((item: ContactImportModel) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return (ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_contacts, parent, false)))
    }

    override fun onBindViewHolder(holder: ContactImportRecyclerAdapter.ViewHolder, position: Int) {
        holder.bindViewHolder(dataList[position], position)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ViewHolder(val item: View) : RecyclerView.ViewHolder(item) {
        private val layoutItem = item.findViewById<LinearLayoutCompat>(R.id.layout_item_contact)
        private val header = item.findViewById<TextView>(R.id.contact_header)
        private val name = item.findViewById<TextView>(R.id.contact_name)
        private val phoneNumber = item.findViewById<TextView>(R.id.contact_phone)
        private val selectionItemImageRadio = item.findViewById<MaterialCardView>(R.id.selection_card)
        private val selectionItemImage = item.findViewById<ImageView>(R.id.selection_item_check)

        fun bindViewHolder(data: ContactImportModel, position: Int) {
            header.text = data.header
            name.text = data.name
            phoneNumber.text = data.phoneNumber
            selectionItemImage.isVisible = data.isSelected

            if (data.isSelected || selectedList.contains(data)) {
                selectionItemImageRadio.strokeColor = item.resources.getColor(R.color.kobold_dark_blue)
                selectionItemImageRadio.setCardBackgroundColor(item.resources.getColor(R.color.kobold_dark_blue))
            } else {
                selectionItemImageRadio.strokeColor = item.resources.getColor(R.color.inner_border)
                selectionItemImageRadio.setCardBackgroundColor(item.resources.getColor(android.R.color.transparent))
            }

            layoutItem.addRipple()

            layoutItem.setOnClickListener {
                data.isSelected = data.isSelected.not()
                if(data.isSelected) {
                    selectedList.add(data)
                } else {
                    selectedList.remove(data)
                }
                notifyItemChanged(position)
                onItemClick?.invoke(data)
            }

        }
    }

}
