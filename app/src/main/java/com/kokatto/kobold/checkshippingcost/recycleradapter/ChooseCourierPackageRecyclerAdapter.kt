package com.kokatto.kobold.checkshippingcost.recycleradapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.kokatto.kobold.R

class ChooseCourierPackageRecyclerAdapter(
//    val dataList: ArrayList<AutoTextModel>,
    val onClick: OnClick
) : RecyclerView.Adapter<ChooseCourierPackageRecyclerAdapter.ViewHolder>() {

    interface OnClick {
        fun onPackageClicked(data: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return (ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_choose_courier_package, parent, false)
        ))
    }

    override fun onBindViewHolder(holder: ChooseCourierPackageRecyclerAdapter.ViewHolder, position: Int) {
        holder.bindViewHolder(position)
    }

    override fun getItemCount(): Int {
        return 2
    }

    inner class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        private val layout = item.findViewById<CardView>(R.id.main_layout)

        fun bindViewHolder(data: Int) {
            layout.setOnClickListener {
                onClick.onPackageClicked(data)
            }
        }
    }
}
