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
import com.kokatto.kobold.dashboardcreatetransaction.spinner.SpinnerLogisticSelector
import com.kokatto.kobold.extension.addRipple

class SpinnerLogisticItem(val label: String)

class SpinnerLogisticAdapter(
    private val context: SpinnerLogisticSelector,
    private val options: ArrayList<PropertiesModel>,
    private var selectedOption: PropertiesModel,
    private val callback: (result: PropertiesModel) -> Unit
) : RecyclerView.Adapter<SpinnerLogisticAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val layout: LinearLayout? = view.findViewById(R.id.kobold_spinner_bank_main)
        var radio: ImageView? = view.findViewById(R.id.spinner_radio_default)
        var label: TextView? = view.findViewById(R.id.spinner_label)
        var icon: ImageView? = view.findViewById(R.id.spinner_icon)

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
            .inflate(R.layout.item_spinner_form_logistic, parent, false)

        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bindViewHolder(options[position])
    }


    override fun getItemCount(): Int {
        return options.size
    }
}
