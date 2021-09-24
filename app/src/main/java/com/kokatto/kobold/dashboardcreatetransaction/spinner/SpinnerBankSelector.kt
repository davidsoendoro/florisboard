package com.kokatto.kobold.dashboardcreatetransaction.spinner

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.BankModel
import com.kokatto.kobold.bank.BankViewModel
import com.kokatto.kobold.constant.ActivityConstantCode.Companion.BANK_TYPE_OTHER
import com.kokatto.kobold.constant.ActivityConstantCode.Companion.CASH
import com.kokatto.kobold.dashboardcreatetransaction.SpinnerBankAdapter
import com.kokatto.kobold.extension.RoundedBottomSheet
import com.kokatto.kobold.extension.showToast
import timber.log.Timber

class SpinnerBankSelector : RoundedBottomSheet() {

    val TAG = "SpinnerBankSelector"

    fun newInstance(): SpinnerBankSelector {
        return SpinnerBankSelector()
    }

    var onItemClick: ((BankModel) -> Unit)? = null
    private var title: TextView? = null
    private var recyclerView: RecyclerView? = null
    private var backButton: ImageView? = null
    private var bottomActionButton: CardView? = null
    private var fullscreenLoading: LinearLayout? = null

    // get using API Function
    private var selectedOption = BankModel("",BANK_TYPE_OTHER, CASH,"", "", "")
    private var pickOptions = ArrayList<BankModel>()

    private val bankViewModel: BankViewModel = BankViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.item_spinner_form_selector, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        title = view.findViewById(R.id.spinner_selector_title)
        title?.text = "Metode Pembayaran"
        backButton = view.findViewById(R.id.spinner_selector_back_button)
        bottomActionButton = view.findViewById(R.id.spinner_selector_bottom_action)
        bottomActionButton?.visibility = View.VISIBLE
        recyclerView = view.findViewById(R.id.spinner_selector_recycler_view)
        fullscreenLoading = view.findViewById(R.id.fullcreen_loading)
        recyclerView?.setHasFixedSize(true)
        getBankList()
        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerView?.adapter = SpinnerBankAdapter(this, pickOptions, selectedOption) { result ->
            onItemClick?.invoke(result)
            dismiss()
        }

        backButton?.setOnClickListener {
            dismiss()
        }

        bottomActionButton?.setOnClickListener {
            // open intent create bank
            showToast("open Create Bank")
        }
    }

    fun openSelector(fragmentManager: FragmentManager, selectedItem: BankModel) {
        selectedItem.let {
            if(it.accountNo.isNotBlank()){
                selectedOption = selectedItem
            }
        }
        this.show(fragmentManager, TAG)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun getBankList() {
        fullscreenLoading!!.isVisible = true
        bankViewModel.getPaginated(
            page = 1,
            onLoading = {
                Timber.e(it.toString())
            },
            onSuccess = {
                if (it.data.totalRecord > 0) {
                    pickOptions.add(BankModel("", BANK_TYPE_OTHER, CASH,"Cash","", ""))
                    pickOptions.addAll(it.data.contents)
                    recyclerView?.adapter?.notifyDataSetChanged()
                    fullscreenLoading!!.isVisible = false
                }
            },
            onError = {
                fullscreenLoading!!.isVisible = false
                showToast(it)
            })
    }

}
