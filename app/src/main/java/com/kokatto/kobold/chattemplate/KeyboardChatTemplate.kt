package com.kokatto.kobold.chattemplate

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.kokatto.kobold.chattemplate.recycleradapter.ChatTemplateRecyclerAdapter
import com.kokatto.kobold.extension.showToast
import com.kokatto.kobold.extension.vertical
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.ime.core.FlorisBoard
import dev.patrickgold.florisboard.util.getActivity

class KeyboardChatTemplate: ConstraintLayout, ChatTemplateRecyclerAdapter.OnClick {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val florisboard: FlorisBoard? = FlorisBoard.getInstanceOrNull()
    private var adapter :ChatTemplateRecyclerAdapter? = null

    private var messageSnackbar: Snackbar? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val searchButton: ImageView = findViewById(R.id.search_button)
        val backButton: TextView = findViewById(R.id.back_button)
        val createTemplateButton: LinearLayout = findViewById(R.id.create_template_button)
        val chatTemplateRecycler: RecyclerView = findViewById(R.id.chat_template_recycler)

        createTemplateButton.setOnClickListener {
            florisboard?.setActiveInput(R.id.kobold_menu_create_chat_template)
        }

        backButton.setOnClickListener {
            florisboard?.setActiveInput(R.id.kobold_mainmenu)
        }

        adapter = ChatTemplateRecyclerAdapter(this)
        chatTemplateRecycler.adapter = adapter
        chatTemplateRecycler.vertical()
    }

    override fun onClicked(index: Int) {
        Snackbar.make(this, "Halo haloo", Snackbar.LENGTH_LONG).show()

//        florisboard?.setActiveInput(R.id.text_input)
    }

}
