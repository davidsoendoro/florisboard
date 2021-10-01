package com.kokatto.kobold.checkshippingcost

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.AutoTextModel
import com.kokatto.kobold.api.model.basemodel.DeliveryFeeModel
import com.kokatto.kobold.checkshippingcost.recycleradapter.ChooseCourierRecyclerAdapter
import com.kokatto.kobold.extension.showSnackBar
import com.kokatto.kobold.extension.vertical
import dev.patrickgold.florisboard.ime.core.FlorisBoard

class KeyboardChooseShippingCost : ConstraintLayout, ChooseCourierRecyclerAdapter.OnClick {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    val florisboard = FlorisBoard.getInstance()

    private var shippingCostViewModel: ShippingCostViewModel? = ShippingCostViewModel()

    var chooseCourierList = arrayListOf<DeliveryFeeModel>()
    var chooseCourierRecyclerView: RecyclerView? = null
    var backButton: TextView? = null

    var adapter: ChooseCourierRecyclerAdapter? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        backButton = findViewById(R.id.back_button)
        backButton?.setOnClickListener {
            florisboard.setActiveInput(R.id.kobold_menu_check_shippingcost)
        }

        chooseCourierRecyclerView = findViewById(R.id.choose_courier_recyclerview)
        backButton = findViewById(R.id.back_button)

        adapter = ChooseCourierRecyclerAdapter(chooseCourierList, this)
        chooseCourierRecyclerView?.adapter = adapter
        chooseCourierRecyclerView?.vertical()
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {

        if (changedView == this && visibility == View.VISIBLE && florisboard.koboldState == FlorisBoard.KoboldState.TEMPLATE_LIST_RELOAD) {
            shippingCostViewModel?.getListDeliveryFee(
                fromCity = "Jakarta Selatan",
                fromDistrict = "Mampang Prapatan",
                fromPostalcode = "43123",
                toCity = "Jakarta Selatan",
                toDistrict = "Pasar Minggu",
                toPostalcode = "43124",
                weight = 250,
                onSuccess = {
                    chooseCourierList.addAll(it.data)
                    adapter?.notifyDataSetChanged()
                },
                onError = {
                    showSnackBar(it, R.color.snackbar_error)
                }
            )
<<<<<<< HEAD
        } else {
            chooseCourierList.clear()
=======
>>>>>>> f92ad8fe8e48b3aec333ea40e84d1ef092b82aa5
        }

        super.onVisibilityChanged(changedView, visibility)
    }

    override fun onClicked(data: AutoTextModel) {
        showSnackBar("alooo~~")
    }

    override fun onPackageClicked(int: Int) {
        showSnackBar("Package " + int.toString())
    }


}
