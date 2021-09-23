package com.kokatto.kobold.transaction

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.button.MaterialButton
import com.kokatto.kobold.R
import com.kokatto.kobold.chattemplate.ChatTemplateViewModel
import com.kokatto.kobold.editor.SpinnerEditorAdapter
import com.kokatto.kobold.editor.SpinnerEditorItem
import com.kokatto.kobold.extension.findKoboldEditTextId
import com.kokatto.kobold.extension.koboldSetEnabled
import com.kokatto.kobold.extension.showToast
import com.kokatto.kobold.uicomponent.KoboldEditText
import dev.patrickgold.florisboard.ime.core.FlorisBoard
import dev.patrickgold.florisboard.ime.text.key.KeyCode
import dev.patrickgold.florisboard.ime.text.keyboard.TextKeyData

class KeyboardCreateTransaction : ConstraintLayout {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val florisboard: FlorisBoard? = FlorisBoard.getInstanceOrNull()

    private var chatTemplateViewModel: ChatTemplateViewModel? = ChatTemplateViewModel()

    private var koboldExpandView: TextView? = null
    private var buyerNameText: KoboldEditText? = null
    private var chooseChannelText: KoboldEditText? = null
    var selectedChannelOptions = SpinnerEditorItem("Belum Ada")
    var pickChannelOptions = arrayOf(
        SpinnerEditorItem("Belum Ada"),
        SpinnerEditorItem("WhatsApp"),
        SpinnerEditorItem("WhatsApp Business"),
        SpinnerEditorItem("Line"),
        SpinnerEditorItem("Facebook Messenger"),
        SpinnerEditorItem("Instagram")
    )

    private var phoneNumberText: KoboldEditText? = null
    private var addressText: KoboldEditText? = null
    private var orderDetailText: KoboldEditText? = null
    private var itemPriceText: KoboldEditText? = null
    private var choosePaymentMethodText: KoboldEditText? = null
    var selectedPaymentMethodOption = SpinnerEditorItem("Cash")
    var pickPaymentMethodOption = arrayOf(
        SpinnerEditorItem("Cash"),
        SpinnerEditorItem("BCA"),
        SpinnerEditorItem("Mandiri"),
        SpinnerEditorItem("BTPN")
    )

    private var chooseCourierText: KoboldEditText? = null
    var selectedCourierOption = SpinnerEditorItem("JNE - Package A")
    var pickCourierOption = arrayOf(
        SpinnerEditorItem("JNE - Package A"),
        SpinnerEditorItem("JNE - Package B"),
        SpinnerEditorItem("J&T - Package A"),
        SpinnerEditorItem("J&T - Package B"),
        SpinnerEditorItem("Lion Parcel - Package A"),
        SpinnerEditorItem("Lion Parcel - Package B"),
    )

    private var shippingCostText: KoboldEditText? = null
    private var backButton: ImageView? = null
    private var createTransactionButton: MaterialButton? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        buyerNameText = findKoboldEditTextId(R.id.kobold_transaction_buyer_name)
        chooseChannelText = findKoboldEditTextId(R.id.kobold_transaction_choose_channel)
        phoneNumberText = findKoboldEditTextId(R.id.kobold_transaction_phone_number)
        addressText = findKoboldEditTextId(R.id.kobold_transaction_receiver_address)
        orderDetailText = findKoboldEditTextId(R.id.kobold_transaction_order_detail)
        itemPriceText = findKoboldEditTextId(R.id.kobold_transaction_item_price)
        choosePaymentMethodText = findKoboldEditTextId(R.id.kobold_transaction_payment_method)
        chooseCourierText = findKoboldEditTextId(R.id.kobold_transaction_courier)
        shippingCostText = findKoboldEditTextId(R.id.kobold_transaction_shippingcost)
        backButton = findViewById(R.id.back_button)
        createTransactionButton = findViewById(R.id.create_transaction_button)

        koboldExpandView = findViewById(R.id.kobold_createtransaction_expand_view)
        koboldExpandView?.setOnClickListener {
            florisboard?.inputFeedbackManager?.keyPress()

//            val pickInput = koboldTemplatePickInput?.editText?.text.toString()
//            val nameInput = koboldTemplateNameInput?.editText?.text.toString()
//            val content = koboldTemplateContent?.editText?.text.toString()
            florisboard?.launchExpandCreateTransactionView()
        }

        buyerNameText?.setOnClickListener {
            val imeOptions = buyerNameText?.imeOptions ?: 0
            val inputType = buyerNameText?.inputType ?: 0
            florisboard?.inputFeedbackManager?.keyPress()
            florisboard?.openEditor(
                R.id.kobold_menu_create_transaction,
                imeOptions,
                inputType,
                buyerNameText?.label?.text.toString(),
                buyerNameText?.editText?.text.toString()
            ) { result ->
                buyerNameText?.editText?.text = result
            }
        }

        chooseChannelText?.setOnClickListener {
            florisboard?.inputFeedbackManager?.keyPress()
            florisboard?.openSpinner(
                R.id.kobold_menu_create_transaction, SpinnerEditorAdapter(
                    context,
//                    convert arraylist data to array
                    pickChannelOptions, selectedChannelOptions
                ) { result ->
                    florisboard.inputFeedbackManager.keyPress()
                    chooseChannelText?.editText?.text = result.label
                    florisboard.setActiveInput(R.id.kobold_menu_create_transaction)
                    selectedChannelOptions = result
                }
            )
        }

