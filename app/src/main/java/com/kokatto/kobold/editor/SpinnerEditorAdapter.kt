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
import dev.patrickgold.florisboard.R
import com.kokatto.kobold.extension.addRipple

class SpinnerEditorItem(val label: String) {
}

class SpinnerEditorAdapter(
    private val context: Context,
    private val options: Array<SpinnerEditorItem>,
    private var selectedOption: SpinnerEditorItem,
    private val callback: (result: SpinnerEditorItem) -> Unit
) :
    RecyclerView.Adapter<SpinnerEditorAdapter.ViewHolder>() {

    inner class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {

        val layout: ConstraintLayout? = item.findViewById(R.id.kobold_spinner_item_main)
        var label: TextView? = null
        var tick: ImageView? = null

        fun bindViewHolder(option: SpinnerEditorItem, index: Int) {
            label = layout?.findViewById(R.id.kobold_spinner_item_label)
            tick = layout?.findViewById(R.id.kobold_spinner_item_checklist)

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
        return (ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_spinner_option, parent, false)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val option = options[position]
        holder.bindViewHolder(option, position)
    }

    override fun getItemCount(): Int {
        return options.size
    }
}
