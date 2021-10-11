package com.kokatto.kobold.dashboardcreatetransaction

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.kokatto.kobold.R
import com.kokatto.kobold.api.impl.DashboardSessionExpiredEventHandler
import com.kokatto.kobold.api.impl.ErrorResponseValidator
import com.kokatto.kobold.api.model.basemodel.TransactionModel
import com.kokatto.kobold.component.DovesRecyclerViewPaginator
import com.kokatto.kobold.constant.TransactionStatusConstant
import com.kokatto.kobold.dashboardcreatetransaction.recycleradapter.TransactionHomeRecyclerAdapter
import com.kokatto.kobold.extension.vertical
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

class CompleteFragment: Fragment(R.layout.fragment_complete) , TransactionHomeRecyclerAdapter.OnClick {

    private var completeRecyclerAdapter: TransactionHomeRecyclerAdapter? = null
    private var completeRecycler: RecyclerView? = null

    private var transactionViewModel: TransactionViewModel? = TransactionViewModel()
    private var transactionList: ArrayList<TransactionModel> = arrayListOf()

    private var bottomLoading: LinearLayout? = null
    private var fullscreenLoading: LinearLayout? = null

    private var emptyLayout: ConstraintLayout? = null
    private var createEmptyButton: Button? = null

    private val isLoadingList = AtomicBoolean(true)
    private val isLast = AtomicBoolean(false)

    private var archiveActivityListener: ArchiveActivityListener? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        completeRecycler = view.findViewById(R.id.recycler_view)
        bottomLoading = view.findViewById(R.id.bottom_loading)
        fullscreenLoading = view.findViewById(R.id.fullcreen_loading)

        emptyLayout = view.findViewById(R.id.pending_layout_empty)
        createEmptyButton = view.findViewById(R.id.empty_create_button)

        getCompleteTransactionList()

        completeRecyclerAdapter = TransactionHomeRecyclerAdapter(transactionList,this)

        DovesRecyclerViewPaginator(
            recyclerView = completeRecycler!!,
            isLoading = { isLoadingList.get() },
            loadMore = {
                bottomLoading!!.isVisible = true
                //showToast(it.toString())
                getCompleteTransactionList(it + 1)
            },
            onLast = { isLast.get() }
        ).run {
            threshold = 3
        }

        completeRecycler!!.adapter = completeRecyclerAdapter
        completeRecycler!!.vertical()
    }

    override fun onClicked(data: TransactionModel) {
        archiveActivityListener?.openDetailActivity(data)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            archiveActivityListener = context as ArchiveActivityListener
        } catch (castException: ClassCastException) {
            // Listener cannot be attached
        }
    }

    private fun getCompleteTransactionList(page: Int = 1) {
        transactionViewModel?.getTransactionList(
            page = page,
            status = TransactionStatusConstant.COMPLETE,
            onLoading = {
                Timber.e(it.toString())
                isLoadingList.set(it)
            },
            onSuccess = { it ->
                if(it.data.totalRecord > 0){
                    showDataState()
                } else {
                    showEmptyState()
                }

                transactionList.addAll(it.data.contents)
                isLast.set(it.data.totalPages <= it.data.page)
//                if first page
                if (page == 1) {
                    fullscreenLoading!!.isVisible = false
                    completeRecycler!!.isVisible = true
                } else {
                    isLoadingList.set(false)
                    bottomLoading!!.isVisible = false
                }
                //contoh insert data
//                    autoTextDatabase?.autoTextDao()?.insertAutoText(it.data.contents[1])
                completeRecyclerAdapter!!.notifyDataSetChanged()
            },
            onError = {
                if(ErrorResponseValidator.isSessionExpiredResponse(it))
                    DashboardSessionExpiredEventHandler(requireContext()).onSessionExpired()
                fullscreenLoading!!.isVisible = false
                completeRecycler!!.isVisible = true
            }
        )
    }

    private fun showEmptyState(){
        emptyLayout?.isVisible = true
        completeRecycler?.isVisible = false
        createEmptyButton?.isVisible = false
    }

    private fun showDataState(){
        emptyLayout?.isVisible = false
        completeRecycler?.isVisible = true
        createEmptyButton?.isVisible = false
    }
}
