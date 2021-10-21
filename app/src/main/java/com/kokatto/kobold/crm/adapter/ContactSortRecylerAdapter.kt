package com.kokatto.kobold.crm.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.kokatto.kobold.R
import com.kokatto.kobold.constant.ContactSortEnum
import com.kokatto.kobold.extension.addRipple

class ContactSortRecylerAdapter(
    var selected: ContactSortEnum? = null,
    val dataList: MutableList<ContactSortEnum> = arrayListOf(),
) : RecyclerView.Adapter<ContactSortRecylerAdapter.ViewHolder>() {

    var onItemClick: ((selected: ContactSortEnum) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return (ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_spinner_contact_sort, parent, false)
        ))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindViewHolder(dataList[position], position)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ViewHolder(val item: View) : RecyclerView.ViewHolder(item) {
        private val layoutItem = item.findViewById<LinearLayout>(R.id.layout_item_sort)
        private val sortRadio = item.findViewById<ImageView>(R.id.sort_radio)
        private val sortText = item.findViewById<TextView>(R.id.item_sort_text)

        fun bindViewHolder(data: ContactSortEnum, position: Int) {
            layoutItem.addRipple()

            sortText.text = data.desc

            if (selected != null) {
                if (data.code === selected!!.code) {
                    sortRadio?.setImageDrawable(
                        ContextCompat.getDrawable(
                            sortRadio.context,
                            R.drawable.ic_radio_selected
                        )
                    )
                } else {
                    sortRadio?.setImageDrawable(
                        ContextCompat.getDrawable(
                            sortRadio.context,
                            R.drawable.ic_radio_default
                        )
                    )
                }
            }

            layoutItem.setOnClickListener {
                selected = data
                onItemClick?.invoke(data)
                notifyItemRangeChanged(0, dataList.size)

            }
        }
    }

}
