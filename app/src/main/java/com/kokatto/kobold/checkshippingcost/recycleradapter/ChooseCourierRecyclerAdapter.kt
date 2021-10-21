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
import com.kokatto.kobold.extension.showSnackBar
import com.kokatto.kobold.utility.CurrencyUtility

class ChooseCourierRecyclerAdapter(
    val view: View,
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
        val notAvailableText: TextView = item.findViewById(R.id.text_not_avaiable)
        val serviceText: TextView = item.findViewById(R.id.service_text)

        fun bindViewHolder(data: DeliveryFeeModel, index: Int) {
            if (data.price <= 0) {
                serviceText.text = data.service_name

                servicePackageLayout.setOnClickListener {
                    view.showSnackBar("Alamat tujuan tidak dijangkau oleh Kurir", R.color.snackbar_error)
                }

                notAvailableText.isVisible = true
            } else {
                serviceText.text = data.service_name + " • " + data.eta
                notAvailableText.isVisible = false

                servicePackageLayout.setOnClickListener {
                    data.isSelected = data.isSelected.not()

                    onClick.onClicked(data.isSelected, index)
                }
            }
//            servicePackageLayout.setOnClickListener {
//                data.isSelected = data.isSelected.not()
//
//                onClick.onClicked(data.isSelected, index)
//            }
//            check if its just image or not
            expeditionServiceImg.isVisible = data.service_name.isEmpty()
            Glide.with(item).load(data.service_logo).into(expeditionServiceImg)

            servicePackageLayout.isVisible = data.service_name.isNotEmpty()

            totalPriceText.text = CurrencyUtility.currencyFormatter(data.price)

//            discountedPriceText.isVisible = data.price != data.price_original
//            discountedPriceText.text = CurrencyUtility.currencyFormatter(data.price_original)
//            discountedPriceText.showStrikeThrough(true)

//            serviceNameText.text = data.service_name
//            serviceEtaText.text = data.eta

            setSelected(data.isSelected)
            setActivated(data.price <= 0)
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

        fun setActivated(isActivated: Boolean) {
            if (isActivated.not()) {
                servicePackageLayout.setCardBackgroundColor(view.resources.getColor(R.color.cardViewBackgroundColor))
                totalPriceText.setTextColor(view.resources.getColor(R.color.text_primary))
                serviceText.setTextColor(view.resources.getColor(R.color.text_primary))
            } else {
                servicePackageLayout.setCardBackgroundColor(view.resources.getColor(R.color.backgroundDisabled))
                totalPriceText.setTextColor(view.resources.getColor(R.color.text_disabled))
                serviceText.setTextColor(view.resources.getColor(R.color.text_disabled))

            }
        }
    }
}
