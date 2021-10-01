package com.kokatto.kobold.checkshippingcost.recycleradapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.DeliveryFeeModel
import com.kokatto.kobold.extension.showStrikeThrough
import com.kokatto.kobold.utility.CurrencyUtility

class ChooseCourierRecyclerAdapter(
    val chooseCourierList: ArrayList<DeliveryFeeModel>,
    val onClick: OnClick,
) : RecyclerView.Adapter<ChooseCourierRecyclerAdapter.ViewHolder>() {

    interface OnClick {
        fun onClicked(data: Boolean, index: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return (ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_choose_courier, parent, false)))
    }

    override fun onBindViewHolder(holder: ChooseCourierRecyclerAdapter.ViewHolder, position: Int) {
        holder.bindViewHolder(chooseCourierList[position], position)
    }

    override fun getItemCount(): Int {
        return chooseCourierList.size
    }

    inner class ViewHolder(val item: View) : RecyclerView.ViewHolder(item) {
        val expeditionServiceImg: ImageView = item.findViewById(R.id.expedition_service_img)
        val servicePackageLayout: MaterialCardView = item.findViewById(R.id.service_package_layout)
        var selectionItemImageRadio: MaterialCardView = item.findViewById(R.id.selection_item_image_radio)
        val selectionItemImage: ImageView = item.findViewById(R.id.selection_item_image)
        val totalPriceText: TextView = item.findViewById(R.id.total_price_text)
        val discountedPriceText: TextView = item.findViewById(R.id.discount_price_text)
        val serviceNameText: TextView = item.findViewById(R.id.service_name_text)
        val serviceEtaText: TextView = item.findViewById(R.id.service_eta_text)

        fun bindViewHolder(data: DeliveryFeeModel, index: Int) {
            servicePackageLayout.setOnClickListener {
                data.isSelected = data.isSelected.not()

                onClick.onClicked(data.isSelected, index)
            }
//            check if its just image or not
            expeditionServiceImg.isVisible = data.service_name.isEmpty()
            Glide.with(item).load(data.service_logo).into(expeditionServiceImg)

            servicePackageLayout.isVisible = data.service_name.isNotEmpty()

            totalPriceText.text = CurrencyUtility.currencyFormatter(data.price)

            discountedPriceText.isVisible = data.price != data.price_original
            discountedPriceText.text = CurrencyUtility.currencyFormatter(data.price_original)
            discountedPriceText.showStrikeThrough(true)

            serviceNameText.text = data.service_name
            serviceEtaText.text = data.eta

            setSelected(data.isSelected)
        }

        fun setSelected(isSelected: Boolean) {
            selectionItemImage.isVisible = isSelected
            if (isSelected) {
                selectionItemImageRadio.strokeColor = item.resources.getColor(R.color.kobold_blue_button)
                selectionItemImageRadio.setCardBackgroundColor(item.resources.getColor(R.color.kobold_blue_button))
            } else {
                selectionItemImageRadio.strokeColor = item.resources.getColor(R.color.inner_border)
                selectionItemImageRadio.setCardBackgroundColor(item.resources.getColor(android.R.color.transparent))
            }
        }
    }
}
