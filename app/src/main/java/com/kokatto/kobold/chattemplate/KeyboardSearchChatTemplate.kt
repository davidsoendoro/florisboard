package com.kokatto.kobold.chattemplate

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.AutoTextModel
import com.kokatto.kobold.component.DovesRecyclerViewPaginator
import com.kokatto.kobold.extension.showToast
import com.kokatto.kobold.extension.vertical
import com.kokatto.kobold.template.recycleradapter.ChatTemplateRecyclerAdapter
import dev.patrickgold.florisboard.ime.core.FlorisBoard
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

class KeyboardSearchChatTemplate: ConstraintLayout, ChatTemplateRecyclerAdapter.OnClick {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val florisboard: FlorisBoard? = FlorisBoard.getInstanceOrNull()

    private var chatTemplateList: ArrayList<AutoTextModel> = arrayListOf()
    private var adapter: ChatTemplateRecyclerAdapter? = null

    private var textViewResultCount: TextView? = null
    private var chatTemplateRecycler: RecyclerView? = null

    private var chatTemplateViewModel: ChatTemplateViewModel? = ChatTemplateViewModel()

    private val isLoadingChatTemplate = AtomicBoolean(true)
    private val isLastChatTemplate = AtomicBoolean(false)

    var query: String = ""

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        textViewResultCount = findViewById(R.id.kobold_searchtemplate_resultcount)
        chatTemplateRecycler = findViewById(R.id.kobold_searchtemplate_searchresult)

        adapter = ChatTemplateRecyclerAdapter(chatTemplateList, this)
        chatTemplateRecycler?.adapter = adapter

        loadChatTemplate()
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        if (visibility == View.VISIBLE) {
            adapter?.dataList?.clear()
            loadChatTemplate()
        }
        super.onVisibilityChanged(changedView, visibility)
    }

    fun loadChatTemplate() {
        chatTemplateViewModel?.getChatTemplateList(
            search = query,
            onLoading = {
                Timber.e(it.toString())
                isLoadingChatTemplate.set(it)
            },
            onSuccess = { it ->
                val totalRecord = it.data.totalRecord
                textViewResultCount?.text = String.format("%d hasil ditemukan", totalRecord)

                chatTemplateList.addAll(it.data.contents)
                adapter?.notifyItemRangeChanged(0, it.data.contents.size)
            },
            onError = {
                showToast(it)
            }
        )

        chatTemplateRecycler?.let {
            val bottomLoading = findViewById<LinearLayout>(R.id.bottom_loading)

            DovesRecyclerViewPaginator(
                recyclerView = it,
                isLoading = { isLoadingChatTemplate.get() },
                loadMore = { loadMoreData ->
                    bottomLoading.isVisible = true
                    chatTemplateViewModel?.getChatTemplateList(
                        page = loadMoreData + 1,
                        search = query,
                        onLoading = { loadData ->
                            Timber.e(loadData.toString())
                            isLoadingChatTemplate.set(loadData)
                        },
                        onSuccess = { successData ->
                            isLastChatTemplate.set(successData.data.totalPages <= successData.data.page)

                            isLoadingChatTemplate.set(false)
                            val initialSize = chatTemplateList.size
                            chatTemplateList.addAll(successData.data.contents)
                            val finalSize = chatTemplateList.size
                            adapter?.notifyItemRangeChanged(initialSize, finalSize)

                            bottomLoading.isVisible = false
                        },
                        onError = { errorMessage ->
                            showToast(errorMessage)
                            bottomLoading.isVisible = false
                        }
                    )
                },
                onLast = { isLastChatTemplate.get() }
            ).run {
                threshold = 3
            }
        }
        chatTemplateRecycler?.vertical()
    }

    override fun onClicked(data: AutoTextModel) {
        florisboard?.inputFeedbackManager?.keyPress()
        florisboard?.activeEditorInstance?.commitText(data.content.toString())
        florisboard?.setActiveInput(R.id.kobold_menu_chat_template)
        florisboard?.activeEditorInstance?.activeEditText = null
    }
}
