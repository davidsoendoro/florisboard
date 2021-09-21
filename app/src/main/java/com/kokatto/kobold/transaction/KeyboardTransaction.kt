package com.kokatto.kobold.transaction

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.kokatto.kobold.extension.vertical
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.TransactionModel
import com.kokatto.kobold.component.DovesRecyclerViewPaginator
import com.kokatto.kobold.dashboardcreatetransaction.TransactionViewModel
import com.kokatto.kobold.dashboardcreatetransaction.recycleradapter.TransactionHomeRecyclerAdapter
import com.kokatto.kobold.extension.showToast
import com.kokatto.kobold.transaction.recycleradapter.TransactionKeyboardRecyclerAdapter
import dev.patrickgold.florisboard.ime.core.FlorisBoard
import dev.patrickgold.florisboard.ime.text.key.KeyCode
import dev.patrickgold.florisboard.ime.text.keyboard.TextKeyData
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

class KeyboardTransaction : ConstraintLayout, TransactionKeyboardRecyclerAdapter.OnClick {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val florisboard: FlorisBoard? = FlorisBoard.getInstanceOrNull()

    private var transactionList: ArrayList<TransactionModel> = arrayListOf()
    private var adapter: TransactionKeyboardRecyclerAdapter? = null

    private var transactionRecycler: RecyclerView? = null

    private var transactionViewModel: TransactionViewModel? = TransactionViewModel()

    private val isLoadingTransaction = AtomicBoolean(true)
    private val isLastTransaction = AtomicBoolean(false)

    private var messageSnackbar: Snackbar? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val searchButton: ImageView = findViewById(R.id.search_button)
        val backButton: TextView = findViewById(R.id.back_button)
//        val createTemplateButton: LinearLayout = findViewById(R.id.create_template_button)
        transactionRecycler = findViewById(R.id.transaction_recycler)

        searchButton.setOnClickListener {
//            florisboard?.inputFeedbackManager?.keyPress()
//            florisboard?.openSearchEditor()
        }
//        createTemplateButton.setOnClickListener {
//            florisboard?.inputFeedbackManager?.keyPress()
//            florisboard?.setActiveInput(R.id.kobold_menu_create_chat_template)
//        }
        backButton.setOnClickListener {
            florisboard?.inputFeedbackManager?.keyPress(TextKeyData(code = KeyCode.CANCEL))
            florisboard?.setActiveInput(R.id.kobold_mainmenu)
        }

        adapter = TransactionKeyboardRecyclerAdapter(transactionList, this)
        transactionRecycler?.adapter = adapter

        loadTransaction()
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        if (visibility == View.VISIBLE && florisboard?.koboldState == FlorisBoard.KoboldState.TEMPLATE_LIST_RELOAD) {
            adapter?.dataList?.clear()
            loadTransaction()
        }
        super.onVisibilityChanged(changedView, visibility)
    }

    fun loadTransaction() {
        florisboard?.koboldState = FlorisBoard.KoboldState.NORMAL

        transactionViewModel?.getTransactionList(
            onLoading = {
                Timber.e(it.toString())
                isLoadingTransaction.set(it)
            },
            onSuccess = { it ->
                transactionList.addAll(it.data.contents)
                adapter?.notifyItemRangeChanged(0, it.data.contents.size)
            },
            onError = {
                showToast(it)
            }
        )

        transactionRecycler?.let {
            val bottomLoading = findViewById<LinearLayout>(R.id.bottom_loading)

            DovesRecyclerViewPaginator(
                recyclerView = it,
                isLoading = { isLoadingTransaction.get() },
                loadMore = { loadMoreData ->
                    bottomLoading.isVisible = true
                    transactionViewModel?.getTransactionList(
                        page = loadMoreData + 1,
                        onLoading = { loadData ->
                            Timber.e(loadData.toString())
                            isLoadingTransaction.set(loadData)
                        },
                        onSuccess = { successData ->
                            isLastTransaction.set(successData.data.totalPages <= successData.data.page)

                            isLoadingTransaction.set(false)
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
                onLast = { isLastTransaction.get() }
            ).run {
                threshold = 0
            }
        }
        transactionRecycler?.vertical()
    }

    override fun onClicked(data: String) {
        florisboard?.inputFeedbackManager?.keyPress()
        florisboard?.textInputManager?.activeEditorInstance?.commitText(data)
    }

}
