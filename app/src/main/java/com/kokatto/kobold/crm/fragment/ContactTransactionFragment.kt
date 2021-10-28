package com.kokatto.kobold.crm.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.kokatto.kobold.R
import com.kokatto.kobold.api.impl.DashboardSessionExpiredEventHandler
import com.kokatto.kobold.api.impl.ErrorResponseValidator
import com.kokatto.kobold.api.model.basemodel.ContactModel
import com.kokatto.kobold.api.model.basemodel.TransactionModel
import com.kokatto.kobold.component.DovesRecyclerViewPaginator
import com.kokatto.kobold.constant.ActivityConstantCode
import com.kokatto.kobold.constant.TransactionFilterEnum
import com.kokatto.kobold.crm.adapter.TransactionFilterRecycleAdapter
import com.kokatto.kobold.dashboardcreatetransaction.DetailActivity
import com.kokatto.kobold.dashboardcreatetransaction.InputActivity
import com.kokatto.kobold.dashboardcreatetransaction.TransactionViewModel
import com.kokatto.kobold.dashboardcreatetransaction.recycleradapter.TransactionHomeRecyclerAdapter
import com.kokatto.kobold.databinding.FragmentContactTransactionBinding
import com.kokatto.kobold.extension.horizontal
import com.kokatto.kobold.extension.showSnackBar
import java.util.concurrent.atomic.AtomicBoolean

class ContactTransactionFragment(val contact: ContactModel?) : Fragment(), TransactionHomeRecyclerAdapter.OnClick {

    private lateinit var binding: FragmentContactTransactionBinding

    private var recyclerAdapter: TransactionHomeRecyclerAdapter? = null
    private var recyclerFilterAdapter: TransactionFilterRecycleAdapter? = null
    private var transactionViewModel: TransactionViewModel? = TransactionViewModel()
    private var transactionList: ArrayList<TransactionModel> = arrayListOf()

    private val isLoadingList = AtomicBoolean(true)
    private val isLast = AtomicBoolean(false)

    private var selectedFilter: TransactionFilterEnum? = null
    private var filters: MutableList<TransactionFilterEnum> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContactTransactionBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        DovesRecyclerViewPaginator(
            recyclerView = binding.recyclerView,
            isLoading = { isLoadingList.get() },
            loadMore = {
                callApiFetchTransaction(it + 1)
            },
            onLast = { isLast.get() }
        ).run {
            threshold = 3
        }

        binding.layoutEmptyState.emptyCreateButton.setOnClickListener {
            requireActivity().run {
                startActivity(createInputActivityIntentWithContactDataExtras())
                finish()
            }
        }

        binding.createTransactionButton.setOnClickListener {
            requireActivity().run {
                startActivity(createInputActivityIntentWithContactDataExtras())
                finish()
            }
        }

        callApiFetchTransaction()
        initFilter()
    }

    private fun createInputActivityIntentWithContactDataExtras(): Intent{
        val intent = Intent(requireActivity(), InputActivity::class.java)
        intent.putExtra(ActivityConstantCode.EXTRA_DATA, contact)
        return intent
    }

    private fun callApiFetchTransaction(page: Int = 1, status: String = "", filterMode: Boolean = false) {
        binding.recyclerView.visibility = View.VISIBLE
        binding.layoutEmptyStateOnData.layoutEmptyRoot.visibility = View.GONE
        binding.createTransactionButton.visibility = View.GONE

        transactionViewModel?.getTransactionList(
            page = page,
            status = status,
            contact = contact!!._id,
            onLoading = {
                binding.layoutFullscreenLoading.isVisible = it
            },
            onSuccess = { it ->
                binding.textTotalTrx.text = HtmlCompat.fromHtml(
                    String.format("Total <b>%s pesanan</b>", it.data.totalRecord), HtmlCompat.FROM_HTML_MODE_LEGACY
                )

                if (page <= 1) {
                    transactionList.clear()
                }

                if (it.data.totalRecord > 0) {
                    transactionList.addAll(it.data.contents)

                    if (filterMode.not()) {
                        showDataState()
                    }

                } else {
                    if (filterMode.not()) {
                        showEmptyState()
                    } else {
                        transactionList.clear()
                        binding.recyclerView.visibility = View.GONE
                        binding.layoutEmptyStateOnData.layoutEmptyRoot.visibility = View.VISIBLE
                    }
                }

                bindAdapterContact(transactionList)
            },
            onError = {
                if (ErrorResponseValidator.isSessionExpiredResponse(it)) {
                    DashboardSessionExpiredEventHandler(requireContext()).onSessionExpired()
                } else {
                    showSnackBar(it, R.color.snackbar_error)
                }
            }
        )
    }

    private fun showDataState() {
        binding.layoutDataState.visibility = View.VISIBLE
        binding.layoutEmptyState.layoutEmptyRoot.visibility = View.GONE
        binding.createTransactionButton.visibility = View.VISIBLE
    }

    private fun showEmptyState() {
        binding.layoutEmptyState.layoutEmptyRoot.visibility = View.VISIBLE
        binding.layoutEmptyState.emptyCreateButton.visibility = View.VISIBLE
        binding.layoutDataState.visibility = View.GONE
        binding.createTransactionButton.visibility = View.GONE
    }

    private fun bindAdapterContact(list: ArrayList<TransactionModel>) {
        recyclerAdapter = TransactionHomeRecyclerAdapter(list, this, true)
        binding.recyclerView.adapter = recyclerAdapter
        recyclerAdapter!!.notifyDataSetChanged()
    }

    override fun onClicked(data: TransactionModel) {
        requireActivity().run {
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_DATA, data)
            startActivity(intent)
        }
    }

    private fun initFilter() {
        filters.add(TransactionFilterEnum.ALL)
        filters.add(TransactionFilterEnum.PENDING)
        filters.add(TransactionFilterEnum.PAID)
        filters.add(TransactionFilterEnum.SENT)
        //filters.add(TransactionFilterEnum.CANCEL)
        //filters.add(TransactionFilterEnum.COMPLETE)

        selectedFilter = TransactionFilterEnum.ALL

        recyclerFilterAdapter = TransactionFilterRecycleAdapter(selectedFilter, filters)
        binding.recyclerViewFilter.horizontal()
        binding.recyclerViewFilter.adapter = recyclerFilterAdapter

        recyclerFilterAdapter!!.onItemClick = {
            callApiFetchTransaction(1, it.code, true)
        }

    }
}
