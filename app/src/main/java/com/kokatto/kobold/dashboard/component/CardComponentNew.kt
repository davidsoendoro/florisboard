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
class CardComponentNew(
    context: Context,
    model: TutorialModel,
    onCompleteAction: (code: String) -> Unit
) : CardView(context) {

    init {
        LayoutInflater.from(context)
            .inflate(R.layout.item_card_tutorial_default, this, true)

        val logo = findViewById<ImageView>(R.id.tutor_logo)
        val title = findViewById<TextView>(R.id.tutor_title)
        val description = findViewById<TextView>(R.id.tutor_desc)
        val shortcut = findViewById<Button>(R.id.tutor_shortcut)
        val video = findViewById<Button>(R.id.tutor_video)

        Glide.with(this@CardComponentNew)
            .load(model.logoAsset)
            .placeholder(R.drawable.ic_kobold_manage_customer_blue)
            .into(logo)

        title.text = model.title
        description.text = model.description

        shortcut.setOnClickListener {
            when (model.targetType) {
                "URI" -> {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(model.targetActivity))
                    context.startActivity(browserIntent)
                }
                "ACTIVITY" -> {
                    context.startActivity(Intent(context, Class.forName(model.targetActivity)))
                }
            }
        }

        video.setOnClickListener {
            onCompleteAction.invoke(model.type)
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(model.videoAsset)))
        }
    }

}



