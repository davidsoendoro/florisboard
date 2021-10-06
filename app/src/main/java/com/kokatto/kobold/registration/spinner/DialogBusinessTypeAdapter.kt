package com.kokatto.kobold.registration.spinner

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.BusinessTypeModel
import java.io.Serializable

class DialogBusinessTypeItem(val label: String) : Serializable

class DialogBusinessTypeAdapter(
    private val businessTypeList: ArrayList<BusinessTypeModel>,
    val onclick: OnClick
) : RecyclerView.Adapter<DialogBusinessTypeAdapter.ItemViewHolder>() {

    interface OnClick {
        fun onClicked(selected: Boolean, index: Int)
    }

    inner class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val mainLayout = view.findViewById<ConstraintLayout>(R.id.main_layout)
        val businessTypeTitle = view.findViewById<TextView>(R.id.business_type_title)
        val businessTypeSubtitle = view.findViewById<TextView>(R.id.business_type_subtitle)
        val selectionItemImageRadio = view.findViewById<MaterialCardView>(R.id.selection_item_image_radio)

        fun bindViewHolder(data: BusinessTypeModel, index: Int) {
            setSelected(data.isSelected)

            businessTypeTitle.text = data.title
            businessTypeSubtitle.text = data.subTitle

            mainLayout.setOnClickListener {
                onclick.onClicked(data.isSelected, index)
            }
        }

        fun setSelected(isSelected: Boolean) {
            if (isSelected) {
                selectionItemImageRadio.strokeColor = view.resources.getColor(R.color.kobold_blue_button)
                selectionItemImageRadio.setCardBackgroundColor(view.resources.getColor(R.color.kobold_blue_button))
            } else {
                selectionItemImageRadio.strokeColor = view.resources.getColor(R.color.inner_border)
                selectionItemImageRadio.setCardBackgroundColor(view.resources.getColor(android.R.color.transparent))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_spinner_form_businesstype, parent, false)

        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bindViewHolder(businessTypeList[position], position)
    }

    override fun getItemCount(): Int {
        return businessTypeList.size
    }
}
