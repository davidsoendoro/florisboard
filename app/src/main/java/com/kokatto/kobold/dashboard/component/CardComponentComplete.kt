package com.kokatto.kobold.dashboard.component

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.TutorialModel

@SuppressLint("ViewConstructor")
class CardComponentComplete(context: Context, model: TutorialModel) : CardView(context) {

    init {
        LayoutInflater.from(context)
            .inflate(R.layout.item_card_tutorial_complete, this, true)

        val logo = findViewById<ImageView>(R.id.tutor_logo)
        val title = findViewById<TextView>(R.id.tutor_title)
        val description = findViewById<TextView>(R.id.tutor_desc)
        val video = findViewById<Button>(R.id.tutor_video)

        Glide.with(this@CardComponentComplete)
            .load(model.logoAsset)
            .placeholder(R.drawable.ic_kobold_manage_customer_blue)
            .into(logo)

        title.text = model.title
        description.text = model.description
        description.text = model.description

        video.setOnClickListener {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(model.videoAsset)))
        }
    }

}



