package com.kokatto.kobold.checkshippingcost

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.ShippingCostModel
import com.kokatto.kobold.extension.addAffixToString
import com.kokatto.kobold.extension.findKoboldEditTextId
import com.kokatto.kobold.extension.findTextViewId
import com.kokatto.kobold.extension.showSnackBar
import com.kokatto.kobold.uicomponent.KoboldEditText
import dev.patrickgold.florisboard.ime.core.FlorisBoard
import dev.patrickgold.florisboard.ime.text.key.KeyCode
import dev.patrickgold.florisboard.ime.text.keyboard.TextKeyData

class KeyboardCheckShippingCost : ConstraintLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val florisboard: FlorisBoard? = FlorisBoard.getInstanceOrNull()

    private var shippingCost = ShippingCostModel()

    private var senderAddressEdittext: KoboldEditText? = null
    private var receiverAddressEdittext: KoboldEditText? = null
    private var minusWeightButton: ImageView? = null
    private var packageWeightText: TextView? = null
    private var plusWeightButton: ImageView? = null
    private var backButton: TextView? = null
    private var submitButton: Button? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        senderAddressEdittext = findKoboldEditTextId(R.id.sender_address_edittext)
        receiverAddressEdittext = findKoboldEditTextId(R.id.receiver_address_edittext)
        minusWeightButton = findViewById(R.id.minus_weight_button)
        packageWeightText = findTextViewId(R.id.package_weight_text)
        plusWeightButton = findViewById(R.id.plus_weight_button)
        backButton = findViewById(R.id.back_button)
        submitButton = findViewById(R.id.submit_button)

        senderAddressEdittext?.setOnClickListener {
            val imeOptions = senderAddressEdittext?.imeOptions ?: 0
            val inputType = senderAddressEdittext?.inputType ?: 0
            val isAutofill = senderAddressEdittext?.isAutofill ?: false
            florisboard?.inputFeedbackManager?.keyPress()
            florisboard?.openEditor(
                R.id.kobold_menu_check_shippingcost,
                imeOptions,
                inputType,
                senderAddressEdittext?.label?.text.toString(),
                senderAddressEdittext?.editText?.text.toString(),
                isAutofill
            ) { result ->
                senderAddressEdittext?.editText?.text = result
                invalidateSaveButton()
            }
        }

        receiverAddressEdittext?.setOnClickListener {
            val imeOptions = receiverAddressEdittext?.imeOptions ?: 0
            val inputType = receiverAddressEdittext?.inputType ?: 0
            florisboard?.inputFeedbackManager?.keyPress()
            florisboard?.openEditor(
                R.id.kobold_menu_check_shippingcost,
                imeOptions,
                inputType,
                receiverAddressEdittext?.label?.text.toString(),
                receiverAddressEdittext?.editText?.text.toString()
            ) { result ->
                receiverAddressEdittext?.editText?.text = result
                invalidateSaveButton()
            }
        }

        minusWeightButton?.setOnClickListener {
            if (shippingCost.packageWeight == 1)
                showSnackBar("Paket tidak boleh lebih ringan dari 1 kg")
            else {
                shippingCost.packageWeight--
                packageWeightText?.text = shippingCost.packageWeight.addAffixToString(suffix = " kg")
            }
        }

        plusWeightButton?.setOnClickListener {
            if (shippingCost.packageWeight == 7)
                showSnackBar("Paket tidak boleh lebih berat dari 7 kg")
            else {
                shippingCost.packageWeight++
                packageWeightText?.text = shippingCost.packageWeight.addAffixToString(suffix = " kg")
            }
        }

        backButton?.setOnClickListener {
            florisboard?.inputFeedbackManager?.keyPress(TextKeyData(code = KeyCode.CANCEL))
            florisboard?.activeEditorInstance?.activeEditText = null
            florisboard?.setActiveInput(R.id.kobold_mainmenu)
        }

        submitButton?.setOnClickListener {
//            error snackbar
//            showSnackBar("Koneksi internet tidak tersedia.", R.color.snackbar_error)

            florisboard?.setActiveInput(R.id.kobold_menu_choose_shippingcost)
        }
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (changedView == this.rootView && visibility == View.VISIBLE && florisboard?.koboldState == FlorisBoard.KoboldState.TEMPLATE_LIST_RELOAD) {

        }
    }

    fun invalidateSaveButton() {
        var isInputValid = false
        senderAddressEdittext?.let { templateNameInput ->
            receiverAddressEdittext?.let { templateContent ->
                isInputValid =
                    templateNameInput.editText.text.isNotEmpty() && templateContent.editText.text.isNotEmpty()
            }
        }
        submitButton?.isEnabled = isInputValid
    }
}
