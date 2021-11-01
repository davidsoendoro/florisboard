package com.kokatto.kobold.crm.component

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.ContactChannelModel
import com.kokatto.kobold.extension.addDrawableStart


class ChannelInfoComponent(context: Context, channel: ContactChannelModel) : LinearLayoutCompat(context) {

    init {
        LayoutInflater.from(context)
            .inflate(R.layout.item_contact_info_card_channel, this, true)

        val name = findViewById<TextView>(R.id.contact_info_channel_name)
        val account = findViewById<TextView>(R.id.contact_info_channel_account)
        val accountTitle = findViewById<TextView>(R.id.contact_info_channel_account_header)

        name.setText("-")
        account.text = "-"

        if (channel.type.isBlank().not()) {
            name.setText(channel.type.lowercase().replaceFirstChar { it.uppercase() })
            name.addDrawableStart(name, channel.asset)
        }

        if (channel.account.isBlank().not()) {
            account.text = channel.account
        }
    }
}