        phoneNumberText?.setOnClickListener {
            val imeOptions = phoneNumberText?.imeOptions ?: 0
            val inputType = phoneNumberText?.inputType ?: 0
            florisboard?.inputFeedbackManager?.keyPress()
            florisboard?.openEditor(
                R.id.kobold_menu_create_transaction,
                imeOptions,
                inputType,
                phoneNumberText?.label?.text.toString(),
                phoneNumberText?.editText?.text.toString()
            ) { result ->
                phoneNumberText?.editText?.text = result
            }
        }

        addressText?.setOnClickListener {
            val imeOptions = addressText?.imeOptions ?: 0
            val inputType = addressText?.inputType ?: 0
            florisboard?.inputFeedbackManager?.keyPress()
            florisboard?.openEditor(
                R.id.kobold_menu_create_transaction,
                imeOptions,
                inputType,
                addressText?.label?.text.toString(),
                addressText?.editText?.text.toString()
            ) { result ->
                addressText?.editText?.text = result
            }
        }

        orderDetailText?.setOnClickListener {
            val imeOptions = orderDetailText?.imeOptions ?: 0
            val inputType = orderDetailText?.inputType ?: 0
            florisboard?.inputFeedbackManager?.keyPress()
            florisboard?.openEditor(
                R.id.kobold_menu_create_transaction,
                imeOptions,
                inputType,
                orderDetailText?.label?.text.toString(),
                orderDetailText?.editText?.text.toString()
            ) { result ->
                orderDetailText?.editText?.text = result
            }
        }

        orderDetailText?.setOnClickListener {
            val imeOptions = orderDetailText?.imeOptions ?: 0
            val inputType = orderDetailText?.inputType ?: 0
            florisboard?.inputFeedbackManager?.keyPress()
            florisboard?.openEditor(
                R.id.kobold_menu_create_transaction,
                imeOptions,
                inputType,
                orderDetailText?.label?.text.toString(),
                orderDetailText?.editText?.text.toString()
            ) { result ->
                orderDetailText?.editText?.text = result
            }
        }

        itemPriceText?.setOnClickListener {
            val imeOptions = itemPriceText?.imeOptions ?: 0
            val inputType = itemPriceText?.inputType ?: 0
            florisboard?.inputFeedbackManager?.keyPress()
            florisboard?.openEditor(
                R.id.kobold_menu_create_transaction,
                imeOptions,
                inputType,
                itemPriceText?.label?.text.toString(),
                itemPriceText?.editText?.text.toString()
            ) { result ->
                itemPriceText?.editText?.text = result
                invalidateSaveButton()
            }
        }

        choosePaymentMethodText?.setOnClickListener {
            florisboard?.inputFeedbackManager?.keyPress()
            florisboard?.openSpinner(
                R.id.kobold_menu_create_transaction, SpinnerEditorAdapter(
                    context,
//                    convert arraylist data to array
                    pickPaymentMethodOption, selectedPaymentMethodOption
                ) { result ->
                    florisboard.inputFeedbackManager.keyPress()
                    choosePaymentMethodText?.editText?.text = result.label
                    florisboard.setActiveInput(R.id.kobold_menu_create_transaction)
                    selectedPaymentMethodOption = result

                    invalidateSaveButton()
                }
            )
        }

        chooseCourierText?.setOnClickListener {
            florisboard?.inputFeedbackManager?.keyPress()
            florisboard?.openSpinner(
                R.id.kobold_menu_create_transaction, SpinnerEditorAdapter(
                    context,
//                    convert arraylist data to array
                    pickCourierOption, selectedCourierOption
                ) { result ->
                    florisboard.inputFeedbackManager.keyPress()
                    chooseCourierText?.editText?.text = result.label
                    florisboard.setActiveInput(R.id.kobold_menu_create_transaction)
                    selectedCourierOption = result
                }
            )
        }

        shippingCostText?.setOnClickListener {
            val imeOptions = shippingCostText?.imeOptions ?: 0
            val inputType = shippingCostText?.inputType ?: 0
            florisboard?.inputFeedbackManager?.keyPress()
            florisboard?.openEditor(
                R.id.kobold_menu_create_transaction,
                imeOptions,
                inputType,
                shippingCostText?.label?.text.toString(),
                shippingCostText?.editText?.text.toString()
            ) { result ->
                shippingCostText?.editText?.text = result
            }
        }

        backButton?.setOnClickListener {
            florisboard?.inputFeedbackManager?.keyPress(TextKeyData(code = KeyCode.CANCEL))
            florisboard?.activeEditorInstance?.activeEditText = null
            florisboard?.setActiveInput(R.id.kobold_menu_transaction)
        }

        invalidateSaveButton()
        createTransactionButton?.setOnClickListener {
            showToast("enabled")
        }
    }

    fun invalidateSaveButton() {
        var isInputValid = false
        itemPriceText?.let { itemPriceText ->
            choosePaymentMethodText?.let { choosePaymentMethodText ->
                isInputValid =
                    itemPriceText.editText.text.isNotEmpty() && choosePaymentMethodText.editText.text.isNotEmpty()
            }
        }
        createTransactionButton?.koboldSetEnabled(isInputValid)
    }
}
