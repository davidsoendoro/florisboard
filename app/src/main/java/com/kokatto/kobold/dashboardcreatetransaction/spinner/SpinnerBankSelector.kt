package com.kokatto.kobold.dashboardcreatetransaction.spinner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.AutoTextModel
import com.kokatto.kobold.api.model.basemodel.BankModel
import com.kokatto.kobold.bank.BankViewModel
import com.kokatto.kobold.dashboardcreatetransaction.SpinnerBankAdapter
import com.kokatto.kobold.dashboardcreatetransaction.SpinnerChannelItem
import com.kokatto.kobold.extension.showToast
import timber.log.Timber

class SpinnerBankSelector : BottomSheetDialogFragment() {

    val TAG = "SpinnerBankSelector"

    fun newInstance(): SpinnerBankSelector? {
        return SpinnerBankSelector()
    }

    var onItemClick: ((BankModel) -> Unit)? = null
    private var title: TextView? = null
    private var recyclerView: RecyclerView? = null
    private var backButton: ImageView? = null

    // get using API Function
    private var selectedOption = BankModel("","","Cash","")
    private var pickOptions = ArrayList<BankModel>()

    private val bankViewModel: BankViewModel? = BankViewModel()

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
        title?.text = "Metode Pembayaran"
        backButton = view.findViewById<ImageView>(R.id.spinner_selector_back_button)
        recyclerView = view.findViewById<RecyclerView>(R.id.spinner_selector_recycler_view)
        recyclerView?.setHasFixedSize(true)
        getBankList()
        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerView?.adapter = SpinnerBankAdapter(this, pickOptions, selectedOption) { result ->
            onItemClick?.invoke(result)
            dismiss()
        }

        val backButton = view.findViewById<View>(R.id.spinner_selector_back_button)
        backButton?.setOnClickListener {
            dismiss()
        }
    }

    fun openSelector(fragmentManager: FragmentManager, selectedItem: BankModel) {
        selectedItem.let { selectedItem ->
            if(selectedItem.accountNo != null && selectedItem.accountNo != ""){
                selectedOption = selectedItem
            }
        }
        this.show(fragmentManager, TAG)
    }

    fun getBankList() {
        bankViewModel?.getPaginated(
            page = 1,
            onLoading = {
                Timber.e(it.toString())
            },
            onSuccess = { it ->
                if (it.data.totalRecord > 0) {
                    pickOptions.add(BankModel("","","Cash",""))
                    pickOptions.addAll(it.data.contents)
                    recyclerView?.adapter?.notifyDataSetChanged()
                }
            },
            onError = {
                showToast(it)
            })
    }

}
