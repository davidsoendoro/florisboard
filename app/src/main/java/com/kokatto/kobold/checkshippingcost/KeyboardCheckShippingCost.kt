package com.kokatto.kobold.checkshippingcost

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kokatto.kobold.R
import com.kokatto.kobold.api.impl.ErrorResponseValidator
import com.kokatto.kobold.api.model.basemodel.DeliveryAddressModel
import com.kokatto.kobold.api.model.basemodel.ShippingCostModel
import com.kokatto.kobold.dashboardcheckshippingcost.AddressShippingActivity
import com.kokatto.kobold.dashboardcheckshippingcost.recylerAdapter.ShippingAddressRecyclerAdapter
import com.kokatto.kobold.extension.addAffixToString
import com.kokatto.kobold.extension.findKoboldEditTextId
import com.kokatto.kobold.extension.findTextViewId
import com.kokatto.kobold.extension.showSnackBar
import com.kokatto.kobold.extension.showToast
import com.kokatto.kobold.uicomponent.KoboldEditText
import dev.patrickgold.florisboard.common.FlorisViewFlipper
import dev.patrickgold.florisboard.ime.core.DELAY
import dev.patrickgold.florisboard.ime.core.FlorisBoard
import dev.patrickgold.florisboard.ime.text.key.KeyCode
import dev.patrickgold.florisboard.ime.text.keyboard.TextKeyData
import java.util.*
import kotlin.properties.Delegates

