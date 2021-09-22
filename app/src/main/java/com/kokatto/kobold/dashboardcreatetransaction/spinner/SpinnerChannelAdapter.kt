package com.kokatto.kobold.dashboardcreatetransaction

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.PropertiesModel
import com.kokatto.kobold.dashboardcreatetransaction.spinner.SpinnerChannelSelector
import com.kokatto.kobold.extension.addRipple
import java.io.Serializable

class SpinnerChannelItem(val label: String): Serializable

class SpinnerChannelAdapter(
    private val context: SpinnerChannelSelector,
    private val options: ArrayList<PropertiesModel>,
    private var selectedOption: PropertiesModel,
    private val callback: (result: PropertiesModel) -> Unit
) : RecyclerView.Adapter<SpinnerChannelAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val layout: LinearLayout? = view.findViewById(R.id.spinner_channel_main)
        var radio: ImageView? = view.findViewById(R.id.spinner_channel_radio)
        var label: TextView? = view.findViewById(R.id.spinner_channel_label)
        var icon: ImageView? = view.findViewById(R.id.spinner_channel_icon)

        fun bindViewHolder(option: PropertiesModel) {
            label?.text = option.assetDesc
            icon?.let {
                Glide.with(context)
                    .load(option.assetUrl)
                    .into(it) };

            if (option.assetDesc == selectedOption.assetDesc) {
                // Selected
                radio?.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.ic_radio_selected,
                        null
                    )
                )
            } else {
                // Not selected
                radio?.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.ic_radio_default,
                        null
                    )
                )
            }

            layout?.addRipple()

            layout?.setOnClickListener {
                selectedOption = option
                callback(selectedOption)
            }

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_spinner_form_channel, parent, false)

        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bindViewHolder(options[position])
    }

    override fun getItemCount(): Int {
        return options.size
    }
}
