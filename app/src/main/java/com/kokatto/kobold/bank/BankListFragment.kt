package com.kokatto.kobold.bank

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.kokatto.kobold.R
import com.kokatto.kobold.api.impl.DashboardSessionExpiredEventHandler
import com.kokatto.kobold.api.impl.ErrorResponseValidator
import com.kokatto.kobold.api.model.basemodel.BankModel
import com.kokatto.kobold.bank.recylerAdapeter.BankRecyclerAdapter
import com.kokatto.kobold.component.DovesRecyclerViewPaginator
import com.kokatto.kobold.extension.vertical
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

class BankListFragment : Fragment(R.layout.fragment_data_list), BankRecyclerAdapter.OnClick {

    private var recyclerAdapter: BankRecyclerAdapter? = null
    private var recyclerView: RecyclerView? = null

    private var dataViewModel: BankViewModel? = BankViewModel()
    private var dataList: ArrayList<BankModel> = arrayListOf()

    private var bottomLoading: LinearLayout? = null
    private var fullscreenLoading: LinearLayout? = null

    private val isLoadingList = AtomicBoolean(true)
    private val isLast = AtomicBoolean(false)

    var onEmptyResult: ((Boolean) -> Unit)? = null
    var onRowClick: ((BankModel) -> Unit)? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.data_recycler_view)
        bottomLoading = view.findViewById(R.id.bottom_loading)
        fullscreenLoading = view.findViewById(R.id.fullcreen_loading)

        getPaginatedData()
        recyclerAdapter = BankRecyclerAdapter(dataList, this)

        DovesRecyclerViewPaginator(
            recyclerView = recyclerView!!,
            isLoading = { isLoadingList.get() },
            loadMore = {
                bottomLoading!!.isVisible = true
                //showToast(it.toString())
                getPaginatedData(it + 1)
            },
            onLast = { isLast.get() }
        ).run {
            threshold = 3
        }

        recyclerView!!.adapter = recyclerAdapter
        recyclerView!!.vertical()
    }

    override fun onClicked(data: BankModel) {
        onRowClick?.invoke(data)
    }

    private fun getPaginatedData(page: Int = 1) {
        dataViewModel?.getPaginated(
            page = page,
            onLoading = {
                Timber.e(it.toString())
                isLoadingList.set(it)
            },
            onSuccess = { it ->
                if (it.data.totalRecord > 0) {
                    dataList.addAll(it.data.contents)
                    isLast.set(it.data.totalPages <= it.data.page)

                    if (page == 1) {
                        fullscreenLoading!!.isVisible = false
                        recyclerView!!.isVisible = true
                    } else {
                        isLoadingList.set(false)
                        bottomLoading!!.isVisible = false
                    }

                    recyclerAdapter!!.notifyDataSetChanged()
                } else {
                    onEmptyResult?.invoke(true)
                }
            },
            onError = {
                if(ErrorResponseValidator.isSessionExpiredResponse(it))
                    DashboardSessionExpiredEventHandler(requireContext()).onSessionExpired()
                fullscreenLoading!!.isVisible = false
                recyclerView!!.isVisible = true
            }
        )
    }

    fun isReachMaximum() : Boolean {
        return dataList.size >= 15
    }

}
