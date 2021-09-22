package com.kokatto.kobold.dashboardcreatetransaction.recycleradapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.TransactionModel
import com.kokatto.kobold.extension.findTextViewId
import com.kokatto.kobold.extension.toStringDate
import com.kokatto.kobold.utility.CurrencyUtility

class TransactionHomeRecyclerAdapter(
    val dataList: ArrayList<TransactionModel> = arrayListOf(),
    val onClick: OnClick
) : RecyclerView.Adapter<TransactionHomeRecyclerAdapter.ViewHolder>() {

    interface OnClick {
        fun onClicked(data: TransactionModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return (ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_transactionhome, parent, false)))
    }

    override fun onBindViewHolder(holder: TransactionHomeRecyclerAdapter.ViewHolder, position: Int) {
        holder.bindViewHolder(dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        private val layout = item.findViewById<CardView>(R.id.main_layout)
        private val nameText = item.findTextViewId(R.id.name_text)
        private val phoneText = item.findTextViewId(R.id.phone_text)
        private val priceText = item.findTextViewId(R.id.total_price_text)
        private val dateText = item.findTextViewId(R.id.date_text)
        private val deliveryFeeText = item.findTextViewId(R.id.deliveryfee_text)
        private val channelImg = item.findViewById<ImageView>(R.id.media_img)
        private val bankImg = item.findViewById<ImageView>(R.id.bank_img)
        private val logisticImg = item.findViewById<ImageView>(R.id.logistic_img)

        fun bindViewHolder(data: TransactionModel) {
            nameText.text =
                if (data.buyer == "")
                    "Belum ada"
                else
                    data.buyer
            phoneText.text =
                if (data.phone == "")
                    "Nomor Kosong"
                else
                    data.phone

            priceText.text = CurrencyUtility.currencyFormatter(data.price)

            dateText.text = data.createdAt.toStringDate("dd MMM yyyy, HH:mm") + " WIB"
            deliveryFeeText.text = "Ongkir " + CurrencyUtility.parseValueToRbAbreviation(data.deliveryFee)


            data.channelAsset.let { asset ->
                channelImg?.let {
                    Glide.with(this.itemView.context)
                        .load(asset)
                        .placeholder(R.drawable.ic_no_number)
                        .into(it)
                }
            }

            data.bankAsset.let { asset ->
                bankImg?.let {
                    Glide.with(this.itemView.context)
                        .load(asset)
                        .into(it)
                    bankImg?.visibility = View.VISIBLE
                }
            }

            data.logisticAsset.let { asset ->
                logisticImg?.let {
                    Glide.with(this.itemView.context)
                        .load(asset)
                        .into(it)
                    logisticImg?.visibility = View.VISIBLE
                }
            }

            layout.setOnClickListener {
                onClick.onClicked(data)
            }
        }
    }
}
