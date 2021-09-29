package com.kokatto.kobold.checkshippingcost

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.AutoTextModel
import com.kokatto.kobold.checkshippingcost.recycleradapter.ChooseCourierRecyclerAdapter
import com.kokatto.kobold.extension.showSnackBar
import com.kokatto.kobold.extension.vertical
import dev.patrickgold.florisboard.ime.core.FlorisBoard

class KeyboardChooseShippingCost : ConstraintLayout, ChooseCourierRecyclerAdapter.OnClick {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    val florisboard = FlorisBoard.getInstance()

    var chooseCourierRecyclerView: RecyclerView? = null
    var backButton: TextView? = null

    val adapter: ChooseCourierRecyclerAdapter? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        chooseCourierRecyclerView = findViewById(R.id.choose_courier_recyclerview)
        backButton = findViewById(R.id.back_button)

        val adapter = ChooseCourierRecyclerAdapter(this)
        chooseCourierRecyclerView?.adapter = adapter
        chooseCourierRecyclerView?.vertical()
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (changedView == this.rootView && visibility == View.VISIBLE && florisboard.koboldState == FlorisBoard.KoboldState.TEMPLATE_LIST_RELOAD) {

        }
    }

    override fun onClicked(data: AutoTextModel) {
        showSnackBar("alooo~~")
    }

    override fun onPackageClicked(int: Int) {
        showSnackBar("Package " + int.toString())
    }


}
