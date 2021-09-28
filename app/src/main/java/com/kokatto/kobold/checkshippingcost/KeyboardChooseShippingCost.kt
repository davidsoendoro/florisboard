package com.kokatto.kobold.checkshippingcost

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import dev.patrickgold.florisboard.ime.core.FlorisBoard

class KeyboardChooseShippingCost : ConstraintLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    val florisboard = FlorisBoard.getInstance()

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (changedView == this.rootView && visibility == View.VISIBLE && florisboard.koboldState == FlorisBoard.KoboldState.TEMPLATE_LIST_RELOAD) {

        }
    }
}
