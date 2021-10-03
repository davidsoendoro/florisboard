package com.kokatto.kobold.transaction

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.button.MaterialButton
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.TransactionModel
import com.kokatto.kobold.api.model.basemodel.getBankInfoStringFormat
import com.kokatto.kobold.bank.BankViewModel
import com.kokatto.kobold.constant.PropertiesTypeConstant
import com.kokatto.kobold.dashboardcreatetransaction.TransactionViewModel
import com.kokatto.kobold.editor.SpinnerEditorWithAssetAdapter
import com.kokatto.kobold.editor.SpinnerEditorWithAssetItem
import com.kokatto.kobold.extension.findKoboldEditTextId
import com.kokatto.kobold.extension.koboldSetEnabled
import com.kokatto.kobold.extension.removeThousandSeparatedString
import com.kokatto.kobold.extension.showSnackBar
import com.kokatto.kobold.extension.toThousandSeperatedString
import com.kokatto.kobold.uicomponent.KoboldEditText
import dev.patrickgold.florisboard.ime.core.FlorisBoard
import dev.patrickgold.florisboard.ime.text.key.KeyCode
import dev.patrickgold.florisboard.ime.text.keyboard.TextKeyData

class KeyboardCreateTransaction : ConstraintLayout {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val florisboard: FlorisBoard? = FlorisBoard.getInstanceOrNull()

    private var transactionViewModel: TransactionViewModel? = TransactionViewModel()
    private var bankViewModel: BankViewModel? = BankViewModel()

    private var transactionModel = TransactionModel()

    private var koboldExpandView: TextView? = null
    private var buyerNameText: KoboldEditText? = null
    private var chooseChannelText: KoboldEditText? = null

    var selectedChannelOptions = SpinnerEditorWithAssetItem("")
    var pickChannelOptions = arrayListOf<SpinnerEditorWithAssetItem>()

    private var phoneNumberText: KoboldEditText? = null
    private var addressText: KoboldEditText? = null
    private var orderDetailText: KoboldEditText? = null
    private var itemPriceText: KoboldEditText? = null
    private var choosePaymentMethodText: KoboldEditText? = null
    var selectedPaymentMethodOption = SpinnerEditorWithAssetItem("")
    var pickPaymentMethodOption = arrayListOf<SpinnerEditorWithAssetItem>()

