package com.kokatto.kobold.dashboardcreatetransaction.recycleradapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.kokatto.kobold.api.model.basemodel.AutoTextModel
import com.kokatto.kobold.api.model.response.GetPaginatedAutoTextResponse
import com.kokatto.kobold.extension.findTextViewId
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.TransactionModel
import com.kokatto.kobold.extension.toStringDate
import com.kokatto.kobold.utility.CurrencyUtility

class PaidRecyclerAdapter(
    val dataList: ArrayList<TransactionModel>,
    val onClick: OnClick
): RecyclerView.Adapter<PaidRecyclerAdapter.ViewHolder>() {

    interface OnClick {
        fun onClicked(data: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return(ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_unprocessed, parent, false)))
    }

    override fun onBindViewHolder(holder: PaidRecyclerAdapter.ViewHolder, position: Int) {
        holder.bindViewHolder(dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ViewHolder(item: View) : RecyclerView.ViewHolder(item){
        private val layout = item.findViewById<CardView>(R.id.main_layout)
        private val nameText = item.findTextViewId(R.id.name_text)
        private val phoneText = item.findTextViewId(R.id.phone_text)
        private val dateText = item.findTextViewId(R.id.date_text)
        private val deliveryFeeText = item.findTextViewId(R.id.deliveryfee_text)

        fun bindViewHolder(data: TransactionModel) {
            nameText.text = data.buyer
            phoneText.text = data.phone
            dateText.text = data.createdAt.toStringDate("dd MMM yyyy, HH:mm") + " WIB"
            deliveryFeeText.text ="Ongkir " + CurrencyUtility.parseValueToRbAbreviation(data.deliveryFee)

            layout.setOnClickListener {
                onClick.onClicked(data.toString())
            }
        }
    }
}
