package com.kokatto.kobold.dashboardcreatetransaction

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.TransactionModel
import com.kokatto.kobold.component.DovesRecyclerViewPaginator
import com.kokatto.kobold.dashboardcreatetransaction.recycleradapter.TransactionHomeRecyclerAdapter
import com.kokatto.kobold.extension.showToast
import com.kokatto.kobold.extension.vertical
import com.kokatto.kobold.template.TemplateActivityInput
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

class SearchTransactionActivity : AppCompatActivity() , TransactionHomeRecyclerAdapter.OnClick {

    private var transactionViewModel: TransactionViewModel? = TransactionViewModel()

    private var buttonBack: ImageView? = null
    private var buttonClear: ImageView? = null
    private var searchResultFound: LinearLayout? = null
    private var searchResultNotFoundLayout: LinearLayout? = null
    private var searchEdittext: EditText? = null
    private var transactionRecycler: RecyclerView? = null
    private var bottomLoading: LinearLayout? = null
    private var fullscreenLoading: LinearLayout? = null

    private var transactionList: ArrayList<TransactionModel> = arrayListOf()
    private var transactionHomeRecyclerAdapter: TransactionHomeRecyclerAdapter? = null

    private val isLoadingList = AtomicBoolean(true)
    private val isLast = AtomicBoolean(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.template_activity_search)

        transactionRecycler = findViewById(R.id.chat_template_recycler)
        bottomLoading = findViewById(R.id.bottom_loading)
        fullscreenLoading = findViewById(R.id.fullcreen_loading)

        buttonBack = findViewById(R.id.back_button)
        buttonClear = findViewById(R.id.clear_button)
        searchEdittext = findViewById(R.id.search_edittext)
        searchResultFound = findViewById(R.id.search_result_found_layout)
        searchResultNotFoundLayout = findViewById(R.id.search_result_not_found_layout)

        buttonBack?.let { button -> button.setOnClickListener { onClicked(button) } }
        buttonClear?.let { button -> button.setOnClickListener { onClicked(button) } }
        searchEdittext!!.requestFocus()
        searchEdittext?.let { editText ->
            editText.setOnKeyListener { v, keyCode, event ->
                onKeyEdit(
                    v,
                    keyCode,
                    event
                )
            }
        }

//        searchEdittext!!.doOnTextChanged { text, start, before, count ->
//            showToast(text.toString())
//            performSearch(1, text.toString())
//        }

        if (transactionHomeRecyclerAdapter == null) {
            Log.e("From", "from search button")
            performSearch(1)

            transactionHomeRecyclerAdapter = TransactionHomeRecyclerAdapter(transactionList, this)
            transactionRecycler!!.adapter = transactionHomeRecyclerAdapter
            transactionRecycler!!.vertical()
        }

        DovesRecyclerViewPaginator(
            recyclerView = transactionRecycler!!,
            isLoading = { isLoadingList.get() },
            loadMore = { page ->
                bottomLoading!!.isVisible = true
                Log.e("From", "from paginator")
                performSearch(page + 1)
            },
            onLast = { isLast.get() }
        ).run {
            threshold = 3
        }
    }

    private fun onKeyEdit(view: View, keyCode: Int?, event: KeyEvent?): Boolean {
        when (view.id) {
            R.id.search_edittext -> {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event?.action == KeyEvent.ACTION_DOWN) {
                    transactionList.clear()

                    transactionRecycler?.isVisible = false
                    fullscreenLoading?.isVisible = true

                    Log.e("From", "from search button")
                    performSearch(1, search = searchEdittext?.text.toString())
                    return true
                }
            }
        }
        return false
    }

    private fun performSearch(page: Int = 1, search: String = "") {
        transactionViewModel?.getTransactionList(
            page = page,
            search = search,
            onLoading = {
                Timber.e(it.toString())
                isLoadingList.set(it)
            },
            onSuccess = { it ->
                transactionList.addAll(it.data.contents)
                isLast.set(it.data.totalPages <= it.data.page)
//                if first page
                if (page == 1) {
                    fullscreenLoading!!.isVisible = false
                    transactionRecycler!!.isVisible = true
                } else {
                    isLoadingList.set(false)

                    bottomLoading!!.isVisible = false
                }
                //contoh insert data
//                    autoTextDatabase?.autoTextDao()?.insertAutoText(it.data.contents[1])
                transactionHomeRecyclerAdapter!!.notifyDataSetChanged()
            },
            onError = {
                showToast(it)
                fullscreenLoading!!.isVisible = false
                transactionRecycler!!.isVisible = true
            }
        )
    }

    private fun onClicked(view: View) {
        when (view.id) {
            R.id.back_button -> {
                super.onBackPressed()
            }
            R.id.clear_button -> {
                searchEdittext?.setText("");
            }
        }
    }

    override fun onClicked(data: TransactionModel) {
        Intent(this, DetailActivity::class.java).apply {
            putExtra(DetailActivity.EXTRA_DATA, data)
            startActivity(this)
        }
    }
}
