package com.kokatto.kobold.dashboardcreatetransaction.spinner

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
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
import com.kokatto.kobold.bank.BankInputActivity
import com.kokatto.kobold.bank.BankViewModel
import com.kokatto.kobold.constant.ActivityConstantCode
import com.kokatto.kobold.constant.ActivityConstantCode.Companion.BANK_TYPE_OTHER
import com.kokatto.kobold.constant.ActivityConstantCode.Companion.CASH
import com.kokatto.kobold.dashboardcreatetransaction.SpinnerBankAdapter
import com.kokatto.kobold.extension.RoundedBottomSheet
import com.kokatto.kobold.extension.showToast
import dev.patrickgold.florisboard.setup.SetupActivity
import timber.log.Timber

class SpinnerBankSelector : RoundedBottomSheet() {

    val TAG = "SpinnerBankSelector"

    fun newInstance(): SpinnerBankSelector {
        return SpinnerBankSelector()
    }

    var onItemClick: ((BankModel) -> Unit)? = null
    private var title: TextView? = null
    private var recyclerView: RecyclerView? = null
    private var backButton: ImageButton? = null
    private var bottomActionButton: CardView? = null
    private var fullscreenLoading: LinearLayout? = null

    // get using API Function
    private var selectedOption : BankModel? = null
    private var defaultBankCash = BankModel("",BANK_TYPE_OTHER, CASH,"", "", "")
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

        val params: ViewGroup.LayoutParams? = recyclerView?.layoutParams
        params?.height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 240f, resources.displayMetrics).toInt()
        recyclerView?.layoutParams = params

        recyclerView?.adapter = SpinnerBankAdapter(this, pickOptions, selectedOption!!) { result, type ->

            if(type == "onclick"){
                onItemClick?.invoke(result)
                dismiss()
            } else {
                val intent = Intent(activity, BankInputActivity::class.java)
                intent.putExtra(ActivityConstantCode.EXTRA_DATA, result)
                intent.putExtra(ActivityConstantCode.EXTRA_MODE, ActivityConstantCode.EXTRA_EDIT)
                startActivity(intent)
                dismiss()
            }
        }

        backButton?.setOnClickListener {
            dismiss()
        }

        bottomActionButton?.setOnClickListener {

            //validate max 15 bank account
            if(pickOptions.size >=15){
                showToast(R.string.kobold_bank_maximum)
            } else {
                val intent = Intent(activity, BankInputActivity::class.java)
                intent.putExtra(ActivityConstantCode.EXTRA_MODE, ActivityConstantCode.EXTRA_CREATE)
                startActivity(intent)
                dismiss()
            }
        }
    }

    fun openSelector(fragmentManager: FragmentManager, selectedItem: BankModel) {
        selectedOption = selectedItem
        recyclerView?.adapter?.notifyDataSetChanged()
        this.show(fragmentManager, TAG)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun getBankList() {
        fullscreenLoading!!.isVisible = true
        bankViewModel.getPaginated(
            page = 1,
            pageSize = 15,
            onLoading = {
                Timber.e(it.toString())
            },
            onSuccess = {
                pickOptions.add(defaultBankCash) // adding cash for default payment

                if (it.data.totalRecord > 0) {
                    pickOptions.addAll(it.data.contents)
                }
                recyclerView?.adapter?.notifyDataSetChanged()
                fullscreenLoading!!.isVisible = false

            },
            onError = {
                fullscreenLoading!!.isVisible = false
                showToast(it)
            })
    }

}
