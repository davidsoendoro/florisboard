package com.kokatto.kobold.transaction

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import com.kokatto.kobold.R
import com.kokatto.kobold.api.impl.ErrorResponseValidator
import com.kokatto.kobold.api.model.basemodel.TransactionModel
import com.kokatto.kobold.api.model.basemodel.createTransactionChat
import com.kokatto.kobold.component.DovesRecyclerViewPaginator
import com.kokatto.kobold.dashboardcreatetransaction.TransactionViewModel
import com.kokatto.kobold.editor.SpinnerEditorAdapter
import com.kokatto.kobold.editor.SpinnerEditorItem
import com.kokatto.kobold.extension.findTextViewId
import com.kokatto.kobold.extension.isConnectedToInternet
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

    var dataUnavailableLayout: LinearLayout? = null
    var dataAvailableLayout: LinearLayout? = null
    var connectionErrorLayout: LinearLayout? = null
    var reloadButton: MaterialCardView? = null

    private var messageSnackbar: Snackbar? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val searchButton: ImageView = findViewById(R.id.search_button)
        val backButton: TextView = findViewById(R.id.back_button)
        val statusLayout: LinearLayout = findViewById(R.id.status_layout)
        val statusText = findTextViewId(R.id.status_text)
        dataAvailableLayout = findViewById(R.id.data_available_layout)
        dataUnavailableLayout = findViewById(R.id.data_unavailable_layout)
        connectionErrorLayout = findViewById(R.id.connection_error_layout)
        reloadButton = findViewById(R.id.reload_button)
//        define as arraylist first
        val pickTemplateOptions = arrayListOf<SpinnerEditorItem>()
        val createTransactionButton: MaterialCardView = findViewById(R.id.create_transaction_button)
        val toolbarCreateTemplateButton: LinearLayout = findViewById(R.id.toolbar_create_transaction_button)
        transactionRecycler = findViewById(R.id.transaction_recycler)
        loadingView = findViewById(R.id.fullcreen_loading)

        searchButton.setOnClickListener {
            florisboard?.inputFeedbackManager?.keyPress()
            florisboard?.openSearchEditor(R.id.kobold_search_transaction)
        }
        createTransactionButton.setOnClickListener {
            florisboard?.inputFeedbackManager?.keyPress()
            florisboard?.setActiveInput(R.id.kobold_menu_create_transaction)
        }
        toolbarCreateTemplateButton.setOnClickListener {
            florisboard?.inputFeedbackManager?.keyPress()
            florisboard?.setActiveInput(R.id.kobold_menu_create_transaction)
        }
        backButton.setOnClickListener {
            florisboard?.inputFeedbackManager?.keyPress(TextKeyData(code = KeyCode.CANCEL))
            florisboard?.setActiveInput(R.id.kobold_mainmenu)
        }
        reloadButton?.setOnClickListener {
            loadTransaction()
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
//        on this layout visible
        if (changedView == this && visibility == View.VISIBLE) {
            transactionViewModel = TransactionViewModel()
//            set on backpress
            florisboard?.setActiveInputFromMainmenu = false

            adapter?.dataList?.clear()
//            if (isFirstLoad.get()) {
            loadTransaction()
//                isFirstLoad.set(false)
//            }
        }
//        on this layout not visible
        else {
            transactionList.clear()
            transactionViewModel?.onDestroy()
        }
        super.onVisibilityChanged(changedView, visibility)
    }

    private fun loadTransaction(index: Int = 0) {
        florisboard?.koboldState = FlorisBoard.KoboldState.NORMAL

        connectionErrorLayout?.isVisible = this.isConnectedToInternet().not()

        transactionViewModel?.getTransactionList(
            status = resources.getStringArray(R.array.kobold_transaction_category_apicall)[index],
            onLoading = {
                Timber.e(it.toString())
//                isLoadingTransaction = it
                loadingView?.isVisible = it
            },
            onSuccess = { it ->
//                clear page first
                if (it.data.page == 1) {
                    transactionList.clear()
                    adapter?.notifyDataSetChanged()
                }

                transactionList.addAll(it.data.contents)
                adapter?.notifyDataSetChanged()
                adapter?.notifyItemRangeInserted(0, it.data.contents.size)

                dataAvailableLayout?.isVisible = transactionList.isNullOrEmpty().not()
                dataUnavailableLayout?.isVisible = transactionList.isNullOrEmpty()
            },
            onError = {
                //showToast(it)
                if(ErrorResponseValidator.isSessionExpiredResponse(it))
                    florisboard?.setActiveInput(R.id.kobold_login)
                else
                    showToast("Koneksi internet tidak tersedia.", R.color.snackbar_error)
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
                            //showToast(errorMessage)

//                            dataUnavailableLayout?.isVisible = false
                            isLoadingTransaction = false
                            if(ErrorResponseValidator.isSessionExpiredResponse(errorMessage))
                                florisboard?.setActiveInput(R.id.kobold_login)
                            else
                                showToast(errorMessage)
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
