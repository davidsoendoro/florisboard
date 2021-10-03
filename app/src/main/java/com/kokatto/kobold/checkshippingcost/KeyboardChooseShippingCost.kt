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
import com.kokatto.kobold.api.model.basemodel.DeliveryFeeModel
import com.kokatto.kobold.api.model.basemodel.format
import com.kokatto.kobold.api.model.basemodel.toText
import com.kokatto.kobold.checkshippingcost.recycleradapter.ChooseCourierRecyclerAdapter
import com.kokatto.kobold.extension.showSnackBar
import com.kokatto.kobold.extension.vertical
import dev.patrickgold.florisboard.ime.core.FlorisBoard

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
            shippingCostViewModel?.getListDeliveryFee(
                fromCity = "Jakarta Selatan",
                fromDistrict = "Mampang Prapatan",
                fromPostalcode = "43123",
                toCity = "Jakarta Selatan",
                toDistrict = "Pasar Minggu",
                toPostalcode = "43124",
                weight = 250,
                onSuccess = {
                    fullscreenLoading?.isVisible = false
                    chooseCourierRecyclerView?.isVisible = true

                    chooseCourierList.addAll(it.data.format())
                    Log.e("courierlist", chooseCourierList.toString())
                    adapter?.notifyDataSetChanged()
                },
                onError = {
                    fullscreenLoading?.isVisible = false
                    chooseCourierRecyclerView?.isVisible = true

                    showSnackBar(it, R.color.snackbar_error)
                }
            )
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
