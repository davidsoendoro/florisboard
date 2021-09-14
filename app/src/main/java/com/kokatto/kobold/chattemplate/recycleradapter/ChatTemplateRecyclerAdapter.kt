package com.kokatto.kobold.chattemplate.recycleradapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.kokatto.kobold.R

class ChatTemplateRecyclerAdapter(
    val onClick: OnClick
): RecyclerView.Adapter<ChatTemplateRecyclerAdapter.ViewHolder>() {

    interface OnClick {
        fun onClicked(index: Int)
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
        holder.bindViewHolder(position)
    }

    override fun getItemCount(): Int {
        return 5
    }

    inner class ViewHolder(item: View) : RecyclerView.ViewHolder(item){
        val layout = item.findViewById<CardView>(R.id.main_layout)

        fun bindViewHolder(index: Int) {
            layout.setOnClickListener {
                onClick.onClicked(index)
            }
        }
    }
}
