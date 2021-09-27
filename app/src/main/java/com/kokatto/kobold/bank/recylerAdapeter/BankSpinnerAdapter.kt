package com.kokatto.kobold.bank.recylerAdapeter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.PropertiesModel
import com.kokatto.kobold.constant.ActivityConstantCode.Companion.BANK_TYPE_OTHER
import com.kokatto.kobold.extension.addRipple
import androidx.core.content.ContextCompat.getSystemService




class BankSpinnerAdapter(
    private val context: Context,
    private val options: ArrayList<PropertiesModel>,
    private var selectedOption: PropertiesModel,
    private val callback: (result: PropertiesModel) -> Unit
) : RecyclerView.Adapter<BankSpinnerAdapter.ItemViewHolder>() {

    private var lastPos: Int? = null

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val layout: ConstraintLayout? = view.findViewById(R.id.spinner_bank_main)
        var radio: ImageView? = view.findViewById(R.id.spinner_bank_radio)
        var icon: ImageView? = view.findViewById(R.id.spinner_bank_icon)
        val editTextBankOther: EditText? = view.findViewById(R.id.editText_bankOther)

        fun bindViewHolder(option: PropertiesModel, position: Int) {
            println("bindViewHolder ::")
            println("option = ${option}")
            println("selectedOption = ${selectedOption}")

            icon?.let {
                Glide.with(context)
                    .load(option.assetUrl)
                    .placeholder(R.drawable.ic_bank_unknown)
                    .into(it)
            }

            if (option.assetDesc.uppercase() == selectedOption.assetDesc.uppercase()) {
                // Selected
                radio?.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.ic_radio_selected,
                        null
                    )
                )

                // check if Other then set the view
                if(selectedOption.assetDesc.uppercase() == BANK_TYPE_OTHER) {
                    println("selectedOption.assetDesc == \"Other\"")
                    println(selectedOption)
                    editTextBankOther?.setText(selectedOption.param1)
                    editTextBankOther?.isFocusable = true
                    editTextBankOther?.requestFocus()
                    editTextBankOther?.setSelection(0)
                }


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

            icon?.isVisible = !option.assetDesc.uppercase().equals(BANK_TYPE_OTHER)
            editTextBankOther?.isVisible = option.assetDesc.uppercase().equals(BANK_TYPE_OTHER)

            layout?.addRipple()

            layout?.setOnClickListener {
                selectedOption = option
                notifyDataSetChanged()
                callback(selectedOption)
            }

            editTextBankOther?.setOnClickListener {
                selectedOption = option
                notifyDataSetChanged()
                callback(selectedOption)
            }

            editTextBankOther?.addTextChangedListener {
                val text = editTextBankOther.text.toString()
                selectedOption = PropertiesModel(
                    "","bank", "","Other", text
                )
                callback(selectedOption)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bank_spinner, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bindViewHolder(options[position], position)
    }

    override fun getItemCount(): Int {
        return options.size
    }
}

