package com.kokatto.kobold.crm.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.ContactImportModel
import com.kokatto.kobold.crm.DataItem

class AddContactRecyclerAdapter(private val exampleList: List<DataItem>,
                                private val listener:OnItemClickListener
) :
    RecyclerView.Adapter<AddContactRecyclerAdapter.DataViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddContactRecyclerAdapter.DataViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.row_add_order_channel,parent,false)
        return DataViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AddContactRecyclerAdapter.DataViewHolder, position: Int) {
        val currentItem = exampleList[position]

    }

    override fun getItemCount(): Int {
        return exampleList.size
    }

    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        private val closeButton = itemView.findViewById<ImageView>(R.id.close_button)

        init {
            itemView.setOnClickListener(this)

            closeButton.setOnClickListener(View.OnClickListener {
//
            })
        }

        override fun onClick(p0: View?) {
            val position:Int = adapterPosition
            if(position!=RecyclerView.NO_POSITION){
                listener.onItemClick(position)
            }

        }
    }

    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }
}
