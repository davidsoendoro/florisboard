package com.kokatto.kobold.transaction

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Filter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.kokatto.kobold.R
import com.kokatto.kobold.api.impl.ErrorResponseValidator
import com.kokatto.kobold.api.model.basemodel.ContactModel
import com.kokatto.kobold.api.model.basemodel.TransactionModel
import com.kokatto.kobold.api.model.basemodel.createTransactionChat
import com.kokatto.kobold.api.model.basemodel.getBankInfoFormatToString
import com.kokatto.kobold.api.model.basemodel.getBankInfoStringFormat
import com.kokatto.kobold.api.model.basemodel.getContactList
import com.kokatto.kobold.bank.BankViewModel
import com.kokatto.kobold.constant.PropertiesTypeConstant
import com.kokatto.kobold.crm.ContactViewModel
import com.kokatto.kobold.dashboardcreatetransaction.TransactionViewModel
import com.kokatto.kobold.editor.SpinnerEditorItem
import com.kokatto.kobold.editor.SpinnerEditorWithAssetAdapter
import com.kokatto.kobold.editor.SpinnerEditorWithAssetItem
import com.kokatto.kobold.extension.findKoboldEditTextId
import com.kokatto.kobold.extension.findTextViewId
import com.kokatto.kobold.extension.koboldSetEnabled
import com.kokatto.kobold.extension.removeThousandSeparatedString
import com.kokatto.kobold.extension.showSnackBar
import com.kokatto.kobold.extension.toThousandSeperatedString
import com.kokatto.kobold.extension.vertical
import com.kokatto.kobold.persistance.AppPersistence
import com.kokatto.kobold.transaction.recycleradapter.BuyerNameRecyclerAdapter
import com.kokatto.kobold.uicomponent.KoboldEditText
import dev.patrickgold.florisboard.common.FlorisViewFlipper
import dev.patrickgold.florisboard.ime.core.DELAY
import dev.patrickgold.florisboard.ime.core.FlorisBoard
import dev.patrickgold.florisboard.ime.text.key.KeyCode
import dev.patrickgold.florisboard.ime.text.keyboard.TextKeyData
import timber.log.Timber
import java.util.*

class KeyboardCreateTransaction : ConstraintLayout {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val florisboard: FlorisBoard? = FlorisBoard.getInstanceOrNull()

    private var transactionViewModel: TransactionViewModel? = TransactionViewModel()
    private var bankViewModel: BankViewModel? = BankViewModel()
    private var contactViewModel: ContactViewModel? = ContactViewModel()

    private var transactionModel = TransactionModel()

    private var koboldExpandView: TextView? = null
    private var buyerNameText: KoboldEditText? = null
    private var imporKontakButton: TextView? = null
    private var chooseChannelText: KoboldEditText? = null

    private var layoutEmpty: LinearLayout? = null
    private var layoutNotFound: LinearLayout? = null
    private var searchEdittext: EditText? = null
    private var fullscreenLoading: ProgressBar? = null
    private var recyclerView: RecyclerView? = null
    private var recyclerAdapter: BuyerNameRecyclerAdapter? = null
    private var dataList: ArrayList<ContactModel> = arrayListOf()

//    private var isLoading: Boolean by Delegates.observable(true) { _, oldValue, newValue ->
//        if (oldValue != newValue) {
//            if (newValue) {
//                recyclerView?.visibility = GONE
//                fullscreenLoading?.visibility = VISIBLE
//            } else {
//                recyclerView?.visibility = VISIBLE
//                fullscreenLoading?.visibility = GONE
//            }
//        }
//    }

    var selectedChannelOptions = SpinnerEditorWithAssetItem("")
    var pickChannelOptions = arrayListOf<SpinnerEditorWithAssetItem>()

    private var selectedContact = SpinnerEditorItem("")
    private var pickContactOptions = arrayOf<SpinnerEditorItem>()

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
        imporKontakButton = findTextViewId(R.id.impor_kontak_button)
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
            val isAutofill = buyerNameText?.isAutofill ?: false
            florisboard?.inputFeedbackManager?.keyPress()
            florisboard?.openEditor(
                R.id.kobold_menu_create_transaction,
                imeOptions,
                inputType,
                buyerNameText?.label?.text.toString(),
                buyerNameText?.editText?.text.toString(),
                isAutofill,
                textWatcher = getTextWatcher()
            ) { result ->
                transactionModel.buyer = result
                buyerNameText?.editText?.text = result
                invalidateSaveButton()
            }
//            val imeOptions = buyerNameText?.imeOptions ?: 0
//            val inputType = buyerNameText?.inputType ?: 0
//            florisboard?.inputFeedbackManager?.keyPress()
//            florisboard?.openEditor(
//                R.id.kobold_menu_create_transaction,
//                imeOptions,
//                inputType,
//                buyerNameText?.label?.text.toString(),
//                buyerNameText?.editText?.text.toString()
//            ) { result ->
//                transactionModel.buyer = result
//                buyerNameText?.editText?.text = result
//            }
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

