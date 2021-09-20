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
import com.kokatto.kobold.dashboardcreatetransaction.SpinnerLogisticAdapter
import com.kokatto.kobold.dashboardcreatetransaction.SpinnerLogisticItem

class SpinnerLogisticSelector : BottomSheetDialogFragment() {

    val TAG = "SpinnerLogisticSelector"

    fun newInstance(): SpinnerLogisticSelector? {
        return SpinnerLogisticSelector()
    }

    var onItemClick: ((SpinnerLogisticItem) -> Unit)? = null

    private var title: TextView? = null
    private var recyclerView: RecyclerView? = null
    private var backButton: ImageView? = null

    // Get Data Via API Call
    private var selectedOption = SpinnerLogisticItem("JNE - REG")
    private var pickOptions = arrayOf(
        SpinnerLogisticItem("JNE - REG"),
        SpinnerLogisticItem("JNE - Best"),
        SpinnerLogisticItem("J&T - REG"),
        SpinnerLogisticItem("SiCepat REG"),
        SpinnerLogisticItem("SiCepat Halu"),
        SpinnerLogisticItem("Anteraja - REG"),
        SpinnerLogisticItem("Lion Parcel - REGPACK"),
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
        title?.text = "Pilih Kurir"
        backButton = view.findViewById<ImageView>(R.id.spinner_selector_back_button)
        recyclerView = view.findViewById<RecyclerView>(R.id.spinner_selector_recycler_view)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerView?.adapter = SpinnerLogisticAdapter(this, pickOptions, selectedOption) { result ->
            onItemClick?.invoke(result)
            dismiss()
        }

        val backButton = view.findViewById<View>(R.id.spinner_selector_back_button)
        backButton?.setOnClickListener {
            dismiss()
        }
    }

    fun openSelector(fragmentManager: FragmentManager, selectedItem: SpinnerLogisticItem) {
        selectedItem.let { selectedItem ->
            if (selectedItem.label != null && selectedItem.label != "") {
                selectedOption = selectedItem
            }
        }
        this.show(fragmentManager, TAG)
    }
}
