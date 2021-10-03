package com.kokatto.kobold.checkshippingcost

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.DeliveryAddressModel
import com.kokatto.kobold.api.model.basemodel.DeliveryFeeModel
import com.kokatto.kobold.api.model.basemodel.format
import com.kokatto.kobold.api.model.basemodel.toText
import com.kokatto.kobold.checkshippingcost.recycleradapter.ChooseCourierRecyclerAdapter
import com.kokatto.kobold.extension.showSnackBar
import com.kokatto.kobold.extension.vertical
import dev.patrickgold.florisboard.ime.core.FlorisBoard
import timber.log.Timber

class KeyboardChooseShippingCost : ConstraintLayout, ChooseCourierRecyclerAdapter.OnClick {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val florisboard: FlorisBoard? = FlorisBoard.getInstanceOrNull()

    private var shippingCostViewModel: ShippingCostViewModel? = ShippingCostViewModel()

    var chooseCourierList = arrayListOf<DeliveryFeeModel>()

    var infoBannerLayout: LinearLayout? = null
    var closeInfoButton: ImageView? = null
    var fullscreenLoading: LinearLayout? = null
    var chooseCourierRecyclerView: RecyclerView? = null
    var backButton: TextView? = null
    var submitButton: Button? = null

    var adapter: ChooseCourierRecyclerAdapter? = null

    var senderAddress: DeliveryAddressModel? = null
    var receiverAddress: DeliveryAddressModel? = null
    var weight: Int = 0

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        infoBannerLayout = findViewById(R.id.info_banner_layout)
        closeInfoButton = findViewById(R.id.close_info_button)
        fullscreenLoading = findViewById(R.id.fullcreen_loading)
        chooseCourierRecyclerView = findViewById(R.id.choose_courier_recyclerview)
        backButton = findViewById(R.id.back_button)
        submitButton = findViewById(R.id.submit_button)

        closeInfoButton?.setOnClickListener {
            infoBannerLayout?.isVisible = false
        }

        adapter = ChooseCourierRecyclerAdapter(chooseCourierList, this)
        chooseCourierRecyclerView?.adapter = adapter
        chooseCourierRecyclerView?.vertical()

        backButton?.setOnClickListener {
            florisboard?.setActiveInput(R.id.kobold_menu_check_shippingcost)
        }

        submitButton?.setOnClickListener {
            val tempString = chooseCourierList.toText()

            if (tempString == "")
                showSnackBar("Anda belum memilih info ongkir")
            else {
                showSnackBar(context.getString(R.string.kobold_submit_check_shippingcost))

                florisboard?.inputFeedbackManager?.keyPress()
                florisboard?.textInputManager?.activeEditorInstance?.commitText(tempString)
                florisboard?.setActiveInput(R.id.text_input)
            }
        }
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        if (changedView == this && visibility == View.VISIBLE && florisboard?.koboldState == FlorisBoard.KoboldState.TEMPLATE_LIST_RELOAD) {
            senderAddress?.let { _senderAddress ->
                receiverAddress?.let { _receiverAddress ->
                    shippingCostViewModel?.getListDeliveryFee(
                        fromCity = _senderAddress.city,
                        fromDistrict = _senderAddress.district,
                        fromPostalcode = _senderAddress.postalcode,
                        toCity = _receiverAddress.city,
                        toDistrict = _receiverAddress.district,
                        toPostalcode = _receiverAddress.postalcode,
                        weight = weight,
                        onSuccess = {
                            fullscreenLoading?.isVisible = false
                            chooseCourierRecyclerView?.isVisible = true

                            val initialSize = chooseCourierList.size
                            chooseCourierList.addAll(it.data.format())
                            Timber.e("courierlist: $chooseCourierList")
                            adapter?.notifyItemRangeInserted(initialSize, it.data.size)
                        },
                        onError = {
                            fullscreenLoading?.isVisible = false
                            chooseCourierRecyclerView?.isVisible = true

                            showSnackBar(it, R.color.snackbar_error)
                        }
                    )
                }
            }
        } else {
            chooseCourierList.clear()

            fullscreenLoading?.isVisible = true
            chooseCourierRecyclerView?.isVisible = false
        }

        super.onVisibilityChanged(changedView, visibility)
    }

    override fun onClicked(data: Boolean, index: Int) {
        chooseCourierList[index].isSelected = data
//        showToast(data.toString() + " " + index)

        chooseCourierRecyclerView?.adapter?.notifyDataSetChanged()
    }


}
