package com.kokatto.kobold.chattemplate

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.kokatto.kobold.chattemplate.recycleradapter.ChatTemplateRecyclerAdapter
import com.kokatto.kobold.extension.vertical
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.ime.core.FlorisBoard

class KeyboardChatTemplate: LinearLayout {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val florisboard: FlorisBoard? = FlorisBoard.getInstanceOrNull()
    private var adapter :ChatTemplateRecyclerAdapter? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val searchButton: ImageView = findViewById(R.id.search_button)
        val sortButton: ImageView = findViewById(R.id.sort_button)
        val createTemplateButton: LinearLayout = findViewById(R.id.create_template_button)
        val chatTemplateRecycler: RecyclerView = findViewById(R.id.chat_template_recycler)

        createTemplateButton.setOnClickListener {
            florisboard?.setActiveInput(R.id.kobold_menu_create_chat_template)
        }

        adapter = ChatTemplateRecyclerAdapter()
        chatTemplateRecycler.adapter = adapter
        chatTemplateRecycler.vertical()
    }


}
