package com.kokatto.kobold.checkshippingcost.recycleradapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.AutoTextModel
import com.kokatto.kobold.api.model.basemodel.DeliveryFeeModel
<<<<<<< HEAD
=======
import com.kokatto.kobold.extension.vertical
>>>>>>> f92ad8fe8e48b3aec333ea40e84d1ef092b82aa5

class ChooseCourierRecyclerAdapter(
    val chooseCourierList: ArrayList<DeliveryFeeModel>,
    val onClick: OnClick,
) : RecyclerView.Adapter<ChooseCourierRecyclerAdapter.ViewHolder>() {

    interface OnClick {
        fun onClicked(data: AutoTextModel)
        fun onPackageClicked(int: Int)
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

    inner class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {

<<<<<<< HEAD
        fun bindViewHolder(data: DeliveryFeeModel, index: Int) {

=======
        fun bindViewHolder(data1: DeliveryFeeModel, data: Int) {
>>>>>>> f92ad8fe8e48b3aec333ea40e84d1ef092b82aa5

        }
    }
}
