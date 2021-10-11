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

class PaidFragment: Fragment(R.layout.fragment_paid) , TransactionHomeRecyclerAdapter.OnClick {

    private var paidRecyclerAdapter: TransactionHomeRecyclerAdapter? = null
    private var paidRecycler: RecyclerView? = null

    private var transactionViewModel: TransactionViewModel? = TransactionViewModel()
    private var transactionList: ArrayList<TransactionModel> = arrayListOf()

    private var bottomLoading: LinearLayout? = null
    private var fullscreenLoading: LinearLayout? = null

    private var emptyLayout: ConstraintLayout? = null
    private var createEmptyButton: Button? = null

    private val isLoadingList = AtomicBoolean(true)
    private val isLast = AtomicBoolean(false)

    private var createTransactionActivityListener: CreateTransactionActivityListener? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        paidRecycler = view.findViewById(R.id.recycler_view)
        bottomLoading = view.findViewById(R.id.bottom_loading)
        fullscreenLoading = view.findViewById(R.id.fullcreen_loading)

        getPaidTransactionList()

        paidRecyclerAdapter = TransactionHomeRecyclerAdapter(transactionList, this)

        DovesRecyclerViewPaginator(
            recyclerView = paidRecycler!!,
            isLoading = { isLoadingList.get() },
            loadMore = {
                bottomLoading!!.isVisible = true
                //showToast(it.toString())
                getPaidTransactionList(it + 1)
            },
            onLast = { isLast.get() }
        ).run {
            threshold = 3
        }

        paidRecycler!!.adapter = paidRecyclerAdapter
        paidRecycler!!.vertical()

        emptyLayout = view.findViewById(R.id.pending_layout_empty)
        createEmptyButton = view.findViewById(R.id.empty_create_button)

        createEmptyButton!!.setOnClickListener{
            createTransactionActivityListener?.openInputActivity()
        }
    }

    override fun onClicked(data: TransactionModel) {
        createTransactionActivityListener?.openDetailActivity(data)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            createTransactionActivityListener = context as CreateTransactionActivityListener
        } catch (castException: ClassCastException) {
            // Listener cannot be attached
        }
    }

//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//
//        launchActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//            if (result.resultCode == ActivityConstantCode.STATUS_TO_CANCEL
//                || result.resultCode == ActivityConstantCode.STATUS_TO_SENT) {
//                val data: Intent? = result.data
//                transactionList.remove(data?.getParcelableExtra<TransactionModel>(ActivityConstantCode.EXTRA_DATA))
//                paidRecyclerAdapter!!.notifyDataSetChanged()
//            }
//        }
//    }

    private fun getPaidTransactionList(page: Int = 1) {
        transactionViewModel?.getTransactionList(
            page = page,
            status = TransactionStatusConstant.PAID,
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
                    paidRecycler!!.isVisible = true
                } else {
                    isLoadingList.set(false)
                    bottomLoading!!.isVisible = false
                }
                //contoh insert data
//                    autoTextDatabase?.autoTextDao()?.insertAutoText(it.data.contents[1])
                paidRecyclerAdapter!!.notifyDataSetChanged()
            },
            onError = {
                if(ErrorResponseValidator.isSessionExpiredResponse(it))
                    DashboardSessionExpiredEventHandler(requireContext()).onSessionExpired()
                fullscreenLoading!!.isVisible = false
                paidRecycler!!.isVisible = true
            }
        )
    }

    private fun showEmptyState(){
        emptyLayout?.isVisible = true
        paidRecycler?.isVisible = false
        //createEmptyButton?.isVisible = createTransactionActivityListener?.getHasTransactionn() != true
    }

    private fun showDataState(){
        emptyLayout?.isVisible = false
        paidRecycler?.isVisible = true
        createTransactionActivityListener?.setHasTransaction(true)
    }

}
