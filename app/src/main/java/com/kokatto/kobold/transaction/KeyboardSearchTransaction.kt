package com.kokatto.kobold.transaction

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.TransactionModel
import com.kokatto.kobold.api.model.basemodel.createTransactionChat
import com.kokatto.kobold.component.DovesRecyclerViewPaginator
import com.kokatto.kobold.dashboardcreatetransaction.TransactionViewModel
import com.kokatto.kobold.extension.showToast
import com.kokatto.kobold.extension.vertical
import com.kokatto.kobold.transaction.recycleradapter.TransactionKeyboardRecyclerAdapter
import dev.patrickgold.florisboard.ime.core.FlorisBoard
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

class KeyboardSearchTransaction : ConstraintLayout, TransactionKeyboardRecyclerAdapter.OnClick {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    private val florisboard: FlorisBoard? = FlorisBoard.getInstanceOrNull()

    private var transactionList: ArrayList<TransactionModel> = arrayListOf()
    private var adapter: TransactionKeyboardRecyclerAdapter? = null

    private var textViewResultCount: TextView? = null
    private var chatTemplateRecycler: RecyclerView? = null

    private var transactionViewModel: TransactionViewModel? = TransactionViewModel()

    private val isLoadingChatTemplate = AtomicBoolean(true)
    private val isLastChatTemplate = AtomicBoolean(false)

    var query: String = ""

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        textViewResultCount = findViewById(R.id.kobold_searchtemplate_resultcount)
        chatTemplateRecycler = findViewById(R.id.kobold_searchtemplate_searchresult)

        adapter = TransactionKeyboardRecyclerAdapter(transactionList, this)
        chatTemplateRecycler?.adapter = adapter

    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        if (changedView == this && visibility == View.VISIBLE) {
            adapter?.dataList?.clear()
            loadChatTemplate()
        }
        super.onVisibilityChanged(changedView, visibility)
    }

    fun loadChatTemplate() {
        transactionViewModel?.getTransactionList(
            search = query,
            onLoading = {
                Timber.e(it.toString())
                isLoadingChatTemplate.set(it)
            },
            onSuccess = { it ->
                val totalRecord = it.data.totalRecord
                textViewResultCount?.text = String.format("%d hasil ditemukan", totalRecord)

                transactionList.addAll(it.data.contents)
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
                    transactionViewModel?.getTransactionList(
                        page = loadMoreData + 1,
                        search = query,
                        onLoading = { loadData ->
                            Timber.e(loadData.toString())
                            isLoadingChatTemplate.set(loadData)
                        },
                        onSuccess = { successData ->
                            isLastChatTemplate.set(successData.data.totalPages <= successData.data.page)

                            isLoadingChatTemplate.set(false)
                            val initialSize = transactionList.size
                            transactionList.addAll(successData.data.contents)
                            val finalSize = transactionList.size
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

    override fun onClicked(data: TransactionModel) {
        florisboard?.inputFeedbackManager?.keyPress()
        florisboard?.textInputManager?.activeEditorInstance?.commitText(
            createTransactionChat(data)
        )
    }
}
