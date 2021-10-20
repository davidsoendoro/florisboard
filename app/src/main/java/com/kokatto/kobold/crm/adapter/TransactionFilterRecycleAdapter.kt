package com.kokatto.kobold.crm.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.kokatto.kobold.R
import com.kokatto.kobold.constant.TransactionFilterEnum
import com.kokatto.kobold.extension.addRipple

class TransactionFilterRecycleAdapter(
    var selected: TransactionFilterEnum? = null,
    val dataList: MutableList<TransactionFilterEnum> = arrayListOf(),
) : RecyclerView.Adapter<TransactionFilterRecycleAdapter.ViewHolder>() {

    var onItemClick: ((selected: TransactionFilterEnum) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return (ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_contact_filter_card, parent, false)
        ))
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindViewHolder(dataList[position], position)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ViewHolder(val item: View) : RecyclerView.ViewHolder(item) {
        private val itemLayout = item.findViewById<LinearLayoutCompat>(R.id.layout_item)
        private val cardLayout = item.findViewById<MaterialCardView>(R.id.card_filter)
        private val cardText = item.findViewById<TextView>(R.id.card_filter_text)

        @RequiresApi(Build.VERSION_CODES.M)
        fun bindViewHolder(data: TransactionFilterEnum, position: Int) {
            itemLayout.addRipple()

            cardText.text = data.desc

            if (selected != null) {
                if (data.code === selected!!.code) {
                    cardLayout.strokeColor = cardLayout.resources.getColor(R.color.stroke_primary, null)
                } else {
                    cardLayout.strokeColor = cardLayout.resources.getColor(R.color.inner_border, null)
                }
            }

            itemLayout.setOnClickListener {
                selected = data
                onItemClick?.invoke(data)
                notifyItemRangeChanged(0, dataList.size)
            }
        }
    }
}
