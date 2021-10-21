package com.kokatto.kobold.dashboardcheckshippingcost.recylerAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.DeliveryAddressModel
import com.kokatto.kobold.extension.addRipple

class ShippingAddressRecyclerAdapter(
    private val context: Context,
    val dataList: ArrayList<DeliveryAddressModel> = arrayListOf(),
) : RecyclerView.Adapter<ShippingAddressRecyclerAdapter.ViewHolder>() {

    var onItemClick: ((item: DeliveryAddressModel) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return (ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_choose_address, parent, false)))
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        private val layout_row_item = item.findViewById<LinearLayout>(R.id.layout_row_item)
        private val textview_district = item.findViewById<TextView>(R.id.tv_distict)
        private val textview_fulladdress = item.findViewById<TextView>(R.id.tv_full_address)

        fun bindViewHolder(data: DeliveryAddressModel) {

            textview_district.text = "${data.district}"
            textview_fulladdress.text = "${data.city}, ${data.province}, ${data.country}"

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
