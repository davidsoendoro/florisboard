package com.kokatto.kobold.registration.spinner

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.BusinessFieldModel
import java.io.Serializable

class DialogBusinessFieldItem(val label: String) : Serializable

class DialogBusinessFieldAdapter(
    private val businessFieldList: ArrayList<BusinessFieldModel>,
    val onclick: OnClick
) : RecyclerView.Adapter<DialogBusinessFieldAdapter.ItemViewHolder>() {

    interface OnClick {
        fun onClicked(selected: Boolean, index: Int)
    }

    inner class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val mainLayout = view.findViewById<LinearLayout>(R.id.main_layout)
        val businessFieldName = view.findViewById<TextView>(R.id.business_field_name)
        val selectionItemImage = view.findViewById<ImageView>(R.id.selection_item_image)
        val selectionItemImageRadio = view.findViewById<MaterialCardView>(R.id.selection_item_image_radio)

        fun bindViewHolder(data: BusinessFieldModel, index: Int) {
            setSelected(data.isSelected)

            businessFieldName.text = data.filedName

            mainLayout.setOnClickListener {
                onclick.onClicked(data.isSelected, index)
            }
        }

        fun setSelected(isSelected: Boolean) {
            selectionItemImage.isVisible = isSelected
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
            .inflate(R.layout.item_spinner_form_businessfield, parent, false)

        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bindViewHolder(businessFieldList[position], position)
    }

    override fun getItemCount(): Int {
        return businessFieldList.size
    }
}
