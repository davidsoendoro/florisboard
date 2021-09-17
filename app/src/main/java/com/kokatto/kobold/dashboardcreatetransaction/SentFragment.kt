package com.kokatto.kobold.dashboardcreatetransaction

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.TransactionModel
import com.kokatto.kobold.dashboardcreatetransaction.recycleradapter.UnprocessedRecyclerAdapter
import com.kokatto.kobold.extension.showToast
import com.kokatto.kobold.extension.vertical
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

class SentFragment: Fragment(R.layout.fragment_sent) , UnprocessedRecyclerAdapter.OnClick {

    private var sentRecyclerAdapter: UnprocessedRecyclerAdapter? = null
    private var sentRecycler: RecyclerView? = null

    private var transactionViewModel: TransactionViewModel? = TransactionViewModel()
    private var transactionList: ArrayList<TransactionModel> = arrayListOf()

    private val isLoadingList = AtomicBoolean(true)
    private val isLast = AtomicBoolean(false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sentRecycler = view.findViewById(R.id.sent_recycler)
        sentRecyclerAdapter = UnprocessedRecyclerAdapter(transactionList,this)

        getSentTransactionList()

        sentRecycler!!.adapter = sentRecyclerAdapter
        sentRecycler!!.vertical()
    }

    override fun onClicked(data: String) {
        showToast(data)
    }

    private fun getSentTransactionList(page: Int = 1) {
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
//                    fullscreenLoading!!.isVisible = false
//                    chatTemplateRecycler!!.isVisible = true
                } else {
                    isLoadingList.set(false)
//
//                    bottomLoading!!.isVisible = false
                }
                //contoh insert data
//                    autoTextDatabase?.autoTextDao()?.insertAutoText(it.data.contents[1])
                sentRecyclerAdapter!!.notifyDataSetChanged()
            },
            onError = {
                showToast(it)
//                fullscreenLoading!!.isVisible = false
//                chatTemplateRecycler!!.isVisible = true
            }
        )
    }
}
