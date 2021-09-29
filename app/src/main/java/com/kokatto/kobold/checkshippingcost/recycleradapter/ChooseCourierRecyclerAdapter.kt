package com.kokatto.kobold.checkshippingcost.recycleradapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.AutoTextModel
import com.kokatto.kobold.api.model.basemodel.DeliveryFeeModel
import com.kokatto.kobold.extension.vertical

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

    inner class ViewHolder(item: View) : RecyclerView.ViewHolder(item), ChooseCourierPackageRecyclerAdapter.OnClick {
        private val adapter = ChooseCourierPackageRecyclerAdapter(this)
        private val choosePackageRecyclerView = item.findViewById<RecyclerView>(R.id.choose_package_recyclerview)

        fun bindViewHolder(data1: DeliveryFeeModel, data: Int) {

            choosePackageRecyclerView.adapter = adapter
            choosePackageRecyclerView.vertical()
        }

        override fun onPackageClicked(data: Int) {
            onClick.onPackageClicked(data)
        }
    }
}
