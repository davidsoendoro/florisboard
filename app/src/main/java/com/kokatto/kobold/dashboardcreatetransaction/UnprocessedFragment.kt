package com.kokatto.kobold.dashboardcreatetransaction

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.TransactionModel
import com.kokatto.kobold.component.DovesRecyclerViewPaginator
import com.kokatto.kobold.dashboardcreatetransaction.recycleradapter.UnprocessedRecyclerAdapter
import com.kokatto.kobold.extension.showToast
import com.kokatto.kobold.extension.vertical
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

class UnprocessedFragment: Fragment(R.layout.fragment_unprocessed), UnprocessedRecyclerAdapter.OnClick {

    private var unprocessedRecyclerAdapter: UnprocessedRecyclerAdapter? = null
    private var unprocesedRecycler: RecyclerView? = null

    private var transactionViewModel: TransactionViewModel? = TransactionViewModel()
    private var transactionList: ArrayList<TransactionModel> = arrayListOf()

    private var bottomLoading: LinearLayout? = null
    private var fullscreenLoading: LinearLayout? = null

    private val isLoadingList = AtomicBoolean(true)
    private val isLast = AtomicBoolean(false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        unprocesedRecycler = view.findViewById(R.id.recycler_view)
        bottomLoading = view.findViewById(R.id.bottom_loading)
        fullscreenLoading = view.findViewById(R.id.fullcreen_loading)

        getUnprocessedTransactionList()
        unprocessedRecyclerAdapter = UnprocessedRecyclerAdapter(transactionList,this)

        DovesRecyclerViewPaginator(
            recyclerView = unprocesedRecycler!!,
            isLoading = { isLoadingList.get() },
            loadMore = {
                bottomLoading!!.isVisible = true
                showToast(it.toString())
                getUnprocessedTransactionList(it + 1)
            },
            onLast = { isLast.get() }
        ).run {
            threshold = 3
        }

        unprocesedRecycler!!.adapter = unprocessedRecyclerAdapter
        unprocesedRecycler!!.vertical()
    }

    override fun onClicked(data: String) {
        showToast(data)
    }

    private fun getUnprocessedTransactionList(page: Int = 1) {
        transactionViewModel?.getTransactionList(
            page = page,
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
                    unprocesedRecycler!!.isVisible = true
                } else {
                    isLoadingList.set(false)

                    bottomLoading!!.isVisible = false
                }
                //contoh insert data
//                    autoTextDatabase?.autoTextDao()?.insertAutoText(it.data.contents[1])
                unprocessedRecyclerAdapter!!.notifyDataSetChanged()
            },
            onError = {
                showToast(it)
                fullscreenLoading!!.isVisible = false
                unprocesedRecycler!!.isVisible = true
            }
        )
    }
}
