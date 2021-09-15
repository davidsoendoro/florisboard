package com.kokatto.kobold.chattemplate

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.kokatto.kobold.extension.vertical
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.AutoTextModel
import com.kokatto.kobold.api.model.response.GetPaginatedAutoTextResponse
import com.kokatto.kobold.component.DovesRecyclerViewPaginator
import com.kokatto.kobold.extension.showToast
import com.kokatto.kobold.template.recycleradapter.ChatTemplateRecyclerAdapter
import dev.patrickgold.florisboard.ime.core.FlorisBoard
import dev.patrickgold.florisboard.ime.text.key.KeyCode
import dev.patrickgold.florisboard.ime.text.keyboard.TextKeyData
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

class KeyboardChatTemplate: ConstraintLayout, ChatTemplateRecyclerAdapter.OnClick {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val florisboard: FlorisBoard? = FlorisBoard.getInstanceOrNull()

    private var chatTemplateList: ArrayList<AutoTextModel> = arrayListOf()
    private var adapter: ChatTemplateRecyclerAdapter? = null

    private var chatTemplateViewModel: ChatTemplateViewModel? = ChatTemplateViewModel()

    private val isLoadingChatTemplate = AtomicBoolean(true)
    private val isLastChatTemplate = AtomicBoolean(false)

    private var messageSnackbar: Snackbar? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val searchButton: ImageView = findViewById(R.id.search_button)
        val backButton: TextView = findViewById(R.id.back_button)
        val createTemplateButton: LinearLayout = findViewById(R.id.create_template_button)
        val chatTemplateRecycler: RecyclerView = findViewById(R.id.chat_template_recycler)
        val bottomLoading = findViewById<LinearLayout>(R.id.bottom_loading)

        createTemplateButton.setOnClickListener {
            florisboard?.inputFeedbackManager?.keyPress()
            florisboard?.setActiveInput(R.id.kobold_menu_create_chat_template)
        }

        backButton.setOnClickListener {
            florisboard?.inputFeedbackManager?.keyPress(TextKeyData(code = KeyCode.CANCEL))
            florisboard?.setActiveInput(R.id.kobold_mainmenu)
        }

        adapter = ChatTemplateRecyclerAdapter(chatTemplateList, this)

        chatTemplateViewModel?.getChatTemplateList(
            onLoading = {
                Timber.e(it.toString())
                isLoadingChatTemplate.set(it)
            },
            onSuccess = { it ->
                chatTemplateList.addAll(it.data.contents)
                adapter?.notifyItemRangeChanged(0, it.data.contents.size)
            },
            onError = {
                showToast(it)
            }
        )
        chatTemplateRecycler.adapter = adapter
        chatTemplateRecycler.vertical()

        DovesRecyclerViewPaginator(
            recyclerView = chatTemplateRecycler,
            isLoading = { isLoadingChatTemplate.get() },
            loadMore = { it ->
                bottomLoading.isVisible = true
                chatTemplateViewModel?.getChatTemplateList(
                    page = it,
                    onLoading = {
                        Timber.e(it.toString())
                        isLoadingChatTemplate.set(it)
                    },
                    onSuccess = { it ->
                        isLastChatTemplate.set(it.data.totalPages <= it.data.page)

                        isLoadingChatTemplate.set(false)
                        val initialSize = chatTemplateList.size
                        chatTemplateList.addAll(it.data.contents)
                        val finalSize = chatTemplateList.size
                        adapter?.notifyItemRangeChanged(initialSize, finalSize)

                        bottomLoading.isVisible = false
                    },
                    onError = {
                        showToast(it)
                        bottomLoading.isVisible = false
                    }
                )
            },
            onLast = { isLastChatTemplate.get() }
        ).run {
            threshold = 3
        }
    }

    override fun onClicked(data: AutoTextModel) {
        florisboard?.inputFeedbackManager?.keyPress()
        florisboard?.textInputManager?.activeEditorInstance?.commitText(data.content.toString())
    }

}
