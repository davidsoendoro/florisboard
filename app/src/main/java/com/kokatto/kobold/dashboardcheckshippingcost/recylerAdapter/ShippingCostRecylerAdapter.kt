package com.kokatto.kobold.dashboardcheckshippingcost.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.DeliveryFeeModel
import com.kokatto.kobold.utility.CurrencyUtility
import kotlin.math.log

class ShippingCostRecylerAdapter(
    private val context: Context,
    val dataList: ArrayList<DeliveryFeeModel> = arrayListOf(),
    val selectedDataList: ArrayList<DeliveryFeeModel> = arrayListOf(),
) : RecyclerView.Adapter<ShippingCostRecylerAdapter.ViewHolder>() {

    private var lastService: String? = null
    var onItemClick: ((item: DeliveryFeeModel) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return (ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_shippingcost, parent, false)))
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        private val layout_row_item = item.findViewById<ConstraintLayout>(R.id.layout_row_item)
        private val card_row_item = item.findViewById<MaterialCardView>(R.id.card_row_item)
        private val card_layout_content = item.findViewById<LinearLayoutCompat>(R.id.card_layout_content)
        private val service_logo = item.findViewById<ImageView>(R.id.service_logo)
        private val item_checkbox_card = item.findViewById<MaterialCardView>(R.id.selection_card)
        private val item_checkbox_check = item.findViewById<ImageView>(R.id.selection_card_check)
        private val item_text_price = item.findViewById<TextView>(R.id.item_text_price)
        private val item_text_price_sub = item.findViewById<TextView>(R.id.item_text_price_sub)
        private val item_text_courier = item.findViewById<TextView>(R.id.item_text_courier)
        private val item_text_period = item.findViewById<TextView>(R.id.item_text_period)
        private val item_not_avaialbe = item.findViewById<TextView>(R.id.text_not_avaiable)

        @RequiresApi(Build.VERSION_CODES.M)
        fun bindViewHolder(data: DeliveryFeeModel, position: Int) {
            val priceText = CurrencyUtility.currencyFormatter(data.price_original)
            item_text_price.text = priceText
            item_text_price_sub.text = priceText

            if(data.availability) {
                item_text_courier.text = "${data.service_name}  •"
                item_text_period.text = data.eta.toString()
            } else {
                item_text_courier.text = data.service_name
                item_text_period.text = ""
            }

            if(lastService == null || lastService != data.service){

                service_logo?.let {
                    Glide.with(context)
                        .load(data.service_logo)
                        .into(it)
                }
                service_logo.visibility = View.VISIBLE

                lastService = data.service
            } else {
                service_logo.visibility = View.GONE
            }

            if (selectedDataList.contains(data)) {
                item_checkbox_check.visibility = View.VISIBLE
                item_checkbox_card.strokeColor = item_checkbox_card.resources.getColor(R.color.kobold_dark_blue)
                item_checkbox_card.setCardBackgroundColor(item_checkbox_check.resources.getColor(R.color.kobold_dark_blue))
                card_row_item.setBackgroundColor(context.resources.getColor(R.color.shipping_cost_card_select,null))
                card_row_item.setStrokeColor(context.resources.getColor(R.color.kobold_dark_blue,null))
                card_row_item.strokeWidth = context.resources.getDimension(R.dimen.kobold_stroke_width_2dp).toInt()
            } else {
                item_checkbox_check.visibility = View.GONE
                item_checkbox_card.strokeColor = item_checkbox_card.resources.getColor(R.color.inner_border)
                item_checkbox_card.setCardBackgroundColor(item_checkbox_check.resources.getColor(android.R.color.transparent))
                card_row_item.setBackgroundColor(context.resources.getColor(R.color.shipping_cost_card_default,null))
                card_row_item.setStrokeColor(context.resources.getColor(R.color.shipping_cost_card_stroke_default,null))
                card_row_item.strokeWidth = context.resources.getDimension(R.dimen.kobold_stroke_width_1dp).toInt()
            }

            if(data.availability){
                item_not_avaialbe.visibility = View.GONE
            } else {
                card_row_item.setBackgroundColor(context.resources.getColor(R.color.kobold_gray,null))
                item_text_price.text = "Rp-"
                item_text_price.setTextColor(context.resources.getColor(R.color.text_disabled,null))
                item_not_avaialbe.visibility = View.VISIBLE
            }

            layout_row_item.setOnClickListener {
                if(data.availability){
                    lastService = null
                    onItemClick?.invoke(data)
                }
            }

            if(position == (dataList.size - 1)){
                lastService = null
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindViewHolder(dataList[position], position)
    }

}
