package com.kokatto.kobold.registration.spinner

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kokatto.kobold.R
import com.kokatto.kobold.api.impl.DashboardSessionExpiredEventHandler
import com.kokatto.kobold.api.impl.ErrorResponseValidator
import com.kokatto.kobold.api.model.basemodel.BusinessFieldModel
import com.kokatto.kobold.api.model.basemodel.fromIntent
import com.kokatto.kobold.databinding.BottomsheetBusinessFieldBinding
import com.kokatto.kobold.extension.RoundedBottomSheet
import com.kokatto.kobold.extension.showToast
import com.kokatto.kobold.registration.viewmodel.MerchantViewModel

class DialogBusinessFieldSelector : RoundedBottomSheet(), DialogBusinessFieldAdapter.OnClick {
    val TAG = "DialogBusinessFieldSelector"

    fun newInstance(): DialogBusinessFieldSelector? {
        return DialogBusinessFieldSelector()
    }

    private var fromSubmitButton = false

    private var mainLayout: LinearLayout? = null
    private var recyclerView: RecyclerView? = null
    private var adapter: DialogBusinessFieldAdapter? = null
    private var backButton: ImageView? = null
    private var fullscreenLoading: LinearLayout? = null
    private var submitButton: CardView? = null

    var onBusinessFieldClicked: OnBusinessFieldClicked? = null

    private var businessFieldList = ArrayList<BusinessFieldModel>()

    private var merchantViewModel: MerchantViewModel? = MerchantViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return BottomsheetBusinessFieldBinding.inflate(inflater, container, false).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBusinessFieldClicked = context as OnBusinessFieldClicked
        businessFieldList.fromIntent(arguments)

        val titleLayout = view.findViewById<ConstraintLayout>(R.id.title_layout)
        backButton = titleLayout.findViewById(R.id.business_field_selector_back_button)
        fullscreenLoading = view.findViewById(R.id.fullcreen_loading)
        mainLayout = view.findViewById(R.id.main_layout)
        recyclerView = view.findViewById(R.id.spinner_selector_recycler_view)
        submitButton = view.findViewById(R.id.submit_button)

        getBusinessFieldList()

        recyclerView?.layoutManager = LinearLayoutManager(context)
        adapter = DialogBusinessFieldAdapter(businessFieldList, this)
        recyclerView?.adapter = adapter

        backButton?.setOnClickListener {
            dismiss()
        }

        submitButton?.setOnClickListener {
            fromSubmitButton = true

            dismiss()
        }
    }

    private fun getBusinessFieldList() {
        if (businessFieldList.size <= 0) {

            merchantViewModel?.getMerchantBusinessField(
                onLoading = {
                    fullscreenLoading?.isVisible = it
                    mainLayout?.isVisible = it.not()
                },
                onSuccess = {
                    businessFieldList.addAll(it.data)
                    adapter?.notifyDataSetChanged()
                },
                onError = {
                    if(ErrorResponseValidator.isSessionExpiredResponse(it))
                        DashboardSessionExpiredEventHandler(requireContext()).onSessionExpired()
                }
            )
        }
    }

    interface OnBusinessFieldClicked {
        fun onDataBusinessFieldPass(data: ArrayList<BusinessFieldModel>)
    }

    override fun onDestroy() {
        merchantViewModel?.onDestroy()
        businessFieldList.clear()
        super.onDestroy()
    }

    override fun onDismiss(dialog: DialogInterface) {
        if (fromSubmitButton.not()) {
            businessFieldList.fromIntent(arguments)
        } else
            onBusinessFieldClicked?.onDataBusinessFieldPass(businessFieldList)

        fromSubmitButton = false

        super.onDismiss(dialog)
    }

    override fun onClicked(selected: Boolean, index: Int) {
        if (businessFieldList.filter { it.isSelected }.size < 3 || selected) {
            businessFieldList[index].isSelected = selected.not()
        } else {
            showToast("Anda hanya dapat memilih maksimal 3 bidang bisnis")
        }

        adapter?.notifyDataSetChanged()
    }
}
