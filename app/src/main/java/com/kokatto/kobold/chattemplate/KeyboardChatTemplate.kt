package com.kokatto.kobold.chattemplate

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.AutoTextModel
import com.kokatto.kobold.component.DovesRecyclerViewPaginator
import com.kokatto.kobold.extension.showToast
import com.kokatto.kobold.extension.vertical
import com.kokatto.kobold.roomdb.AutoTextDatabase
import com.kokatto.kobold.template.recycleradapter.ChatTemplateRecyclerAdapter
import dev.patrickgold.florisboard.ime.core.FlorisBoard
import dev.patrickgold.florisboard.ime.text.key.KeyCode
import dev.patrickgold.florisboard.ime.text.keyboard.TextKeyData
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.properties.Delegates

class KeyboardChatTemplate : ConstraintLayout, ChatTemplateRecyclerAdapter.OnClick {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val florisboard: FlorisBoard? = FlorisBoard.getInstanceOrNull()

    private var chatTemplateList: ArrayList<AutoTextModel> = arrayListOf()
    private var adapter: ChatTemplateRecyclerAdapter? = null

    private var chatTemplateRecycler: RecyclerView? = null
    private var chatTemplateRecyclerLoading: ProgressBar? = null

    private var chatTemplateViewModel: ChatTemplateViewModel? = ChatTemplateViewModel()

    private var isLoadingChatTemplate: Boolean by Delegates.observable(true) { _, oldValue, newValue ->
        if (oldValue != newValue) {
            if (newValue) {
                chatTemplateRecyclerLoading?.visibility = VISIBLE
            } else {
                chatTemplateRecyclerLoading?.visibility = GONE
            }
        }
    }
    private val isLastChatTemplate = AtomicBoolean(false)

    private var messageSnackbar: Snackbar? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val searchButton: ImageView = findViewById(R.id.search_button)
        val backButton: TextView = findViewById(R.id.back_button)
        val createTemplateButton: LinearLayout = findViewById(R.id.create_template_button)
        chatTemplateRecycler = findViewById(R.id.chat_template_recycler)
        chatTemplateRecyclerLoading = findViewById(R.id.chat_template_recycler_loading)

        searchButton.setOnClickListener {
            florisboard?.inputFeedbackManager?.keyPress()
            florisboard?.openSearchEditor(R.id.kobold_search_result)
        }
        createTemplateButton.setOnClickListener {
            florisboard?.inputFeedbackManager?.keyPress()
            florisboard?.setActiveInput(R.id.kobold_menu_create_chat_template)
        }
        backButton.setOnClickListener {
            florisboard?.inputFeedbackManager?.keyPress(TextKeyData(code = KeyCode.CANCEL))
            florisboard?.setActiveInput(R.id.kobold_mainmenu)
        }
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (changedView is KeyboardChatTemplate && visibility == View.VISIBLE && changedView == this) {
            adapter = ChatTemplateRecyclerAdapter(chatTemplateList, this)
            chatTemplateRecycler?.adapter = adapter
            adapter?.dataList?.clear()
            isLastChatTemplate.set(false)
            loadChatTemplate()
        }
    }

    fun loadChatTemplate() {
        florisboard?.koboldState = FlorisBoard.KoboldState.NORMAL

        isLoadingChatTemplate = true
        chatTemplateViewModel?.getChatTemplateList(
            onLoading = {
//                Timber.e(it.toString())
                isLoadingChatTemplate = it
            },
            onSuccess = { it ->
                AutoTextDatabase.getInstance(context)?.autoTextDao()?.clearAutoTextTable()
                it.data.contents.forEach { item ->
                    AutoTextDatabase.getInstance(context)?.autoTextDao()?.insertAutoText(item)
                }
                chatTemplateList.addAll(it.data.contents)
                adapter?.notifyItemRangeChanged(0, it.data.contents.size)

                isLoadingChatTemplate = false
            },
            onError = {
                showToast(it)
            }
        )

        chatTemplateRecycler?.let {
            val bottomLoading = findViewById<LinearLayout>(R.id.bottom_loading)

            DovesRecyclerViewPaginator(
                recyclerView = it,
                isLoading = { isLoadingChatTemplate },
                loadMore = { loadMoreData ->
                    bottomLoading.isVisible = true
                    chatTemplateViewModel?.getChatTemplateList(
                        page = loadMoreData + 1,
                        onLoading = { loadData ->
//                            Timber.e(loadData.toString())
                            isLoadingChatTemplate = loadData
                        },
                        onSuccess = { successData ->
                            isLastChatTemplate.set(successData.data.totalPages <= successData.data.page)

                            val initialSize = chatTemplateList.size
                            chatTemplateList.addAll(successData.data.contents)
                            successData.data.contents.forEach { item ->
                                AutoTextDatabase.getInstance(context)?.autoTextDao()?.insertAutoText(item)
                            }
                            val finalSize = chatTemplateList.size
                            adapter?.notifyItemRangeChanged(initialSize, finalSize)

                            isLoadingChatTemplate = false
                            bottomLoading.isVisible = false
                        },
                        onError = { errorMessage ->
                            showToast(errorMessage)
                            isLoadingChatTemplate = false
                            bottomLoading.isVisible = false
                        }
                    )
                },
                onLast = { isLastChatTemplate.get() }
            ).run {
                threshold = 5
            }
        }
        chatTemplateRecycler?.vertical()
    }

    override fun onClicked(data: AutoTextModel) {
        florisboard?.inputFeedbackManager?.keyPress()
        florisboard?.textInputManager?.activeEditorInstance?.commitText(data.content.toString())
    }

}
