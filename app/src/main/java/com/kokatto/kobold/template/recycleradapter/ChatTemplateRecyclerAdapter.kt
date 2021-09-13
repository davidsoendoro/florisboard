package com.kokatto.kobold.template.recycleradapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.kokatto.kobold.api.model.response.PaginatedAutoTextResponse
import com.kokatto.kobold.extension.findTextViewId
import dev.patrickgold.florisboard.R

class ChatTemplateRecyclerAdapter(
    val dataList: ArrayList<PaginatedAutoTextResponse.Content>,
    val onClick: OnClick
): RecyclerView.Adapter<ChatTemplateRecyclerAdapter.ViewHolder>() {

    interface OnClick {
        fun onClicked(data: PaginatedAutoTextResponse.Content)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return(ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_chat_template, parent, false)))
    }

    override fun onBindViewHolder(holder: ChatTemplateRecyclerAdapter.ViewHolder, position: Int) {
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
        private val contentText = item.findTextViewId(R.id.content_text)
        private val categoryText = item.findTextViewId(R.id.category_text)

        fun bindViewHolder(data: PaginatedAutoTextResponse.Content) {
            titleText.text = data.title
            contentText.text = data.content
            categoryText.text = data.template

            layout.setOnClickListener {
                onClick.onClicked(data)
            }
        }
    }
}
