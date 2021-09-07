package com.kokatto.kobold.chattemplate.recycleradapter

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.patrickgold.florisboard.R

class ChatTemplateRecyclerAdapter(): RecyclerView.Adapter<ChatTemplateRecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return(ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_chat_template, parent, false)))
    }

    override fun onBindViewHolder(holder: ChatTemplateRecyclerAdapter.ViewHolder, position: Int) {
        holder.bindViewHolder(position)
    }

    override fun getItemCount(): Int {
        return 5
    }

    inner class ViewHolder(item: View) : RecyclerView.ViewHolder(item){
        fun bindViewHolder(index: Int) {

        }
    }
}
