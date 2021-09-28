package com.kokatto.kobold.dashboardcreatetransaction

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.BankModel
import com.kokatto.kobold.constant.ActivityConstantCode.Companion.CASH
import com.kokatto.kobold.dashboardcreatetransaction.spinner.SpinnerBankSelector
import com.kokatto.kobold.extension.addRipple

class SpinnerBankAdapter(
    private val context: SpinnerBankSelector,
    private val options: ArrayList<BankModel>,
    private var selectedOption: BankModel,
    private val callback: (result: BankModel, callbackType: String) -> Unit,
) : RecyclerView.Adapter<SpinnerBankAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val layout: ConstraintLayout? = view.findViewById(R.id.spinner_bank_main)
        var radio: ImageView? = view.findViewById(R.id.spinner_bank_radio)
        var icon: ImageView? = view.findViewById(R.id.spinner_bank_icon)
        var bankLabel: TextView? = view.findViewById(R.id.spinner_bank_label)
        var bankNo: TextView? = view.findViewById(R.id.spinner_bank_no)
        var bankHolder: TextView? = view.findViewById(R.id.spinner_bank_holder)
        var bankEdit: ImageButton? = view.findViewById(R.id.spinner_bank_edit)


        fun bindViewHolder(option: BankModel) {

            if(option.accountNo.uppercase().equals(CASH)) {
                bankLabel?.text = ""
                bankNo?.text = option.accountNo
                bankHolder?.text = ""
                bankEdit?.visibility = View.GONE

                icon?.let {
                    Glide.with(context)
                        .load(option.asset)
                        .placeholder(R.drawable.img_cash)
                        .into(it) };

            } else {
                bankLabel?.text = option.bank
                bankNo?.text = option.accountNo
                bankHolder?.text = option.accountHolder
                bankEdit?.visibility = View.VISIBLE

                icon?.let {
                    Glide.with(context)
                        .load(option.asset)
                        .placeholder(R.drawable.ic_bank_unknown)
                        .into(it) };
            }

            if (option._id == selectedOption._id) {
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
                callback(selectedOption, "onclick" )
            }

            bankEdit?.setOnClickListener {
                selectedOption = option
                callback(selectedOption, "onedit")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_spinner_form_bank, parent, false)

        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bindViewHolder(options[position])
    }


    override fun getItemCount(): Int {
        return options.size
    }
}
