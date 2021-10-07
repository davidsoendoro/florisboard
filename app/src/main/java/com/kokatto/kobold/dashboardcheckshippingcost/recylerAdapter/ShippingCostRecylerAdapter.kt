package com.kokatto.kobold.dashboardcheckshippingcost.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
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
        private val card_layout_content = item.findViewById<ConstraintLayout>(R.id.card_layout_content)
        private val service_logo = item.findViewById<ImageView>(R.id.service_logo)
        private val item_checkbox = item.findViewById<ImageView>(R.id.item_checkbox)
        private val item_text_price = item.findViewById<TextView>(R.id.item_text_price)
        private val item_text_price_sub = item.findViewById<TextView>(R.id.item_text_price_sub)
        private val item_text_courier = item.findViewById<TextView>(R.id.item_text_courier)
        private val item_text_period = item.findViewById<TextView>(R.id.item_text_period)

        fun bindViewHolder(data: DeliveryFeeModel, position: Int) {
            item_text_price.text = "${CurrencyUtility.currencyFormatter(data.price_original)}"
            item_text_price_sub.text = "${CurrencyUtility.currencyFormatter(data.price_original)}"
            item_text_courier.text = "${data.service_name}  •"
            item_text_period.text = data.eta.toString()

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
                item_checkbox?.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.ic_checkbox_selected,
                        null
                    )
                )

                card_row_item.setBackgroundColor(context.resources.getColor(R.color.shipping_cost_card_select,null))
                card_row_item.setStrokeColor(context.resources.getColor(R.color.shipping_cost_card_stroke_select,null))

            } else {
                item_checkbox?.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.ic_checkbox_default,
                        null
                    )
                )

                card_row_item.setBackgroundColor(context.resources.getColor(R.color.shipping_cost_card_default,null))
                card_row_item.setStrokeColor(context.resources.getColor(R.color.shipping_cost_card_stroke_default,null))
            }

            layout_row_item.setOnClickListener {
                lastService = null
                onItemClick?.invoke(data)
            }

            if(position == (dataList.size - 1)){
                lastService = null
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindViewHolder(dataList[position], position)
    }
}