    private var chooseCourierText: KoboldEditText? = null
    var selectedCourierOption = SpinnerEditorWithAssetItem("")
    var pickCourierOption = arrayListOf<SpinnerEditorWithAssetItem>()

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
            florisboard?.launchExpandCreateTransactionView(
                transactionModel
            )
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
                transactionModel.buyer = result
                buyerNameText?.editText?.text = result
            }
        }

        chooseChannelText?.setOnClickListener {
            florisboard?.inputFeedbackManager?.keyPress()
            florisboard?.openSpinner(
                R.id.kobold_menu_create_transaction, SpinnerEditorWithAssetAdapter(
                    context, pickChannelOptions, selectedChannelOptions
                ) { result ->
                    florisboard.inputFeedbackManager.keyPress()
                    chooseChannelText?.editText?.text = result.label
                    florisboard.setActiveInput(R.id.kobold_menu_create_transaction)

                    transactionModel.channel = result.label
                    selectedChannelOptions = result
                },
                "Pilih Channel"
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
                transactionModel.phone = result
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
                transactionModel.address = result
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
                transactionModel.notes = result
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
                itemPriceText?.editText?.text.toString().removeThousandSeparatedString()
            ) { result ->
                transactionModel.price = result.toDouble()
                itemPriceText?.editText?.text = result.toThousandSeperatedString("Rp")
                invalidateSaveButton()
            }
        }

        choosePaymentMethodText?.setOnClickListener {
            florisboard?.inputFeedbackManager?.keyPress()
            florisboard?.openSpinner(
                R.id.kobold_menu_create_transaction, SpinnerEditorWithAssetAdapter(
                    context,
//                    convert arraylist data to array
                    pickPaymentMethodOption, selectedPaymentMethodOption
                ) { result ->
                    florisboard.inputFeedbackManager.keyPress()
                    choosePaymentMethodText?.editText?.text = result.label
                    florisboard.setActiveInput(R.id.kobold_menu_create_transaction)

                    transactionModel.payingMethod = result.label
                    selectedPaymentMethodOption = result

                    invalidateSaveButton()
                },
                "Pilih Metode pembayaran"
            )
        }

        chooseCourierText?.setOnClickListener {
            florisboard?.inputFeedbackManager?.keyPress()
            florisboard?.openSpinner(
                R.id.kobold_menu_create_transaction, SpinnerEditorWithAssetAdapter(
                    context,
//                    convert arraylist data to array
                    pickCourierOption, selectedCourierOption,
                ) { result ->
                    florisboard.inputFeedbackManager.keyPress()
                    chooseCourierText?.editText?.text = result.label
                    florisboard.setActiveInput(R.id.kobold_menu_create_transaction)

                    transactionModel.logistic = result.label
                    selectedCourierOption = result
                },
                "Pilih Kurir"
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
                shippingCostText?.editText?.text.toString().removeThousandSeparatedString()
            ) { result ->
                transactionModel.deliveryFee = result.toDouble()
                shippingCostText?.editText?.text = result.toThousandSeperatedString("Rp")
            }
        }

        backButton?.setOnClickListener {
//            clear edittext
            buyerNameText?.editText?.text = ""
            chooseChannelText?.editText?.text = ""
            selectedChannelOptions = SpinnerEditorWithAssetItem("")
            phoneNumberText?.editText?.text = ""
            addressText?.editText?.text = ""
            orderDetailText?.editText?.text = ""
            itemPriceText?.editText?.text = ""
            choosePaymentMethodText?.editText?.text = ""
            selectedPaymentMethodOption = SpinnerEditorWithAssetItem("")
            chooseCourierText?.editText?.text = ""
            selectedCourierOption = SpinnerEditorWithAssetItem("")
            shippingCostText?.editText?.text = ""

            if (florisboard?.setActiveInputFromMainmenu == true) {
                florisboard.inputFeedbackManager.keyPress(TextKeyData(code = KeyCode.CANCEL))
                florisboard.activeEditorInstance.activeEditText = null
                florisboard.setActiveInput(R.id.kobold_mainmenu)
            } else {
                florisboard?.inputFeedbackManager?.keyPress(TextKeyData(code = KeyCode.CANCEL))
                florisboard?.activeEditorInstance?.activeEditText = null
                florisboard?.setActiveInput(R.id.kobold_menu_transaction)
            }
        }

        invalidateSaveButton()
        createTransactionButton?.setOnClickListener {

            transactionViewModel?.createTransaction(
                createTransactionRequest = transactionModel,
                onSuccess = {
                    showSnackBar(it)
                    florisboard?.setActiveInput(R.id.kobold_menu_transaction)
                },
                onError = {
                    showSnackBar(it)
                }
            )
        }
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)

        if (changedView == this && visibility == View.VISIBLE) {

            if (pickChannelOptions.size == 0)
                transactionViewModel?.getStandardListProperties(
                    type = PropertiesTypeConstant.channel,
                    onSuccess = {
                        it.data.forEach { it ->
                            pickChannelOptions.add(
                                SpinnerEditorWithAssetItem(it.assetDesc, it.assetUrl)
                            )
                        }
                    },
                    onError = {
                        showSnackBar(it, R.color.snackbar_error)
                    }
                )

            if (pickCourierOption.size == 0)
                transactionViewModel?.getStandardListProperties(
                    type = PropertiesTypeConstant.logistic,
                    onSuccess = {
                        it.data.forEach { it ->
                            pickCourierOption.add(
                                SpinnerEditorWithAssetItem(it.assetDesc, it.assetUrl)
                            )
                        }
                    },
                    onError = {
                        showSnackBar(it, R.color.snackbar_error)
                    }
                )

            if (pickPaymentMethodOption.size == 0) {
                pickPaymentMethodOption.add(SpinnerEditorWithAssetItem("Cash", "cash"))

                bankViewModel?.getPaginated(
                    onLoading = {},
                    onSuccess = {
                        it.data.contents.forEach {
                            pickPaymentMethodOption.add(
                                SpinnerEditorWithAssetItem(getBankInfoStringFormat(it), it.asset)
                            )
                        }
                    },
                    onError = {
                        showSnackBar(it, R.color.snackbar_error)
                    }
                )
            }

        } else {
//            pickChannelOptions.clear()
//            pickCourierOption.clear()
//            pickPaymentMethodOption.clear()
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

    @Deprecated("use transaction model instead")
    fun createTransactionModel(): TransactionModel {
        return TransactionModel(
            buyer = buyerNameText?.editText?.text.toString(),
            channel = chooseChannelText?.editText?.text.toString(),
            phone = phoneNumberText?.editText?.text.toString(),
            address = addressText?.editText?.text.toString(),
            notes = orderDetailText?.editText?.text.toString(),
            price =
            if (itemPriceText?.editText?.text.toString() == "")
                0.0
            else
                itemPriceText?.editText?.text.toString().toDouble(),
            payingMethod = choosePaymentMethodText?.editText?.text.toString(),
            logistic = chooseCourierText?.editText?.text.toString(),
            deliveryFee =
            if (shippingCostText?.editText?.text.toString() == "")
                0.0
            else
                shippingCostText?.editText?.text.toString().toDouble()
        )
    }
}
