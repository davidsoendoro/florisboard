package com.kokatto.kobold.bank.recylerAdapeter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.BankModel

class BankRecyclerAdapter(
    val dataList: ArrayList<BankModel> = arrayListOf(),
    val onClick: OnClick
) : RecyclerView.Adapter<BankRecyclerAdapter.ViewHolder>() {

    interface OnClick {
        fun onClicked(data: BankModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return (ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_bank, parent, false)))
    }

    override fun onBindViewHolder(holder: BankRecyclerAdapter.ViewHolder, position: Int) {
        holder.bindViewHolder(dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        private val layout = item.findViewById<CardView>(R.id.main_layout)
        private val bankName = item.findViewById<TextView>(R.id.bank_name)
        private val bankNo = item.findViewById<TextView>(R.id.bank_no)
        private val bankHolder = item.findViewById<TextView>(R.id.bank_holder)
        private val bankIcon = item.findViewById<ImageView>(R.id.bank_icon)

        fun bindViewHolder(data: BankModel) {
            bankName.text = data.bank
            bankNo.text = data.accountNo
            bankHolder.text = data.accountHolder

            data.asset.let { asset ->
                bankIcon?.let {
                    Glide.with(this.itemView.context)
                        .load(asset)
                        .placeholder(R.drawable.ic_bank_unknown)
                        .into(it)
                }
            }

            layout.setOnClickListener {
                onClick.onClicked(data)
            }
        }
    }
}
