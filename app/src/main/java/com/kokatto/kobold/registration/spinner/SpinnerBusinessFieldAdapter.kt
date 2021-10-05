package com.kokatto.kobold.registration.spinner

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kokatto.kobold.R
import java.io.Serializable

class SpinnerBusinessFieldItem(val label: String) : Serializable

class SpinnerBusinessFieldAdapter : RecyclerView.Adapter<SpinnerBusinessFieldAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bindViewHolder(option: Int) {

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_spinner_form_businessfield, parent, false)

        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bindViewHolder(position)
    }

    override fun getItemCount(): Int {
        return 10
    }
}
