package com.kokatto.kobold.transaction

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.TransactionModel
import com.kokatto.kobold.api.model.basemodel.createTransactionChat
import com.kokatto.kobold.component.DovesRecyclerViewPaginator
import com.kokatto.kobold.dashboardcreatetransaction.TransactionViewModel
import com.kokatto.kobold.editor.SpinnerEditorAdapter
import com.kokatto.kobold.editor.SpinnerEditorItem
import com.kokatto.kobold.extension.findTextViewId
import com.kokatto.kobold.extension.showToast
import com.kokatto.kobold.extension.vertical
import com.kokatto.kobold.transaction.recycleradapter.TransactionKeyboardRecyclerAdapter
import dev.patrickgold.florisboard.ime.core.FlorisBoard
import dev.patrickgold.florisboard.ime.text.key.KeyCode
import dev.patrickgold.florisboard.ime.text.keyboard.TextKeyData
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.properties.Delegates

class KeyboardTransaction : ConstraintLayout, TransactionKeyboardRecyclerAdapter.OnClick {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val florisboard: FlorisBoard? = FlorisBoard.getInstanceOrNull()

    var selectedOption: SpinnerEditorItem =
        SpinnerEditorItem(resources.getStringArray(R.array.kobold_transaction_category_values)[0])

    private var transactionList: ArrayList<TransactionModel> = arrayListOf()
    private var adapter: TransactionKeyboardRecyclerAdapter? = null

    private var transactionRecycler: RecyclerView? = null
    private var loadingView: LinearLayout? = null

    private var transactionViewModel: TransactionViewModel? = TransactionViewModel()

    private var isLoadingTransaction: Boolean by Delegates.observable(true) { _, oldValue, newValue ->
        if (oldValue != newValue) {
            if (newValue) {
                loadingView?.visibility = VISIBLE
            } else {
                loadingView?.visibility = GONE
            }
        }
    }
    private val isLastTransaction = AtomicBoolean(false)
    private val isFirstLoad = AtomicBoolean(true)

//    var dataUnavailableLayout: LinearLayout? = null

    private var messageSnackbar: Snackbar? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val searchButton: ImageView = findViewById(R.id.search_button)
        val backButton: TextView = findViewById(R.id.back_button)
        val statusLayout: LinearLayout = findViewById(R.id.status_layout)
        val statusText = findTextViewId(R.id.status_text)
//        dataUnavailableLayout = findViewById(R.id.data_unavailable_layout)
//        define as arraylist first
        val pickTemplateOptions = arrayListOf<SpinnerEditorItem>()
        val createTemplateButton: LinearLayout = findViewById(R.id.create_template_button)
        transactionRecycler = findViewById(R.id.transaction_recycler)
        loadingView = findViewById(R.id.bottom_loading)

        searchButton.setOnClickListener {
            florisboard?.inputFeedbackManager?.keyPress()
            florisboard?.openSearchEditor(R.id.kobold_search_transaction)
        }
        createTemplateButton.setOnClickListener {
            florisboard?.inputFeedbackManager?.keyPress()
            florisboard?.setActiveInput(R.id.kobold_menu_create_transaction)
        }
        backButton.setOnClickListener {
            florisboard?.inputFeedbackManager?.keyPress(TextKeyData(code = KeyCode.CANCEL))
            florisboard?.setActiveInput(R.id.kobold_mainmenu)
        }

//        add data from string array to array list
        resources.getStringArray(R.array.kobold_transaction_category_values).forEach {
            pickTemplateOptions.add(SpinnerEditorItem(it))
        }

        statusLayout.setOnClickListener {
            florisboard?.inputFeedbackManager?.keyPress()
            florisboard?.openSpinner(
                R.id.kobold_menu_transaction, SpinnerEditorAdapter(
                    context,
//                    convert arraylist data to array
                    pickTemplateOptions.toTypedArray(), selectedOption
                ) { result ->
                    florisboard.inputFeedbackManager.keyPress()
                    statusText.text = result.label
                    florisboard.setActiveInput(R.id.kobold_menu_transaction)
                    selectedOption = result

                    transactionList.clear()
                    adapter?.notifyDataSetChanged()

                    loadTransaction(
                        index = pickTemplateOptions.indexOf(result)
                    )
                }
            )
        }
        adapter = TransactionKeyboardRecyclerAdapter(transactionList, this)
        transactionRecycler?.adapter = adapter
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
//        on this layout not visible
        if (changedView == this && visibility == View.VISIBLE) {
//            set on backpress
            florisboard?.setActiveInputFromMainmenu = false

            adapter?.dataList?.clear()
//            if (isFirstLoad.get()) {
            Log.d("OkHttpClient", "first--------------------------------------")
            loadTransaction()
//                isFirstLoad.set(false)
//            }
        }
//        on this layout visible
        else {
            transactionList.clear()
        }
        super.onVisibilityChanged(changedView, visibility)
    }

    private fun loadTransaction(index: Int = 0) {
        florisboard?.koboldState = FlorisBoard.KoboldState.NORMAL
        transactionViewModel?.getTransactionList(
            status = resources.getStringArray(R.array.kobold_transaction_category_apicall)[index],
            onLoading = {
                Timber.e(it.toString())
                isLoadingTransaction = it
//                dataUnavailableLayout?.isVisible = false
            },
            onSuccess = { it ->
//                clear page first
                if (it.data.page == 1) {
                    transactionList.clear()
                    adapter?.notifyDataSetChanged()
                }

                transactionList.addAll(it.data.contents)
                adapter?.notifyItemRangeInserted(0, it.data.contents.size)
            },
            onError = {
                showToast(it)
            }
        )

        transactionRecycler?.let {
            DovesRecyclerViewPaginator(
                recyclerView = it,
                isLoading = { isLoadingTransaction },
                loadMore = { loadMoreData ->
                    isLoadingTransaction = true
                    transactionViewModel?.getTransactionList(
                        status = resources.getStringArray(R.array.kobold_transaction_category_apicall)[index],
                        page = loadMoreData + 1,
                        onLoading = { loadData ->
                            Timber.e(loadData.toString())
                            isLoadingTransaction = loadData
                        },
                        onSuccess = { successData ->
                            isLastTransaction.set(successData.data.totalPages <= successData.data.page)
//                            dataUnavailableLayout?.isVisible = successData.data.contents.isEmpty()

                            isLoadingTransaction = false
                            val initialSize = transactionList.size
//                            clear page first
                            if (successData.data.page == 1) {
                                showToast("first page")
                                transactionList.clear()
                                adapter?.notifyDataSetChanged()
                            }

                            transactionList.addAll(successData.data.contents)
                            val finalSize = successData.data.contents.size
                            adapter?.notifyItemRangeInserted(initialSize, finalSize)
                        },
                        onError = { errorMessage ->
                            showToast(errorMessage)

//                            dataUnavailableLayout?.isVisible = false
                            isLoadingTransaction = false
                        }
                    )
                },
                onLast = { isLastTransaction.get() }
            ).run {
                threshold = 5
            }
        }
        transactionRecycler?.vertical()
    }

    override fun onClicked(data: TransactionModel) {
        florisboard?.inputFeedbackManager?.keyPress()
        florisboard?.textInputManager?.activeEditorInstance?.commitText(
            createTransactionChat(data)
        )
    }

}
