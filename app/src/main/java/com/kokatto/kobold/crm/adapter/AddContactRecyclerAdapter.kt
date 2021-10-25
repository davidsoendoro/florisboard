package com.kokatto.kobold.crm.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
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
import com.kokatto.kobold.crm.AddContactActivity
import com.kokatto.kobold.dashboardcreatetransaction.spinner.SpinnerChannelSelector
import dev.patrickgold.florisboard.util.getActivity


class AddContactRecyclerAdapter(
    private val dataList: ArrayList<ContactChannelModel>,
    private val listener: OnItemClickListener
) :
    RecyclerView.Adapter<AddContactRecyclerAdapter.DataViewHolder>() {

    private var spinnerChannelSelector: SpinnerChannelSelector? = SpinnerChannelSelector()

    var mAddContactActivity: AddContactActivity? = null

//    var context: Context? = null
//    fun AddContactRecyclerAdapter(context: Context?) {
//        this.context = context
//    }

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
        var idEdittextLayout = itemView.findViewById<TextInputLayout>(R.id.add_contact_id_layout)

        fun bindViewHolder(data: ContactChannelModel, index: Int) {
            channelEditText.setText(data.type)
            idEdittext.setText(data.account)
            deleteButton.isVisible = itemCount >= 1 && data.type != ""

//            idEdittext.setText(dataList[adapterPosition].account)
//            channelEditText.setText(dataList[adapterPosition].type)
            constructChannel(channelEditText!!, data.asset)

            data

//            if (itemCount == 1 && data.type == "") {
//                deleteButton.visibility = View.GONE
//            }
//            else {
//                deleteButton.visibility = View.VISIBLE
//            }


            if (data.type == "") {
                idEdittextLayout.setBackgroundColor(itemView.resources.getColor(R.color.backgroundDisabled))
                idEdittext.isFocusable = false
                idEdittext.isCursorVisible = false
            } else {
                idEdittextLayout.setBackgroundColor(itemView.resources.getColor(R.color.background))
                idEdittext.isFocusableInTouchMode = true
                idEdittext.isCursorVisible = true
            }

            deleteButton.setOnClickListener {
                idEdittext.text.clear()
                channelEditText.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    ContextCompat.getDrawable(channelEditText.context, R.drawable.ic_subdued),
                    null
                )

//                idFormChannel.isFocusable = false
//                idFormChannel.isFocusableInTouchMode = false
//                idFormChannel.inputType = InputType.TYPE_NULL
//                idEdittext.isFocusable = false
//                idEdittext.isCursorVisible = false
//                idEdittext.text.clear()
//                idEdittextLayout.setBackgroundColor(itemView.resources.getColor(R.color.backgroundDisabled))

                listener.onDataChange(null, index)

//                if (itemCount == 1) {
//                    deleteButton.visibility = View.GONE
//                } else {
//                    dataList.removeAt(adapterPosition)
//                    notifyItemRemoved(adapterPosition)
//                }
            }


            channelEditText.setOnClickListener {
                spinnerChannelSelector = SpinnerChannelSelector().newInstance()

                spinnerChannelSelector?.openSelector(
                    itemView.context.getActivity()!!.supportFragmentManager, PropertiesModel("", "", "", data.type)
                    //.context.getActivity()!!.supportFragmentManager,
                )
                spinnerChannelSelector?.onItemClick = {
                    channelEditText?.setText(it.assetDesc)
                    constructChannel(channelEditText!!, it.assetUrl)

                    if (it.assetDesc.contains("Belum ada", false)) {
                        idEdittext.isFocusable = false
                        idEdittext.isCursorVisible = false
                        idEdittextLayout.setBackgroundColor(
                            ContextCompat.getColor(
                                idEdittext.context,
                                R.color.backgroundDisabled
                            )
                        )
                    } else {
                        idEdittext.isFocusableInTouchMode = true
                        idEdittext.isCursorVisible = true
                        idEdittextLayout.setBackgroundColor(
                            ContextCompat.getColor(
                                idEdittext.context,
                                R.color.colorWhite
                            )
                        )
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

//            idEdittext.doAfterTextChanged {
//                data.account = it.toString()
//
////                listener.onDataChange(data, index)
//            }
        }
    }

    interface OnItemClickListener {
        fun onDataChange(data: ContactChannelModel?, index: Int)
    }

    private fun constructChannel(editText: EditText, assetUrl: String) {
        Glide.with(editText.context).load(assetUrl).apply(RequestOptions().fitCenter()).into(
            object : CustomTarget<Drawable>(50, 50) {
                override fun onLoadCleared(placeholder: Drawable?) {
                    editText.setCompoundDrawablesWithIntrinsicBounds(
                        placeholder, null,
                        editText.context.resources.getDrawable(R.drawable.ic_subdued, null), null
                    )
                    editText.compoundDrawablePadding = 12
                }

                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    editText.setCompoundDrawablesWithIntrinsicBounds(
                        resource, null,
                        editText.context.resources.getDrawable(R.drawable.ic_subdued, null), null
                    )
                    editText.compoundDrawablePadding = 12
                }
            }
        )
    }
}


