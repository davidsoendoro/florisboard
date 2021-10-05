package com.kokatto.kobold.registration.spinner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.PropertiesModel
import com.kokatto.kobold.constant.PropertiesTypeConstant
import com.kokatto.kobold.dashboardcreatetransaction.TransactionViewModel
import com.kokatto.kobold.extension.RoundedBottomSheet
import com.kokatto.kobold.extension.showToast

class SpinnerBusinessFieldSelector : RoundedBottomSheet() {

    val TAG = "SpinnerBusinessFieldSelector"

    fun newInstance(): SpinnerBusinessFieldSelector? {
        return SpinnerBusinessFieldSelector()
    }

    var onItemClick: ((PropertiesModel) -> Unit)? = null

    private var title: TextView? = null
    private var recyclerView: RecyclerView? = null
    private var backButton: ImageButton? = null
    private var fullscreenLoading: LinearLayout? = null
    private var selectedOption = PropertiesModel("", "", "", "")
    private var pickOptions = ArrayList<PropertiesModel>()

    private var transactionViewModel: TransactionViewModel? = TransactionViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottomsheet_business_field, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        title = view.findViewById<TextView>(R.id.spinner_selector_title)
//        title?.text = "Pilih Channel"
//        backButton = view.findViewById<ImageButton>(R.id.spinner_selector_back_button)
//        fullscreenLoading = view.findViewById(R.id.fullcreen_loading)
        recyclerView = view.findViewById<RecyclerView>(R.id.spinner_selector_recycler_view)
        recyclerView?.setHasFixedSize(true)
//        getPropertiesList()
        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerView?.adapter = SpinnerBusinessFieldAdapter()

        val backButton = view.findViewById<View>(R.id.spinner_selector_back_button)
        backButton?.setOnClickListener {
            dismiss()
        }
    }

//    fun openSelector(fragmentManager: FragmentManager, selectedItem: BusinessFieldModel) {
//        selectedItem.let { selectedItem ->
//            if (selectedItem.assetDesc != null && selectedItem.assetDesc != "") {
//                selectedOption = selectedItem
//            }
//        }
//        this.show(fragmentManager, TAG)
//    }

    fun getPropertiesList() {
        if (pickOptions.size <= 0) {
            fullscreenLoading!!.isVisible = true
            transactionViewModel?.getStandardListProperties(
                type = PropertiesTypeConstant.channel,
                onSuccess = { it ->
                    if (it.data.size > 0) {
                        pickOptions.addAll(it.data)
                        recyclerView?.adapter?.notifyDataSetChanged()
                    }
                    fullscreenLoading!!.isVisible = false
                },
                onError = {
                    fullscreenLoading!!.isVisible = false
                    showToast(it)
                })
        }

    }
}
