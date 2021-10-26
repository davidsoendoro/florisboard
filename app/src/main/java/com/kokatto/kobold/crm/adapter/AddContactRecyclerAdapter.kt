package com.kokatto.kobold.crm.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.ContactChannelModel
import com.kokatto.kobold.api.model.basemodel.PropertiesModel
import com.kokatto.kobold.dashboardcreatetransaction.spinner.SpinnerChannelSelector
import dev.patrickgold.florisboard.util.getActivity


class AddContactRecyclerAdapter(
    private val dataList: ArrayList<ContactChannelModel>,
    private val listener: OnItemClickListener
) :
    RecyclerView.Adapter<AddContactRecyclerAdapter.DataViewHolder>() {

    private var spinnerChannelSelector: SpinnerChannelSelector? = SpinnerChannelSelector()
    private var count: Int = 0

    companion object {
        fun AddContactRecyclerAdapter.getData(): ArrayList<ContactChannelModel> {
            return dataList
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddContactRecyclerAdapter.DataViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.row_add_order_channel, parent, false)
        return DataViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AddContactRecyclerAdapter.DataViewHolder, position: Int) {
        holder.bindViewHolder(dataList[position], position)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var deleteButton = itemView.findViewById<ImageView>(R.id.close_row_button)
        var channelEditText = itemView.findViewById<TextInputEditText>(R.id.edittext_add_contact_channel_field)
        var idEdittext = itemView.findViewById<EditText>(R.id.edittext_add_contact_id)
        var idEdittextTitle = itemView.findViewById<TextView>(R.id.add_contact_id_title)
        var idEdittextLayout = itemView.findViewById<TextInputLayout>(R.id.add_contact_id_layout)

        fun bindViewHolder(data: ContactChannelModel, index: Int) {
            channelEditText.setText(data.type)
            idEdittext.setText(data.account)

            if (itemCount > 1) count = 1

            if (index == 0 && data.type == "" && itemCount == 1 && count == 0) {
                deleteButton.visibility = View.GONE
            } else {
                deleteButton.visibility = View.VISIBLE
            }

            constructChannel(channelEditText!!, data.asset)

            if (data.type == "" || data.type == "Belum ada") {
                idEdittextLayout.setBackgroundColor(itemView.resources.getColor(R.color.backgroundDisabled))
                idEdittext.isFocusable = false
                idEdittext.isCursorVisible = false
            } else {
                idEdittextLayout.setBackgroundColor(itemView.resources.getColor(R.color.colorWhite))
                idEdittext.isFocusableInTouchMode = true
                idEdittext.isCursorVisible = true
            }

            if (data.type == "Belum ada") idEdittextTitle.text = "Nomor telepon"
            else if (data.type == "WhatsApp") idEdittextTitle.text = "Nomor WhatsApp"
            else if (data.type == "WhatsApp Business") idEdittextTitle.text = "Nomor WhatsApp"
            else if (data.type == "Line") idEdittextTitle.text = "Akun Line"
            else if (data.type == "Facebook Messenger") idEdittextTitle.text = "Nama Profil"
            else if (data.type == "Instagram") idEdittextTitle.text = "Akun Instagram"
            else if (data.type == "Bukalapak Chat") idEdittextTitle.text = "Akun Bukalapak"
            else if (data.type == "Tokopedia Chat") idEdittextTitle.text = "Akun Tokopedia"
            else if (data.type == "Shopee Chat") idEdittextTitle.text = "Akun Shopee"
            else idEdittextTitle.text = "Nomor WhatsApp/ ID"

            deleteButton.setOnClickListener {
                idEdittext.text.clear()

//                channelEditText.text?.clear()
//                channelEditText.setCompoundDrawablesWithIntrinsicBounds(
//                    null,
//                    null,
//                    ContextCompat.getDrawable(channelEditText.context, R.drawable.ic_subdued),
//                    null
//                )
//                listener.onDataChange(
//                    ContactChannelModel("", "", "", ""),
//                    index
//                )

                if (itemCount == 1 && count == 1) {
                    count = 0
                }

                listener.onDataChange(null, index)
            }


            channelEditText.setOnClickListener {
                spinnerChannelSelector = SpinnerChannelSelector().newInstance()

                spinnerChannelSelector?.openSelector(
                    itemView.context.getActivity()!!.supportFragmentManager, PropertiesModel("", "", "", data.type)
                )
                spinnerChannelSelector?.onItemClick = {
                    channelEditText?.setText(it.assetDesc)
                    constructChannel(channelEditText!!, it.assetUrl)

                    if (it.assetDesc.contains("Belum ada", false)) {
                        idEdittext.isFocusable = false
                        idEdittext.isCursorVisible = false
                        idEdittextLayout.setBackgroundColor(itemView.resources.getColor(R.color.kobold_blue_button))
                        idEdittext.isEnabled = false
                    } else {
                        idEdittext.isFocusableInTouchMode = true
                        idEdittext.isCursorVisible = true
                        idEdittext.isEnabled = true
                    }

                    listener.onDataChange(
                        ContactChannelModel(it.assetDesc, idEdittext.text.toString(), it.assetUrl),
                        index
                    )
                }
            }

            idEdittext.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus.not() && v == idEdittext && data.account != idEdittext.text.toString()) {
                    data.account = idEdittext.text.toString()

                    listener.onDataChange(data, index)
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onDataChange(data: ContactChannelModel?, index: Int)
    }

    private fun constructChannel(editText: EditText, assetUrl: String) {
        if (assetUrl == "")
            editText.setCompoundDrawables(
                null,
                null,
                editText.context.resources.getDrawable(R.drawable.ic_subdued, null),
                null
            )
        else
            Glide.with(editText.context).load(assetUrl).apply(RequestOptions().fitCenter()).into(
                object : CustomTarget<Drawable>(50, 50) {
                    override fun onLoadCleared(placeholder: Drawable?) {
                        editText.setCompoundDrawablesWithIntrinsicBounds(
                            placeholder,
                            null,
                            editText.context.resources.getDrawable(R.drawable.ic_subdued, null),
                            null
                        )
                        editText.compoundDrawablePadding = 12
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable>?
                    ) {
                        editText.setCompoundDrawablesWithIntrinsicBounds(
                            resource,
                            null,
                            editText.context.resources.getDrawable(R.drawable.ic_subdued, null),
                            null
                        )
                        editText.compoundDrawablePadding = 12
                    }
                }
            )
    }
}