class KeyboardCheckShippingCost : ConstraintLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val florisboard: FlorisBoard? = FlorisBoard.getInstanceOrNull()

    private var shippingCost = ShippingCostModel()

    private var senderAddressEdittext: KoboldEditText? = null
    private var receiverAddressEdittext: KoboldEditText? = null
    private var packageWeightDetail: TextView? = null
    private var minusWeightButton: ImageView? = null
    private var packageWeightText: TextView? = null
    private var plusWeightButton: ImageView? = null
    private var backButton: TextView? = null
    private var submitButton: Button? = null

    private var layoutEmpty: LinearLayout? = null
    private var layoutNotFound: LinearLayout? = null
    private var searchEdittext: EditText? = null
    private var fullscreenLoading: ProgressBar? = null

    private var dataList: ArrayList<DeliveryAddressModel> = arrayListOf()
    private var recyclerView: RecyclerView? = null
    private var recyclerAdapter: ShippingAddressRecyclerAdapter? = null
    private var shippingCostViewModel: ShippingCostViewModel? = ShippingCostViewModel()

    private val minPackage: Int = 1
    private val maxPackage: Int = 1000

    private var isLoading: Boolean by Delegates.observable(true) { _, oldValue, newValue ->
        if (oldValue != newValue) {
            if (newValue) {
                recyclerView?.visibility = GONE
                fullscreenLoading?.visibility = VISIBLE
            } else {
                recyclerView?.visibility = VISIBLE
                fullscreenLoading?.visibility = GONE
            }
        }
    }

    private var mode: String? = null

    private fun getTextWatcher(_mode: String): TextWatcher {
        return object : TextWatcher {
            var timer: Timer? = null

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int,
                count: Int
            ) {
                if (timer != null) timer?.cancel()
            }

            override fun afterTextChanged(s: Editable) {
                mode = _mode
                //avoid triggering event when text is too short
                if (s.length >= 3) {

                    timer = Timer()
                    timer?.schedule(object : TimerTask() {
                        override fun run() {
                            // TODO: do what you need here (refresh list)
                            // you will probably need to use
                            // runOnUiThread(Runnable action) for some specific
                            // actions
                            Runnable {
                                florisboard?.setActiveInput(R.id.kobold_autofill_editor)
                                loadAddressSuggestion(s.toString())
                            }.also { florisboard?.updateOnUiThread(it) }
                        }
                    }, DELAY)
                }
            }
        }
    }

    fun loadAddressSuggestion(address: String) {
        layoutEmpty?.isVisible = false
        layoutNotFound?.isVisible = false
        recyclerView?.isVisible = false
        fullscreenLoading?.isVisible = true

        val previousSize = dataList.size
        dataList.clear()
        if (previousSize > 0) {
            recyclerAdapter?.notifyItemRangeRemoved(0, previousSize)
        }
        shippingCostViewModel?.getPaginatedDeliveryAddress(
            page = 1,
            pageSize = 15,
            onLoading = {
                isLoading = it
            },
            search = address,
            onSuccess = { it ->
                if (it.data.contents.isNotEmpty()) {
                    dataList.addAll(it.data.contents)
                    recyclerAdapter?.notifyItemInserted(dataList.size)

                    recyclerView?.isVisible = true
                    layoutEmpty?.isVisible = false
                    layoutNotFound?.isVisible = false
                } else {
                    layoutNotFound?.isVisible = true
                    recyclerView?.isVisible = false
                    layoutEmpty?.isVisible = false
                }

                fullscreenLoading?.isVisible = false
            },
            onError = {
                fullscreenLoading?.isVisible = false
                recyclerView?.isVisible = false
                layoutEmpty?.isVisible = false
                layoutNotFound?.isVisible = true

                if(ErrorResponseValidator.isSessionExpiredResponse(it))
                    florisboard?.setActiveInput(R.id.kobold_login)
                else
                    showToast(it)
            }
        )
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        senderAddressEdittext = findKoboldEditTextId(R.id.sender_address_edittext)
        receiverAddressEdittext = findKoboldEditTextId(R.id.receiver_address_edittext)
        packageWeightDetail = findTextViewId(R.id.package_weight_detail)
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
                isAutofill,
                textWatcher = getTextWatcher(AddressShippingActivity.SENDER)
            ) { result ->
                senderAddressEdittext?.editText?.text = result
                invalidateSaveButton()
            }
        }

        receiverAddressEdittext?.setOnClickListener {
            val imeOptions = receiverAddressEdittext?.imeOptions ?: 0
            val inputType = receiverAddressEdittext?.inputType ?: 0
            val isAutofill = senderAddressEdittext?.isAutofill ?: false
            florisboard?.inputFeedbackManager?.keyPress()
            florisboard?.openEditor(
                R.id.kobold_menu_check_shippingcost,
                imeOptions,
                inputType,
                receiverAddressEdittext?.label?.text.toString(),
                receiverAddressEdittext?.editText?.text.toString(),
                isAutofill,
                textWatcher = getTextWatcher(AddressShippingActivity.RECEIVER)
            ) { result ->
                receiverAddressEdittext?.editText?.text = result
                invalidateSaveButton()
            }
        }

        packageWeightDetail?.text = "Maks. $maxPackage kg"

        minusWeightButton?.setOnClickListener {
            if (shippingCost.packageWeight == minPackage) {
                florisboard?.inputFeedbackManager?.keyPress(TextKeyData(code = KeyCode.CANCEL))
                showSnackBar("Paket tidak boleh lebih ringan dari $minPackage kg")
            } else {
                florisboard?.inputFeedbackManager?.keyPress()
                shippingCost.packageWeight--
                packageWeightText?.text = shippingCost.packageWeight.addAffixToString(suffix = " kg")
            }
        }

        plusWeightButton?.setOnClickListener {
            if (shippingCost.packageWeight == maxPackage) {
                florisboard?.inputFeedbackManager?.keyPress(TextKeyData(code = KeyCode.CANCEL))
                showSnackBar("Paket tidak boleh lebih berat dari $maxPackage kg")
            } else {
                florisboard?.inputFeedbackManager?.keyPress()
                shippingCost.packageWeight++
                packageWeightText?.text = shippingCost.packageWeight.addAffixToString(suffix = " kg")
            }
        }

        backButton?.setOnClickListener {
            florisboard?.inputFeedbackManager?.keyPress(TextKeyData(code = KeyCode.CANCEL))
            florisboard?.activeEditorInstance?.activeEditText = null
            florisboard?.setActiveInput(R.id.kobold_mainmenu)

            senderAddressEdittext?.editText?.text = ""
            receiverAddressEdittext?.editText?.text = ""
            shippingCost = ShippingCostModel()
        }

        submitButton?.setOnClickListener {
//            error snackbar
//            showSnackBar("Koneksi internet tidak tersedia.", R.color.snackbar_error)
            florisboard?.inputFeedbackManager?.keyPress()

            florisboard?.openShippingCost(
                shippingCost.senderAddress,
                shippingCost.receiverAddress,
                shippingCost.packageWeight
            )
        }
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (changedView is KeyboardCheckShippingCost && visibility == View.VISIBLE && changedView == this) {
            prepareAddressAutofill()
            invalidateSaveButton()
        }
    }

    fun prepareAddressAutofill() {

        val keyboardViewFlipper =
            florisboard?.uiBinding?.mainViewFlipper?.findViewById<FlorisViewFlipper>(R.id.kobold_keyboard_flipper)
        recyclerView = keyboardViewFlipper?.findViewById(R.id.autofill_options_recycler_view)
        fullscreenLoading = keyboardViewFlipper?.findViewById(R.id.autofill_options_loader)

        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerAdapter = ShippingAddressRecyclerAdapter(context, dataList)
        recyclerView?.adapter = recyclerAdapter

        recyclerAdapter?.onItemClick = {
            when (mode) {
                AddressShippingActivity.SENDER -> {
                    shippingCost.senderAddress = it
                    senderAddressEdittext?.editText?.text = it.writtenAddress()
                    florisboard?.setActiveInput(R.id.kobold_menu_check_shippingcost)
                    invalidateSaveButton()
                }
                AddressShippingActivity.RECEIVER -> {
                    shippingCost.receiverAddress = it
                    receiverAddressEdittext?.editText?.text = it.writtenAddress()
                    florisboard?.setActiveInput(R.id.kobold_menu_check_shippingcost)
                    invalidateSaveButton()
                }
            }
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
