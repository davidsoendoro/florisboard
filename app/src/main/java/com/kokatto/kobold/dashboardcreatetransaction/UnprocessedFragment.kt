package com.kokatto.kobold.dashboardcreatetransaction

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.TransactionModel
import com.kokatto.kobold.component.DovesRecyclerViewPaginator
import com.kokatto.kobold.constant.ActivityConstantCode
import com.kokatto.kobold.constant.TransactionStatusConstant
import com.kokatto.kobold.dashboardcreatetransaction.recycleradapter.TransactionHomeRecyclerAdapter
import com.kokatto.kobold.extension.showToast
import com.kokatto.kobold.extension.vertical
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

class UnprocessedFragment : Fragment(R.layout.fragment_unprocessed), TransactionHomeRecyclerAdapter.OnClick {

    private var unprocessedRecyclerAdapter: TransactionHomeRecyclerAdapter? = null
    private var unprocesedRecycler: RecyclerView? = null

    private var transactionViewModel: TransactionViewModel? = TransactionViewModel()
    private var transactionList: ArrayList<TransactionModel> = arrayListOf()

    private var bottomLoading: LinearLayout? = null
    private var fullscreenLoading: LinearLayout? = null

    private val isLoadingList = AtomicBoolean(true)
    private val isLast = AtomicBoolean(false)
    private var launchActivity: ActivityResultLauncher<Intent>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        unprocesedRecycler = view.findViewById(R.id.recycler_view)
        bottomLoading = view.findViewById(R.id.bottom_loading)
        fullscreenLoading = view.findViewById(R.id.fullcreen_loading)

        getUnprocessedTransactionList()
        unprocessedRecyclerAdapter = TransactionHomeRecyclerAdapter(transactionList, this)

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

    override fun onClicked(data: TransactionModel) {
        val intent = Intent(requireContext(), DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_DATA, data)
        launchActivity?.launch(intent)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        launchActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == ActivityConstantCode.STATUS_TO_CANCEL
                || result.resultCode == ActivityConstantCode.STATUS_TO_PAID) {
                val data: Intent? = result.data
                transactionList.remove(data?.getParcelableExtra<TransactionModel>(ActivityConstantCode.EXTRA_DATA))
                unprocessedRecyclerAdapter!!.notifyDataSetChanged()
                showToast(resources.getString(R.string.kobold_transaction_cancel_toast))
            }
        }
    }

    private fun getUnprocessedTransactionList(page: Int = 1) {
        transactionViewModel?.getTransactionList(
            page = page,
            status = TransactionStatusConstant.PENDING,
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
