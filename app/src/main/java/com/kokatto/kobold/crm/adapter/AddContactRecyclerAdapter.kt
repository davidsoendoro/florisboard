package com.kokatto.kobold.crm.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.textfield.TextInputLayout
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.ContactChannelModel
import com.kokatto.kobold.api.model.basemodel.PropertiesModel
import com.kokatto.kobold.crm.AddContactActivity
import com.kokatto.kobold.dashboardcreatetransaction.spinner.SpinnerChannelSelector
import dev.patrickgold.florisboard.util.getActivity


class AddContactRecyclerAdapter(
    private val dataList: MutableList<ContactChannelModel>,
    private val listener: OnItemClickListener
) :
    RecyclerView.Adapter<AddContactRecyclerAdapter.DataViewHolder>() {

    private var spinnerChannelSelector: SpinnerChannelSelector? = SpinnerChannelSelector()
    private var selectedChannel: PropertiesModel? = null

    var mAddContactActivity: AddContactActivity? = null

    var context: Context? = null

    fun AddContactRecyclerAdapter(context: Context?) {
        this.context = context
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddContactRecyclerAdapter.DataViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.row_add_order_channel, parent, false)
        return DataViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AddContactRecyclerAdapter.DataViewHolder, position: Int) {
        val currentItem = dataList[position]
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var closeButton = itemView.findViewById<ImageView>(R.id.close_row_button)
        var spinnerFormChannel = itemView.findViewById<EditText>(R.id.edittext_add_contact_channel_field)
        var idFormChannel = itemView.findViewById<EditText>(R.id.edittext_add_contact_id)
        var idLayoutChannel = itemView.findViewById<TextInputLayout>(R.id.add_contact_id_layout)

        init {
            if (itemCount == 1) {
                closeButton.visibility = View.GONE
            }

            if (spinnerFormChannel.text.isEmpty()) {
                selectedChannel = null
                idLayoutChannel.setBackgroundColor(
                    ContextCompat.getColor(
                        idFormChannel.context,
                        R.color.colorEditTextDisable
                    )
                )
            } else {
                idLayoutChannel.setBackgroundColor(ContextCompat.getColor(idFormChannel.context, R.color.transparent))
            }


            itemView.setOnClickListener(this)

            closeButton.setOnClickListener(View.OnClickListener {

                //val clickedItem: ContactChannelModel = dataList[position]
                selectedChannel = null
                spinnerFormChannel?.setText(null)
                spinnerFormChannel?.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    ContextCompat.getDrawable(itemView.context.getActivity()!!, R.drawable.ic_subdued),
                    null
                )

                if (itemCount == 1) {
                    closeButton.visibility = View.GONE
                    idFormChannel.isFocusable = false
                    idFormChannel.isFocusableInTouchMode = false
                    idFormChannel.inputType = InputType.TYPE_NULL
                    idFormChannel.text.clear()
                    idLayoutChannel.setBackgroundColor(
                        ContextCompat.getColor(
                            idFormChannel.context,
                            R.color.colorEditTextDisable
                        )
                    )
                } else {
                    dataList.removeAt(position)
                    notifyItemRemoved(position)
                    //notifyDataSetChanged()
                }

                Toast.makeText(itemView.context.getActivity()!!, "TEST $position", Toast.LENGTH_LONG).show()
            })

            spinnerFormChannel.setOnClickListener(View.OnClickListener {

                val channel = spinnerFormChannel?.text.toString()
                spinnerChannelSelector = SpinnerChannelSelector().newInstance()

                if (selectedChannel == null) {
                    selectedChannel = PropertiesModel("", "", "", channel)
                }

                spinnerChannelSelector?.openSelector(
                    itemView.context.getActivity()!!.supportFragmentManager,
                    selectedChannel!!
                )
                spinnerChannelSelector?.onItemClick = {
                    selectedChannel = it
                    spinnerFormChannel?.setText(it.assetDesc)
                    constructChannel(spinnerFormChannel!!, it.assetUrl)

                    idLayoutChannel.setBackgroundColor(
                        ContextCompat.getColor(
                            idFormChannel.context,
                            R.color.transparent
                        )
                    )
                    closeButton.visibility = View.VISIBLE
                    if (it.assetDesc == "WhatsApp") {
                        var waNumber: String? = null
//                        waNumber = mAddContactActivity?.getWANumber()
                        //(getContext() as AddContactActivity).getWANumber()
                        (context as AddContactActivity)?.getWANumber()
                        //idFormChannel.setText(waNumber)
                        ///////////////////////////////////
                    } else {
                        idFormChannel.isFocusable = true
                        idFormChannel.isFocusableInTouchMode = true
                        idFormChannel.inputType = InputType.TYPE_CLASS_TEXT
                    }
                }
            })
        }

        override fun onClick(p0: View?) {
            val position: Int = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }

        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

}