                    Glide.with(this).load(result.assetUrl).into(chooseChannelText!!.imageLeft)
                },
                "Pilih Channel"
            )
        }

        phoneNumberText?.setOnClickListener {
//            val imeOptions = phoneNumberText?.imeOptions ?: 0
//            val inputType = phoneNumberText?.inputType ?: 0
//            florisboard?.inputFeedbackManager?.keyPress()
//            florisboard?.openContact(
//                R.id.kobold_menu_create_transaction,
//                SpinnerEditorAdapter(
//                    context,
//                    pickContactOptions, selectedContact
//                ) {
//                    phoneNumberText?.editText?.text = it.label
//                }
//            )

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


                    val selectedBankTemp = getBankInfoFormatToString(result.label)

                    transactionModel.payingMethod = selectedBankTemp.bankType
                    transactionModel.bankType = selectedBankTemp.bankType
                    transactionModel.bankAccountNo = selectedBankTemp.accountNo
                    transactionModel.bankAccountName = selectedBankTemp.accountHolder
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
            clearSelected()

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
            florisboard?.inputFeedbackManager?.keyPress()

            val selectedBankTemp = getBankInfoFormatToString(selectedPaymentMethodOption.label)
            Log.e("selected", selectedBankTemp.toString())

//            clearSelected()

//            florisboard?.setActiveInput(R.id.kobold_menu_create_transaction_save_confirmation)

            transactionViewModel?.createTransaction(
                createTransactionRequest = transactionModel,
                onSuccess = {
                    transactionModel.contactId = ContactModel(_id = it.contactId)
                    florisboard?.createTransactionModel = transactionModel
                    if (it.isProfileChange && AppPersistence.showContactUpdateMessage) {
//                        if (it.isProfileChange) {
                        florisboard?.setActiveInput(R.id.kobold_menu_create_transaction_save_confirmation)
                    } else {
                        florisboard?.inputFeedbackManager?.keyPress()
                        florisboard?.textInputManager?.activeEditorInstance?.commitText(
                            createTransactionChat(florisboard.createTransactionModel)
                        )

                        showSnackBar(context.getString(R.string.keyboard_new_transaction_created))

                        florisboard?.setActiveInput(R.id.text_input)
                    }
                    clearSelected()
                },
                onError = {
                    if (ErrorResponseValidator.isSessionExpiredResponse(it))
                        florisboard?.setActiveInput(R.id.kobold_login)
                    else
                        showSnackBar(it)
                }
            )
        }
    }

    private fun clearSelected() {
//            clear edittext
        buyerNameText?.editText?.text = ""
        chooseChannelText?.editText?.text = ""
        chooseChannelText?.imageLeft?.setImageResource(0)
//            or
//            chooseChannelText?.imageLeft?.setImageResource(android.R.color.transparent)
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
    }

    fun prepareContactAutofill() {

        val keyboardViewFlipper =
            florisboard?.uiBinding?.mainViewFlipper?.findViewById<FlorisViewFlipper>(R.id.kobold_keyboard_flipper)
        recyclerView = keyboardViewFlipper?.findViewById(R.id.autofill_options_recycler_view)
        fullscreenLoading = keyboardViewFlipper?.findViewById(R.id.autofill_options_loader)

        recyclerAdapter = BuyerNameRecyclerAdapter(dataList, context)
        recyclerView?.vertical()
        recyclerView?.adapter = recyclerAdapter

        recyclerAdapter?.onItemClick = {
//            shippingCost.senderAddress = it
            buyerNameText?.editText?.text = it.name
            addressText?.editText?.text = it.address
            if(it.channels.isEmpty()){
                chooseChannelText?.editText?.text = "WhatsApp"
                phoneNumberText?.editText?.text = it.phoneNumber
            } else {
                chooseChannelText?.editText?.text = it.channels[0].type
                phoneNumberText?.editText?.text = it.channels[0].account
            }
            florisboard?.setActiveInput(R.id.kobold_menu_create_transaction)
            invalidateSaveButton()
        }
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (changedView == this && visibility == View.VISIBLE) {

            imporKontakButton?.isVisible = true
            val contactpermission =
                ContextCompat.checkSelfPermission(imporKontakButton!!.context, Manifest.permission.READ_CONTACTS)
            when (contactpermission) {
                PackageManager.PERMISSION_GRANTED -> {
                    imporKontakButton?.isVisible = false
                }
                else -> {
                    imporKontakButton?.isVisible = true
                    imporKontakButton?.setOnClickListener {
                        florisboard?.inputFeedbackManager?.keyPress()

                        florisboard?.launchExpandCreateTransactionView(
                            transactionModel
                        )
                    }
                }
            }
            prepareContactAutofill()

            pickChannelOptions.clear()
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
                    //showSnackBar(it, R.color.snackbar_error)
                    if (ErrorResponseValidator.isSessionExpiredResponse(it))
                        florisboard?.setActiveInput(R.id.kobold_login)
                    else
                        showSnackBar(it, R.color.snackbar_error)
                }
            )

            pickContactOptions = arrayOf()
            contactViewModel?.getPaginated(
                page = 1,
                pageSize = 10,
                onLoading = {},
                onSuccess = {
                }, onError = {
                }
            )

            pickCourierOption.clear()
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
                    //showSnackBar(it, R.color.snackbar_error)
                    if (ErrorResponseValidator.isSessionExpiredResponse(it))
                        florisboard?.setActiveInput(R.id.kobold_login)
                    else
                        showSnackBar(it, R.color.snackbar_error)
                }
            )

            pickPaymentMethodOption.clear()

            bankViewModel?.getPaginated(
                onLoading = {},
                onSuccess = {
                    pickPaymentMethodOption.add(SpinnerEditorWithAssetItem("Cash", "cash"))
                    it.data.contents.forEach {
                        pickPaymentMethodOption.add(
                            SpinnerEditorWithAssetItem(getBankInfoStringFormat(it), it.asset)
                        )
                    }
                },
                onError = {
                    //showSnackBar(it, R.color.snackbar_error)
                    if (ErrorResponseValidator.isSessionExpiredResponse(it))
                        florisboard?.setActiveInput(R.id.kobold_login)
                    else
                        showSnackBar(it, R.color.snackbar_error)
                }
            )
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

    private fun getTextWatcher(): TextWatcher {
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
                //avoid triggering event when text is too short
                if (s.length >= 3) {

                    timer = Timer()
                    timer?.schedule(object : TimerTask() {
                        override fun run() {
                            // TO DO: do what you need here (refresh list)
                            // you will probably need to use
                            // runOnUiThread(Runnable action) for some specific
                            // actions
                            Runnable {
                                florisboard?.setActiveInput(R.id.kobold_autofill_editor)
                                loadUserSuggestion(s.toString())
                            }.also { florisboard?.updateOnUiThread(it) }
                        }
                    }, DELAY)
                }
            }
        }
    }

    fun loadUserSuggestion(search: String) {

        fullscreenLoading?.isVisible = false

        val previousSize = dataList.size
        dataList.clear()
        if (previousSize > 0) {
            recyclerAdapter?.notifyItemRangeRemoved(0, previousSize)
        }
        contactViewModel?.getPaginated(1,10, "", search, {}, {
            val contacts = it.data.contents
            contacts.map { contact ->
                contact.isFromBackend = true
            }
            dataList.addAll(contacts)
            Timber.d("Adding contacts: $contacts")
            recyclerAdapter?.notifyItemInserted(dataList.size)
        }, {

        })

        val contactList = getContactList(context)
        dataList.addAll(
            contactList
                .filter {
                it.name.contains(search)
            }.distinctBy {
                it.name
            }.distinctBy {
                it.phoneNumber
            }
        )
        recyclerAdapter?.notifyItemInserted(dataList.size)
    }

//    @Deprecated("use transaction model instead")
//    fun createTransactionModel(): TransactionModel {
//        val selectedBankTemp = getBankInfoFormatToString(selectedPaymentMethodOption.label)
//
//        return TransactionModel(
//            buyer = buyerNameText?.editText?.text.toString(),
//            channel = chooseChannelText?.editText?.text.toString(),
//            phone = phoneNumberText?.editText?.text.toString(),
//            address = addressText?.editText?.text.toString(),
//            notes = orderDetailText?.editText?.text.toString(),
//            price =
//            if (itemPriceText?.editText?.text.toString() == "")
//                0.0
//            else
//                itemPriceText?.editText?.text.toString().toDouble(),
//            payingMethod = selectedBankTemp.bankType,
//            bankType = selectedBankTemp.bankType,
//            bankAccountNo = selectedBankTemp.accountNo,
//            bankAccountName = selectedBankTemp.accountHolder,
//            logistic = chooseCourierText?.editText?.text.toString(),
//            deliveryFee =
//            if (shippingCostText?.editText?.text.toString() == "")
//                0.0
//            else
//                shippingCostText?.editText?.text.toString().toDouble()
//        )
//    }
}
