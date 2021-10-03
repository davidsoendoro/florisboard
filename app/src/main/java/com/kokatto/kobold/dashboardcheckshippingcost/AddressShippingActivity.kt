package com.kokatto.kobold.dashboardcheckshippingcost

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.DeliveryAddressModel
import com.kokatto.kobold.checkshippingcost.ShippingCostViewModel
import com.kokatto.kobold.constant.ActivityConstantCode
import com.kokatto.kobold.dashboardcheckshippingcost.adapter.ShippingAddressRecyclerAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.CoroutineContext

class AddressShippingActivity : AppCompatActivity(), CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.Main

    companion object {
        val SENDER = "SENDER"
        val RECEIVER = "RECEIVER"
    }

    private var buttonBack: ImageButton? = null
    private var buttonClear: ImageView? = null
    private var layoutEmpty: LinearLayout? = null
    private var layoutNotFound: LinearLayout? = null
    private var searchEdittext: EditText? = null
    private var fullscreenLoading: LinearLayout? = null

    private var dataList: ArrayList<DeliveryAddressModel> = arrayListOf()
    private var recyclerView: RecyclerView? = null
    private var recyclerAdapter: ShippingAddressRecyclerAdapter? = null
    private var shippingCostViewModel: ShippingCostViewModel? = ShippingCostViewModel()
    private val isLoading = AtomicBoolean(true)
    private var mode: String? = null

    private var textChangedJob: Job? = null
    private lateinit var textListener: TextWatcher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_address)

        buttonBack = findViewById(R.id.back_button)
        buttonClear = findViewById(R.id.clear_button)
        searchEdittext = findViewById(R.id.search_edittext)
        layoutEmpty = findViewById(R.id.layout_empty)
        layoutNotFound = findViewById(R.id.layout_not_found)
        fullscreenLoading = findViewById(R.id.fullscreen_loading)
        recyclerView = findViewById(R.id.recycler_view)

        buttonBack?.setOnClickListener {
            onBackPressed()
        }

        buttonClear?.setOnClickListener {
            searchEdittext?.setText("")
        }

        recyclerView?.layoutManager = LinearLayoutManager(this)
        recyclerAdapter = ShippingAddressRecyclerAdapter(this, dataList)
        recyclerView?.adapter = recyclerAdapter

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

        textListener = object : TextWatcher {
            private var searchFor = searchEdittext?.text.toString()

            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchText = s.toString().trim()
                if (searchText != searchFor) {
                    searchFor = searchText
                    buttonClear?.isVisible = true
                    textChangedJob?.cancel()
                    textChangedJob = launch(Dispatchers.Main) {
                        delay(700L)
                        if (searchText == searchFor) {
                            callAPIShippingAddress(searchText)
                        }
                    }
                }
            }
        }

        initialView()
    }

    override fun onResume() {
        super.onResume()
        searchEdittext?.addTextChangedListener(textListener)
    }

    override fun onPause() {
        searchEdittext?.removeTextChangedListener(textListener)
        super.onPause()
    }

    override fun onDestroy() {
        textChangedJob?.cancel()
        super.onDestroy()
    }

    fun initialView() {
        mode = intent.getStringExtra(ActivityConstantCode.EXTRA_MODE)
        fullscreenLoading?.isVisible = false
        recyclerView?.isVisible = false
        layoutEmpty?.isVisible = true
        layoutNotFound?.isVisible = false
        buttonClear?.isVisible = false
    }

    private fun callAPIShippingAddress(address: String = "") {
        layoutEmpty?.isVisible = false
        layoutNotFound?.isVisible = false
        recyclerView?.isVisible = false
        fullscreenLoading?.isVisible = true

        dataList.clear()
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

                    recyclerView?.isVisible = true
                    layoutEmpty?.isVisible = false
                    layoutNotFound?.isVisible = false
                } else {
                    layoutNotFound?.isVisible = true
                    recyclerView?.isVisible = false
                    layoutEmpty?.isVisible = false
                }

                fullscreenLoading?.isVisible = false
            },
            onError = {
                fullscreenLoading?.isVisible = false
                recyclerView?.isVisible = false
                layoutEmpty?.isVisible = false
                layoutNotFound?.isVisible = true
            }
        )
    }

//    private fun <T> debounce(
//        waitMs: Long = 300L,
//        scope: CoroutineScope,
//        destinationFunction: (T) -> Unit
//    ): (T) -> Unit {
//        var debounceJob: Job? = null
//        return { param: T ->
//            debounceJob?.cancel()
//            debounceJob = scope.launch {
//                delay(waitMs)
//                destinationFunction(param)
//            }
//        }
//    }
}
