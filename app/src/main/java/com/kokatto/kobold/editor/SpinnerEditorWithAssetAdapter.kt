package com.kokatto.kobold.editor

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kokatto.kobold.R
import com.kokatto.kobold.extension.addRipple

class SpinnerEditorWithAssetItem(val label: String, val assetUrl: String = "")

class SpinnerEditorWithAssetAdapter(
    private val context: Context,
    private val options: ArrayList<SpinnerEditorWithAssetItem>,
    private var selectedOption: SpinnerEditorWithAssetItem,
    private val callback: (result: SpinnerEditorWithAssetItem) -> Unit
) :
    RecyclerView.Adapter<SpinnerEditorWithAssetAdapter.ViewHolder>() {

    inner class ViewHolder(val item: View) : RecyclerView.ViewHolder(item) {

        val layout: ConstraintLayout? = item.findViewById(R.id.kobold_spinner_item_main_withasset)
        var asset: ImageView? = null
        var label: TextView? = null
        var tick: ImageView? = null

        fun bindViewHolder(option: SpinnerEditorWithAssetItem, index: Int) {
            asset = layout?.findViewById(R.id.kobold_spinner_item_withasset_icon)
            label = layout?.findViewById(R.id.kobold_spinner_item_withasset_label)
            tick = layout?.findViewById(R.id.kobold_spinner_item_withasset_checklist)

            Glide.with(item).load(
                if (option.assetUrl == "cash")
                    item.resources.getDrawable(R.drawable.img_cash)
                else if (option.assetUrl == "")
                    "https://kobold-test-asset.s3.ap-southeast-1.amazonaws.com/public/img_bank_unknown.png"
                else
                    option.assetUrl
            ).into(asset!!)

            label?.text = option.label

            if (option.label == selectedOption.label) {
                // Selected
                layout?.setBackgroundColor(context.resources.getColor(R.color.backgroundSelected, null))
                label?.setTypeface(null, Typeface.BOLD)
                tick?.visibility = View.VISIBLE
            } else {
                // Not selected
                layout?.setBackgroundColor(context.resources.getColor(R.color.background, null))
                label?.setTypeface(null, Typeface.NORMAL)
                tick?.visibility = View.GONE
            }
            layout?.addRipple()

            layout?.setOnClickListener {
                selectedOption = option
                callback(selectedOption)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return (ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_spinner_option_withasset, parent, false)
        ))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val option = options[position]
        holder.bindViewHolder(option, position)
    }

    override fun getItemCount(): Int {
        return options.size
    }
}
