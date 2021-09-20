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
import com.kokatto.kobold.dashboardcreatetransaction.SpinnerChannelAdapter
import com.kokatto.kobold.dashboardcreatetransaction.SpinnerChannelItem

class SpinnerChannelSelector : BottomSheetDialogFragment() {

    val TAG = "SpinnerChannelSelector"

//    fun newInstance(item : SpinnerChannelItem): SpinnerChannelSelector? {
//        return SpinnerChannelSelector().apply {
//            arguments = Bundle().apply {
//                putSerializable("item", item)
//            }
//        }
//    }

    fun newInstance(): SpinnerChannelSelector? {
        return SpinnerChannelSelector()
    }

    var onItemClick: ((SpinnerChannelItem) -> Unit)? = null

    private var title: TextView? = null
    private var recyclerView: RecyclerView? = null
    private var backButton: ImageView? = null

    // Get Data Via API Call
    private var selectedOption = SpinnerChannelItem("Belum Ada")
    private var pickOptions = arrayOf(
        SpinnerChannelItem("Belum Ada"),
        SpinnerChannelItem("WhatsApp"),
        SpinnerChannelItem("WhatsApp Business"),
        SpinnerChannelItem("Line"),
        SpinnerChannelItem("Facebook Messenger"),
        SpinnerChannelItem("Bukalapak Chat"),
        SpinnerChannelItem("Tokopedia Chat"),
        SpinnerChannelItem("Shopee Chat"),
    )

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

    fun openSelector(fragmentManager: FragmentManager, selectedItem: SpinnerChannelItem) {
        selectedItem.let { selectedItem ->
            if(selectedItem.label != null && selectedItem.label != ""){
                selectedOption = selectedItem
            }
        }
        this.show(fragmentManager, TAG)
    }
}
