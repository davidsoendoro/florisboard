package com.kokatto.kobold.registration.spinner

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kokatto.kobold.R
import com.kokatto.kobold.api.impl.DashboardSessionExpiredEventHandler
import com.kokatto.kobold.api.impl.ErrorResponseValidator
import com.kokatto.kobold.api.model.basemodel.BusinessTypeModel
import com.kokatto.kobold.api.model.basemodel.fromIntent
import com.kokatto.kobold.databinding.BottomsheetBusinessTypeBinding
import com.kokatto.kobold.extension.RoundedBottomSheet
import com.kokatto.kobold.registration.viewmodel.MerchantViewModel

class DialogBusinessTypeSelector : RoundedBottomSheet(), DialogBusinessTypeAdapter.OnClick {
    val TAG = "DialogBusinessTypeSelector"

    fun newInstance(): DialogBusinessTypeSelector? {
        return DialogBusinessTypeSelector()
    }

    private var fromSubmitButton = false

    private var mainLayout: LinearLayout? = null
    private var recyclerView: RecyclerView? = null
    private var adapter: DialogBusinessTypeAdapter? = null
    private var backButton: ImageButton? = null
    private var fullscreenLoading: LinearLayout? = null
    private var submitButton: CardView? = null

    var onBusinessTypeClicked: OnBusinessTypeClicked? = null

    private var businessTypeList = ArrayList<BusinessTypeModel>()

    private var merchantViewModel: MerchantViewModel? = MerchantViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return BottomsheetBusinessTypeBinding.inflate(inflater, container, false).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBusinessTypeClicked = context as OnBusinessTypeClicked
        businessTypeList.fromIntent(arguments)

        backButton = view.findViewById(R.id.business_type_back_button)
        fullscreenLoading = view.findViewById(R.id.fullcreen_loading)
        mainLayout = view.findViewById(R.id.main_layout)
        recyclerView = view.findViewById(R.id.spinner_selector_recycler_view)
        submitButton = view.findViewById(R.id.submit_button)

        getBusinessTypeList()

        recyclerView?.layoutManager = LinearLayoutManager(context)
        adapter = DialogBusinessTypeAdapter(businessTypeList, this)
        recyclerView?.adapter = adapter

        backButton?.setOnClickListener {
            dismiss()
        }

        submitButton?.setOnClickListener {
            fromSubmitButton = true

            dismiss()
        }
    }

    private fun getBusinessTypeList() {
        if (businessTypeList.size <= 0) {
            merchantViewModel?.getMerchantBusinessType(
                onLoading = {
                    fullscreenLoading?.isVisible = it
                    mainLayout?.isVisible = it.not()
                },
                onSuccess = {
                    businessTypeList.addAll(it.data)
                    adapter?.notifyDataSetChanged()
                },
                onError = {
                    if(ErrorResponseValidator.isSessionExpiredResponse(it))
                        DashboardSessionExpiredEventHandler(requireContext()).onSessionExpired()
                }
            )
        }

    }

    interface OnBusinessTypeClicked {
        fun onDataBusinessTypePass(data: ArrayList<BusinessTypeModel>)
    }

    override fun onDestroy() {
        merchantViewModel?.onDestroy()
        businessTypeList.clear()
        super.onDestroy()
    }

    override fun onDismiss(dialog: DialogInterface) {
        if (fromSubmitButton.not())
            businessTypeList.fromIntent(arguments)
        else
            onBusinessTypeClicked?.onDataBusinessTypePass(businessTypeList)

        fromSubmitButton = false

        super.onDismiss(dialog)
    }

    override fun onClicked(selected: Boolean, index: Int) {
        businessTypeList.forEach { it.isSelected = false }
        businessTypeList[index].isSelected = selected.not()

        adapter?.notifyDataSetChanged()
    }
}
