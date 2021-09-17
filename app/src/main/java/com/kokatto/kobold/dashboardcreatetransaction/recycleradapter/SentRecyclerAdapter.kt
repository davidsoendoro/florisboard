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

class SentRecyclerAdapter(
//    val dataList: ArrayList<AutoTextModel>,
    val onClick: OnClick
): RecyclerView.Adapter<SentRecyclerAdapter.ViewHolder>() {

    interface OnClick {
        fun onClicked(data: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return(ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_unprocessed, parent, false)))
    }

    override fun onBindViewHolder(holder: SentRecyclerAdapter.ViewHolder, position: Int) {
        holder.bindViewHolder(position)
    }

    override fun getItemCount(): Int {
        return 5
    }

    inner class ViewHolder(item: View) : RecyclerView.ViewHolder(item){
        private val layout = item.findViewById<CardView>(R.id.main_layout)

        fun bindViewHolder(data: Int) {


            layout.setOnClickListener {
                onClick.onClicked(data.toString())
            }
        }
    }
}
