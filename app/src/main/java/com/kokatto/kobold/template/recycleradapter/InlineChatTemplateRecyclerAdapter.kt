package com.kokatto.kobold.template.recycleradapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.kokatto.kobold.api.model.basemodel.AutoTextModel
import com.kokatto.kobold.api.model.response.GetPaginatedAutoTextResponse
import com.kokatto.kobold.extension.findTextViewId
import com.kokatto.kobold.R

class InlineChatTemplateRecyclerAdapter(
    val dataList: ArrayList<AutoTextModel>,
    val onClick: OnClick
): RecyclerView.Adapter<InlineChatTemplateRecyclerAdapter.ViewHolder>() {

    interface OnClick {
        fun onClicked(data: AutoTextModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return(ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_inline_chat_template, parent, false)))
    }

    override fun onBindViewHolder(holder: InlineChatTemplateRecyclerAdapter.ViewHolder, position: Int) {
//        if (position == itemCount - 1) {
//            holder.layout.setMargins(
//                bottom = 100
//            )
//        } else {
//            holder.layout.setMargins(
//                bottom = 0
//            )
//        }
        holder.bindViewHolder(dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ViewHolder(item: View) : RecyclerView.ViewHolder(item){
        private val layout = item.findViewById<CardView>(R.id.main_layout)
        private val titleText = item.findTextViewId(R.id.title_text)

        fun bindViewHolder(data: AutoTextModel) {
            titleText.text = data.title

            layout.setOnClickListener {
                onClick.onClicked(data)
            }
        }
    }
}
