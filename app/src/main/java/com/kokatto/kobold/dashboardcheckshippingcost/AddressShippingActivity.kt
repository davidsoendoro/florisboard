package com.kokatto.kobold.dashboardcheckshippingcost

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.kokatto.kobold.api.model.basemodel.DeliveryAddressModel
import com.kokatto.kobold.checkshippingcost.ShippingCostViewModel
import com.kokatto.kobold.constant.ActivityConstantCode
import com.kokatto.kobold.dashboardcheckshippingcost.adapter.ShippingAddressRecyclerAdapter
import com.kokatto.kobold.databinding.ActivitySearchAddressBinding
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

class AddressShippingActivity : AppCompatActivity() {

    companion object {
        val SENDER = "SENDER"
        val RECEIVER = "RECEIVER"
    }

    lateinit var uiBinding: ActivitySearchAddressBinding

    private var dataList: ArrayList<DeliveryAddressModel> = arrayListOf()
    private var recyclerAdapter: ShippingAddressRecyclerAdapter? = null
    private var shippingCostViewModel: ShippingCostViewModel? = ShippingCostViewModel()
    private val isLoading = AtomicBoolean(true)
    private var mode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uiBinding = ActivitySearchAddressBinding.inflate(layoutInflater).apply {
            setContentView(root)
        }

        uiBinding.backButton.setOnClickListener {
            onBackPressed()
        }

        uiBinding.clearButton.setOnClickListener {

        }

        uiBinding.recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerAdapter = ShippingAddressRecyclerAdapter(this, dataList)
        uiBinding.recyclerView.adapter = recyclerAdapter

        recyclerAdapter?.onItemClick = {
            when (mode) {
                SENDER -> {
                    val intent = Intent()
                    intent.putExtra(ActivityConstantCode.EXTRA_DATA, it)
                    setResult(ActivityConstantCode.RESULT_SENDER, intent)
                    finish()
                }
                RECEIVER -> {
                    val intent = Intent()
                    intent.putExtra(ActivityConstantCode.EXTRA_DATA, it)
                    setResult(ActivityConstantCode.RESULT_RECEIVER, intent)
                    finish()
                }
            }
        }

        callAPIShippingAddress()
        initialView()
    }

    fun initialView() {
        mode = intent.getStringExtra(ActivityConstantCode.EXTRA_MODE)
        uiBinding.fullcreenLoading.isVisible = false
        uiBinding.recyclerView.isVisible = false
        uiBinding.searchResultNotFoundLayout.isVisible = true
    }

    private fun callAPIShippingAddress(address: String = "") {
        uiBinding.searchResultNotFoundLayout.isVisible = false
        uiBinding.fullcreenLoading.isVisible = true

        shippingCostViewModel?.getPaginatedDeliveryAddress(
            page = 1,
            pageSize = 15,
            onLoading = {
                Timber.e(it.toString())
                isLoading.set(it)
            },
            search = address,
            onSuccess = { it ->
                if (it.data.contents.size > 0) {
                    dataList.addAll(it.data.contents)
                    recyclerAdapter!!.notifyDataSetChanged()

                    uiBinding.recyclerView.isVisible = true
                    uiBinding.searchResultNotFoundLayout.isVisible = false
                }

                uiBinding.fullcreenLoading.isVisible = false
            },
            onError = {
                uiBinding.fullcreenLoading.isVisible = false
                uiBinding.recyclerView.isVisible = false
                uiBinding.searchResultNotFoundLayout.isVisible = true
            }
        )
    }

}
