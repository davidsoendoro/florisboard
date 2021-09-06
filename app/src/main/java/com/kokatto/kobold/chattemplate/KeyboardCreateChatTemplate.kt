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

class KeyboardCreateChatTemplate: LinearLayout {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val florisboard: FlorisBoard? = FlorisBoard.getInstanceOrNull()

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

    }
}
