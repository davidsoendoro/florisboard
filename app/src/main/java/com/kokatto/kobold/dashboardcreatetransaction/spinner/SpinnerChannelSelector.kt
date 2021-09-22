package com.kokatto.kobold.dashboardcreatetransaction.spinner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.PropertiesModel
import com.kokatto.kobold.constant.PropertiesTypeConstant
import com.kokatto.kobold.dashboardcreatetransaction.SpinnerChannelAdapter
import com.kokatto.kobold.dashboardcreatetransaction.SpinnerChannelItem
import com.kokatto.kobold.dashboardcreatetransaction.TransactionViewModel
import com.kokatto.kobold.extension.showToast

class SpinnerChannelSelector : BottomSheetDialogFragment() {

    val TAG = "SpinnerChannelSelector"

    fun newInstance(): SpinnerChannelSelector? {
        return SpinnerChannelSelector()
    }

    var onItemClick: ((PropertiesModel) -> Unit)? = null

    private var title: TextView? = null
    private var recyclerView: RecyclerView? = null
    private var backButton: ImageView? = null

    // get using API Function
    private var selectedOption =  PropertiesModel("","","","")
    private var pickOptions = ArrayList<PropertiesModel>()

    private var transactionViewModel: TransactionViewModel? = TransactionViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.item_spinner_form_selector, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        title = view.findViewById<TextView>(R.id.spinner_selector_title)
        title?.text = "Pilih Channel"
        backButton = view.findViewById<ImageView>(R.id.spinner_selector_back_button)
        recyclerView = view.findViewById<RecyclerView>(R.id.spinner_selector_recycler_view)
        recyclerView?.setHasFixedSize(true)
        getPropertiesList()
        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerView?.adapter = SpinnerChannelAdapter(this, pickOptions, selectedOption) { result ->
            onItemClick?.invoke(result)
            dismiss()
        }

        val backButton = view.findViewById<View>(R.id.spinner_selector_back_button)
        backButton?.setOnClickListener {
            dismiss()
        }
    }

    fun openSelector(fragmentManager: FragmentManager, selectedItem: PropertiesModel) {
        selectedItem.let { selectedItem ->
            if(selectedItem.assetDesc != null && selectedItem.assetDesc != ""){
                selectedOption = selectedItem
            }
        }
        this.show(fragmentManager, TAG)
    }

    fun getPropertiesList() {
        if(pickOptions.size <= 0) {
            transactionViewModel?.getStandardListProperties(
                type = PropertiesTypeConstant.channel,
                onSuccess = { it ->
                    if (it.data.size > 0) {
                        pickOptions.addAll(it.data)
                        recyclerView?.adapter?.notifyDataSetChanged()
                    }
                },
                onError = {
                    showToast(it)
                })
        }

    }
}
