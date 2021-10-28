package com.kokatto.kobold.dashboardcreatetransaction

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.kokatto.kobold.R
import com.kokatto.kobold.api.impl.DashboardSessionExpiredEventHandler
import com.kokatto.kobold.api.impl.ErrorResponseValidator
import com.kokatto.kobold.api.model.basemodel.BankModel
import com.kokatto.kobold.api.model.basemodel.ContactChannelModel
import com.kokatto.kobold.api.model.basemodel.ContactModel
import com.kokatto.kobold.api.model.basemodel.PropertiesModel
import com.kokatto.kobold.api.model.basemodel.PropertiesModel.Companion.setTitleText
import com.kokatto.kobold.api.model.basemodel.TransactionModel
import com.kokatto.kobold.api.model.basemodel.getContactList
import com.kokatto.kobold.api.model.request.PostUpdateContactByTransactionIdRequest
import com.kokatto.kobold.component.DashboardThemeActivity
import com.kokatto.kobold.constant.ActivityConstantCode
import com.kokatto.kobold.constant.ActivityConstantCode.Companion.BANK_TYPE_OTHER
import com.kokatto.kobold.constant.ActivityConstantCode.Companion.READ_CONTACT_PERMISSION_CODE
import com.kokatto.kobold.crm.ContactViewModel
import com.kokatto.kobold.dashboardcreatetransaction.autocompleteadapter.ContactAutocompleteAdapter
import com.kokatto.kobold.dashboardcreatetransaction.spinner.SpinnerBankSelector
import com.kokatto.kobold.dashboardcreatetransaction.spinner.SpinnerChannelSelector
import com.kokatto.kobold.dashboardcreatetransaction.spinner.SpinnerLogisticSelector
import com.kokatto.kobold.extension.addSeparator
import com.kokatto.kobold.extension.createBottomSheetDialog
import com.kokatto.kobold.extension.showToast
import com.kokatto.kobold.persistance.AppPersistence
import com.kokatto.kobold.utility.CurrencyUtility
import timber.log.Timber


class InputActivity : DashboardThemeActivity() {

    companion object {
        const val CREATE = -1
        const val EDIT = 1
        const val EDIT_COMPLETE = 2
        const val FROM_KEYBOARD = 3
        const val MODE = "MODE"
        const val EXTRA_ID = "EXTRA_ID"
        const val EXTRA_DATA = "EXTRA_DATA"
    }

    private var editTextBuyer: AutoCompleteTextView? = null
    private var editTextChannel: EditText? = null
    private var editTextChannelLayout: TextInputLayout? = null
    private var channelAccountTitle: TextView? = null
    private var editTextPhone: EditText? = null
    private var editTextAddress: EditText? = null
    private var editTextAddressLayout: TextInputLayout? = null
    private var editTextNote: EditText? = null
    private var editTextNoteLayout: TextInputLayout? = null
    private var editTextPrice: EditText? = null
    private var editTextPriceLayout: TextInputLayout? = null
    private var editTextPayment: EditText? = null
    private var editTextPaymentLayout: TextInputLayout? = null
    private var editTextLogistic: EditText? = null
    private var editTextLogisticLayout: TextInputLayout? = null
    private var editTextdeliveryFee: EditText? = null
    private var editTextDeliveryFeeLayout: TextInputLayout? = null
    private var btnSubmit: CardView? = null
    private var btnSubmitText: TextView? = null
    private var btnSubmitProgress: ProgressBar? = null
    private var layoutTitleText: TextView? = null
    private var btnBack: ImageView? = null

    private var mode: Int? = -1
    private var isValidFormArray: BooleanArray = BooleanArray(7)
    private var isLoading: Boolean = false

    private var selectedBank: BankModel = BankModel("", BANK_TYPE_OTHER, "Cash", "", "", "")
    private var selectedLogistic: PropertiesModel? = null
    private var selectedChannel: PropertiesModel? = null
    private var currentTransaction: TransactionModel? = null
    private val contactChannels = arrayListOf<ContactChannelModel>()
    private var selectedContact: ContactModel? = null
    private var finishedTransaction: TransactionModel? = null

    private var transactionViewModel: TransactionViewModel? = TransactionViewModel()
    private var spinnerChannelSelector: SpinnerChannelSelector? = SpinnerChannelSelector()
    private var spinnerBankSelector: SpinnerBankSelector? = SpinnerBankSelector()
    private var spinnerLogisticSelector: SpinnerLogisticSelector? = SpinnerLogisticSelector()
    private var contactAutocompleteAdapter: ContactAutocompleteAdapter? = null
    private var contactBottomDialog: BottomSheetDialog? = null
    private var contactCancelButton: MaterialButton? = null
    private var contactSubmitButton: MaterialButton? = null
    private var contactNeverShowAgainButton: AppCompatCheckBox? = null
    private val contactViewModel = ContactViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_transaction)

        editTextBuyer = findViewById<AutoCompleteTextView>(R.id.edittext_buyername)
        editTextChannel = findViewById<EditText>(R.id.edittext_channel)
        editTextChannelLayout = findViewById<TextInputLayout>(R.id.edittext_channel_layout)
        channelAccountTitle = findViewById(R.id.channel_account_title)
        editTextPhone = findViewById<EditText>(R.id.edittext_phone)
        editTextAddress = findViewById<EditText>(R.id.edittext_buyeraddress)
        editTextAddressLayout = findViewById<TextInputLayout>(R.id.edittext_buyeraddress_layout)
        editTextNote = findViewById<EditText>(R.id.edittext_note)
        editTextNoteLayout = findViewById<TextInputLayout>(R.id.edittext_note_layout)
        editTextPrice = findViewById<EditText>(R.id.edittext_price)
        editTextPriceLayout = findViewById<TextInputLayout>(R.id.edittext_price_layout)
        editTextPayment = findViewById<EditText>(R.id.edittext_paymentmethod)
        editTextPaymentLayout = findViewById<TextInputLayout>(R.id.edittext_paymentmethod_layout)
        editTextLogistic = findViewById<EditText>(R.id.edittext_logistic)
        editTextLogisticLayout = findViewById<TextInputLayout>(R.id.edittext_logistic_layout)
        editTextdeliveryFee = findViewById<EditText>(R.id.edittext_deliveryfee)
        editTextDeliveryFeeLayout = findViewById<TextInputLayout>(R.id.edittext_deliveryfee_layout)
        btnSubmit = findViewById<CardView>(R.id.submit_button)
        btnBack = findViewById<ImageView>(R.id.back_button)
        btnSubmitText = findViewById<TextView>(R.id.submit_button_text)
        btnSubmitProgress = findViewById<ProgressBar>(R.id.submit_button_loading)
        layoutTitleText = findViewById<TextView>(R.id.title_text)

        contactBottomDialog = createBottomSheetDialog(
            layoutInflater.inflate(
                R.layout.kobold_update_contact_bottom_dialog,
                null
            )
        )
        contactCancelButton = contactBottomDialog?.findViewById(R.id.contact_bottom_dialog_cancel_button)
        contactSubmitButton = contactBottomDialog?.findViewById(R.id.contact_bottom_dialog_submit_button)
        contactNeverShowAgainButton =
            contactBottomDialog?.findViewById(R.id.contact_bottom_dialog_never_show_dialog_checkbox)

        try {
            selectedContact = intent.getParcelableExtra(ActivityConstantCode.EXTRA_DATA)
            Timber.d("[EXTRAS] input extras: $selectedContact")
            prefillContactData(selectedContact!!)
        } catch (e: Exception) {
            Timber.d("[EXTRAS] unable to receive input extras")
        }
        contactCancelButton?.setOnClickListener {
            try {
                contactBottomDialog?.dismiss()
                setActivityResult(ActivityConstantCode.RESULT_OK_CREATED, finishedTransaction ?: TransactionModel())
            } catch (e: Exception) {

            }
        }
        contactSubmitButton?.setOnClickListener {
            //update contact data
//            selectedContact?.let {
//                it.address = editTextAddress?.text.toString()
//                it.name = editTextBuyer?.text.toString()
//                var channel =
//                    it.channels.filter { channel -> channel.type == editTextChannel?.text.toString() }
//                if (channel.isNotEmpty()) {
//                    //replace data
//                    val channelIndex = it.channels.indexOf(channel[0])
//                    if (channelIndex > -1) {
//                        it.channels[channelIndex].account = editTextPhone?.text.toString()
//                        it.channels[channelIndex].asset = selectedChannel?.assetUrl ?: ""
//                    } else {
//                        it.channels.add(ContactChannelModel(selectedChannel?.assetDesc?: "", editTextPhone?.text.toString()))
//                    }
//                } else {
//                    it.channels.toMutableList().add(
//                        ContactChannelModel(
//                            selectedChannel?.assetDesc ?: "",
//                            editTextPhone?.text.toString(),
//                            selectedChannel?.assetUrl ?: ""
//                        )
//                    )
//                }
//
//            }

            //submit contact data
            val updateModel = PostUpdateContactByTransactionIdRequest(finishedTransaction?._id ?: "")
            contactViewModel.updateByTransactionId(selectedContact?._id ?: "", updateModel, {
                contactBottomDialog?.dismiss()
                progressSubmit(false)
                setActivityResult(ActivityConstantCode.RESULT_OK_CREATED, finishedTransaction ?: TransactionModel())
            }, {
                progressSubmit(false)
                Toast.makeText(this, "Gagal update kontak", Toast.LENGTH_SHORT).show()
            })
        }

        contactNeverShowAgainButton?.isSelected = false
        contactNeverShowAgainButton?.setOnCheckedChangeListener { buttonView, isChecked ->
            AppPersistence.showContactUpdateMessage = isChecked.not()
        }

        mode = intent.getIntExtra(MODE, -1)

        when (mode) {
            CREATE -> {
                layoutTitleText?.text = resources.getString(R.string.form_trx_create)
                disableFormInput(false)
                initBooleanArray(isValidFormArray, true)
                formValidation()
            }
            EDIT -> {
                intent.getParcelableExtra<TransactionModel>(EXTRA_DATA).let { model ->
                    if (model != null) {
                        currentTransaction = model
                        layoutTitleText?.text = resources.getString(R.string.form_trx_edit)
                        btnSubmitText?.text = resources.getString(R.string.form_trx_btn_edit)
                        disableFormInput(false)
                        setupDisplay(model)
                        initBooleanArray(isValidFormArray, true)
                        isValidFormArray[4] = true
                        isValidFormArray[5] = true
                        formValidation()
                    }
                }
            }
            EDIT_COMPLETE -> {
                intent.getParcelableExtra<TransactionModel>(EXTRA_DATA).let { model ->
                    if (model != null) {
                        currentTransaction = model
                        layoutTitleText?.text = resources.getString(R.string.form_trx_edit)
                        btnSubmitText?.text = resources.getString(R.string.form_trx_btn_edit)
                        disableFormCompleteState()
                        setupDisplay(model)
                        initBooleanArray(isValidFormArray, true)
                        isValidFormArray[4] = true
                        isValidFormArray[5] = true
                        formValidation()
                    }
                }
            }
            FROM_KEYBOARD -> {
                intent.getParcelableExtra<TransactionModel>(EXTRA_DATA).let { model ->
                    if (model != null) {
                        layoutTitleText?.text = resources.getString(R.string.form_trx_create)
                        disableFormInput(false)
                        setupDisplay(model)
                        initBooleanArray(isValidFormArray, true)
                        formValidation()
                    }
                }
            }
        }

        btnSubmit?.let { button -> button.setOnClickListener { onClicked(button) } }
        btnBack?.let { button -> button.setOnClickListener { onClicked(button) } }

        checkThenRequestContactPermission()
        editTextChannel?.setOnClickListener {
            val channel = editTextChannel?.text.toString()
            spinnerChannelSelector = SpinnerChannelSelector().newInstance()

            if (selectedChannel == null) {
                selectedChannel = PropertiesModel("", "", "", channel)
            }

            spinnerChannelSelector?.openSelector(supportFragmentManager, selectedChannel!!)
            spinnerChannelSelector?.onItemClick = {
                selectedChannel = it
                selectedChannel!!.setTitleText(channelAccountTitle!!, editTextPhone!!)

//                if (selectedChannel?.assetDesc == "Belum ada") {
//                    editTextPhone?.isFocusable = false
//                    editTextPhone?.isCursorVisible = false
//
//                    channelAccountTitle?.text = "Nomor telepon"
//                } else {
//                    editTextPhone?.isFocusableInTouchMode = true
//                    editTextPhone?.isCursorVisible = true
//                    var string = resources.getString(R.string.form_trx_phone)
//
//                    if (selectedChannel?.assetDesc == "WhatsApp")
//                        string = "Nomor WhatsApp"
//                    else if (selectedChannel?.assetDesc == "WhatsApp Business")
//                        string = "Nomor WhatsApp"
//                    else if (selectedChannel?.assetDesc == "Line")
//                        string = "Akun Line"
//                    else if (selectedChannel?.assetDesc == "Facebook Messenger")
//                        string = "Nama Profil"
//                    else if (selectedChannel?.assetDesc == "Instagram")
//                        string = "Akun Instagram"
//                    else if (selectedChannel?.assetDesc == "Bukalapak Chat")
//                        string = "Akun Bukalapak"
//                    else if (selectedChannel?.assetDesc == "Tokopedia Chat")
//                        string = "Akun Tokopedia"
//                    else if (selectedChannel?.assetDesc == "Shopee Chat")
//                        string = "Akun Shopee"
//
//                    channelAccountTitle?.text = string
//                }

                editTextChannel?.setText(it.assetDesc)
                constructChannel(editTextChannel!!, it.assetUrl)
            }
        }

        editTextPayment?.setOnClickListener {
            spinnerBankSelector = SpinnerBankSelector().newInstance()
            spinnerBankSelector?.openSelector(supportFragmentManager, selectedBank)
            spinnerBankSelector?.onItemClick = {
                selectedBank = it

                if (it.accountNo == "Cash") {
                    editTextPayment?.setText("Cash")
                } else {
                    editTextPayment?.setText(it.bank)
                }
            }
        }

        editTextLogistic?.setOnClickListener {
            val logistic = editTextLogistic?.text.toString()
            spinnerLogisticSelector = SpinnerLogisticSelector().newInstance()

            if (selectedLogistic == null) {
                selectedLogistic = PropertiesModel("", "", "", logistic)
            }

            spinnerLogisticSelector?.openSelector(supportFragmentManager, selectedLogistic!!)
            spinnerLogisticSelector?.onItemClick = {
                selectedLogistic = it
                editTextLogistic?.setText(it.assetDesc)
            }

            formValidation()
        }

        editTextBuyer?.addTextChangedListener {
            isValidFormArray[0] = editTextLengthValidation(editTextBuyer!!, 0, 100, "editTextBuyerError")
            formValidation()
        }

        editTextPhone?.addTextChangedListener {
            isValidFormArray[1] = editTextLengthValidation(editTextPhone!!, 0, 15, "editTextPhoneError")
            formValidation()
        }

        editTextAddress?.addTextChangedListener {
            isValidFormArray[2] = editTextLengthValidation(editTextAddress!!, 0, 500, "editTextNoteError")
            formValidation()
        }

        editTextNote?.addTextChangedListener {
            isValidFormArray[3] = editTextLengthValidation(editTextNote!!, 0, 1000, "editTextNoteError")
            formValidation()
        }

        editTextPrice?.addTextChangedListener {
            isValidFormArray[4] = editTextLengthValidation(editTextPrice!!, 1, 15, "editTextPriceError", true)
            formValidation()
        }

        editTextPayment?.addTextChangedListener { text ->
            isValidFormArray[5] = text.toString().isNotEmpty()
            formValidation()
        }

        editTextdeliveryFee?.addTextChangedListener {
            isValidFormArray[6] = editTextLengthValidation(editTextdeliveryFee!!, 0, 15, "editTextdeliveryFeeError")
            formValidation()
        }

        editTextdeliveryFee?.addSeparator(editTextdeliveryFee, ".", ",")
        editTextPrice?.addSeparator(editTextPrice, ".", ",")

        formValidation()
    }

    private fun onClicked(view: View) {
        when (view.id) {
            R.id.submit_button -> {
                if (currentTransaction == null) {
                    submitForm("")
                } else {
                    currentTransaction?.let { submitForm(it._id) }
                }

            }
            R.id.back_button -> {
                setResult(RESULT_CANCELED)
                finish()
            }
        }

    }

    private fun submitForm(_id: String) {

        if (!isNotValid()) {
            progressSubmit(true)

            var deliveryFee = 0.0
            var price = 0.0

            editTextdeliveryFee?.let {
                if (!it.text.isNullOrBlank()) {
                    deliveryFee = it.text.toString().replace(".", "").toDouble()
                }
            }

            editTextPrice?.let {
                if (!it.text.isNullOrBlank()) {
                    price = it.text.toString().replace(".", "").toDouble()
                }
            }
            if (editTextdeliveryFee!!.text.toString() == "") {
                editTextdeliveryFee?.setText("0")
            }


            val model = TransactionModel(
                _id = _id,
                buyer = editTextBuyer?.text.toString(),
                channel = editTextChannel?.text.toString(),
                phone = selectedContact?.phoneNumber ?: editTextPhone?.text.toString(),
                address = editTextAddress?.text.toString(),
                notes = editTextNote?.text.toString(),
                price = price,
                payingMethod = editTextPayment?.text.toString(),
                bankType = selectedBank.bankType.toString(),
                bankAccountNo = selectedBank.accountNo.toString(),
                bankAccountName = selectedBank.accountHolder.toString(),
                logistic = editTextLogistic?.text.toString(),
                deliveryFee = deliveryFee,
                channelAccount = editTextPhone?.text.toString()
            )

            when (mode) {
                CREATE -> {
                    transactionViewModel?.createTransaction(
                        model,
                        onSuccess = {
                            progressSubmit(false)
                            if (it.isProfileChange) {
                                contactBottomDialog?.show()
                                finishedTransaction = model.copy(_id = it._id)
                            } else {
                                setActivityResult(ActivityConstantCode.RESULT_OK_CREATED, finishedTransaction ?: model)
                            }
                            //showToast(resources.getString(R.string.kobold_transaction_action_save_success))
                        },
                        onError = {
                            progressSubmit(false)
                            if (ErrorResponseValidator.isSessionExpiredResponse(it))
                                DashboardSessionExpiredEventHandler(this).onSessionExpired()
                        }
                    )
                }
                else -> {
                    transactionViewModel?.updateTransactionById(
                        _id,
                        model,
                        onSuccess = {
                            setActivityResult(ActivityConstantCode.RESULT_OK_UPDATED, model)
                            progressSubmit(false)
                            //showToast(resources.getString(R.string.kobold_transaction_action_save_success))
                        },
                        onError = {
                            super.finish()
                            progressSubmit(false)
                            if (ErrorResponseValidator.isSessionExpiredResponse(it))
                                DashboardSessionExpiredEventHandler(this).onSessionExpired()
                        }
                    )
                }
            }

        } else {
            showToast("Form Validation Failed!!")
        }
    }

    private fun formValidation() {
        isValidFormArray.forEach { println(it.toString()) }
        saveButtonDisable(isNotValid())
    }

    private fun progressSubmit(_isLoading: Boolean) {
        isLoading = _isLoading
        btnSubmit?.isEnabled = !_isLoading
        btnSubmitText?.isVisible = !_isLoading
        btnSubmitProgress?.isVisible = _isLoading
        btnSubmitText?.setBackgroundColor(resources.getColor(R.color.kobold_blue_button_disabled))
    }

    private fun disableFormInput(_isEditable: Boolean) {
        editTextBuyer?.isEnabled = !_isEditable
        editTextChannel?.isEnabled = !_isEditable
        editTextPhone?.isEnabled = !_isEditable
        editTextAddress?.isEnabled = !_isEditable
        editTextNote?.isEnabled = !_isEditable
        editTextPrice?.isEnabled = !_isEditable
        editTextPayment?.isEnabled = !_isEditable
        editTextLogistic?.isEnabled = !_isEditable
        editTextdeliveryFee?.isEnabled = !_isEditable
    }

    private fun saveButtonDisable(_isDisable: Boolean) {
        if (_isDisable) {
            btnSubmit?.isEnabled = false
            btnSubmitText?.setBackgroundColor(resources.getColor(R.color.kobold_blue_button_disabled))
            btnSubmit?.preventCornerOverlap = true
        } else {
            btnSubmit?.isEnabled = true
            btnSubmitText?.setBackgroundColor(resources.getColor(R.color.colorPrimary50))
            btnSubmit?.preventCornerOverlap = true
        }
    }

    private fun setupDisplay(model: TransactionModel) {
        model.buyer.let { s -> editTextBuyer?.setText(s) }
        model.channel.let { s -> editTextChannel?.setText(s) }
        constructChannel(editTextChannel!!, model.channelAsset)
        model.phone.let { s -> editTextPhone?.setText(s) }
        model.address.let { s -> editTextAddress?.setText(s) }
        model.notes.let { s -> editTextNote?.setText(s) }
        model.price.let { s -> editTextPrice?.setText(CurrencyUtility.currencyFormatterNoPrepend(s)) }
        model.payingMethod.let { s -> editTextPayment?.setText(s) }
        model.logistic.let { s -> editTextLogistic?.setText(s) }
        model.deliveryFee.let { s -> editTextdeliveryFee?.setText(CurrencyUtility.currencyFormatterNoPrepend(s)) }

        selectedChannel = PropertiesModel("", "", model.channelAsset, model.channel)
        selectedBank = BankModel(
            "",
            model.bankType!!,
            model.payingMethod,
            model.bankAccountNo,
            model.bankAccountName,
            model.bankAsset
        )
        selectedLogistic = PropertiesModel("", "", model.logisticAsset, model.logistic)
    }

    private fun constructChannel(editText: EditText, assetUrl: String) {
        Glide.with(this).load(assetUrl).apply(RequestOptions().fitCenter()).into(
            object : CustomTarget<Drawable>(50, 50) {
                override fun onLoadCleared(placeholder: Drawable?) {
                    editText.setCompoundDrawablesWithIntrinsicBounds(
                        placeholder, null,
                        resources.getDrawable(R.drawable.ic_subdued, null), null
                    )
                    editText.compoundDrawablePadding = 12
                }

                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    editText.setCompoundDrawablesWithIntrinsicBounds(
                        resource, null,
                        resources.getDrawable(R.drawable.ic_subdued, null), null
                    )
                    editText.compoundDrawablePadding = 12
                }
            }
        )
    }

    private fun disableFormCompleteState() {
        val textColor = this.resources.getColor(R.color.colorEditTextDisableText)
        val backgroundColor = this.resources.getColor(R.color.colorEditTextDisable)
        val isEditable = false

        editTextChannel?.isEnabled = isEditable
        editTextChannel?.setTextColor(textColor)
        editTextChannel?.isFocusableInTouchMode = isEditable
        editTextChannel?.isFocusable = isEditable
        editTextChannelLayout?.setBackgroundColor(backgroundColor)

        editTextAddress?.isEnabled = isEditable
        editTextAddress?.setTextColor(textColor)
        editTextAddress?.isFocusableInTouchMode = isEditable
        editTextAddress?.isFocusable = isEditable
        editTextAddressLayout?.setBackgroundColor(backgroundColor)

        editTextNote?.isEnabled = isEditable
        editTextNote?.setTextColor(textColor)
        editTextNote?.isFocusableInTouchMode = isEditable
        editTextNote?.isFocusable = isEditable
        editTextNoteLayout?.setBackgroundColor(backgroundColor)

        editTextPrice?.isEnabled = isEditable
        editTextPrice?.setTextColor(textColor)
        editTextPrice?.isFocusableInTouchMode = isEditable
        editTextPrice?.isFocusable = isEditable
        editTextPriceLayout?.setBackgroundColor(backgroundColor)

        editTextPayment?.isEnabled = isEditable
        editTextPayment?.setTextColor(textColor)
        editTextPayment?.isFocusableInTouchMode = isEditable
        editTextPayment?.isFocusable = isEditable
        editTextPaymentLayout?.setBackgroundColor(backgroundColor)
        editTextLogistic?.isEnabled = isEditable

        editTextLogistic?.setTextColor(textColor)
        editTextLogistic?.isFocusableInTouchMode = isEditable
        editTextLogistic?.isFocusable = isEditable
        editTextLogisticLayout?.setBackgroundColor(backgroundColor)

        editTextdeliveryFee?.isEnabled = isEditable
        editTextdeliveryFee?.setTextColor(textColor)
        editTextdeliveryFee?.isFocusableInTouchMode = isEditable
        editTextdeliveryFee?.isFocusable = isEditable
        editTextDeliveryFeeLayout?.setBackgroundColor(backgroundColor)
    }

    private fun setActivityResult(result: Int, model: TransactionModel) {
        val intent = Intent()
        intent.putExtra(EXTRA_DATA, model)
        setResult(result, intent)
        finish()
    }

    private fun addErrorTextView(
        editText: EditText,
        viewTag: String,
        message: String = resources.getString(R.string.template_text_error_length)
    ) {
        // Create TextView programmatically.
        val errorTextView = TextView(this)
        val textInputLayout = editText.parent.parent as TextInputLayout
        val errorTextId = textInputLayout.findViewWithTag<TextView>(viewTag)

        if (errorTextId == null) {
            errorTextView.tag = viewTag
            errorTextView.text = message
            errorTextView.setTextAppearance(errorTextView.context, R.style.error_edit_text)
            textInputLayout.addView(errorTextView, 1)
            textInputLayout.boxStrokeColor = resources.getColor(R.color.colorEditTextError)
        }

    }

    private fun removeErrorTextView(editText: EditText, viewTag: String) {
        val textInputLayout = editText.parent.parent as TextInputLayout
        textInputLayout.let { til ->
            til.findViewWithTag<TextView>(viewTag).let {
                til.removeView(it)
                til.boxStrokeColor = resources.getColor(R.color.colorEditTextDefault)
            }
        }
    }

    private fun editTextLengthValidation(
        editText: EditText,
        minChar: Int,
        maxChar: Int,
        viewTag: String,
        required: Boolean = false,
        message: String = resources.getString(R.string.template_text_error_length)
    ): Boolean {

        if (required && editText.text.length < minChar) {
            addErrorTextView(editText, viewTag, "Wajib diisi")
        }

        if (editText.text.length > maxChar || editText.text.length < minChar) {
            addErrorTextView(editText, viewTag, message)
            return false
        } else {
            removeErrorTextView(editText, viewTag)
            return true
        }
    }

    private fun initBooleanArray(arr: BooleanArray, initVal: Boolean) {
        for (i in arr.indices) {
            arr[i] = initVal
        }

        // init required field
        isValidFormArray[4] = false
        isValidFormArray[5] = false
    }

    private fun isNotValid(): Boolean {
        val test = isValidFormArray.any { b -> b.equals(false) }
        println("isNotValid :: $test")
        return test
    }

    private fun checkThenRequestContactPermission() {
        val contactpermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
        when (contactpermission) {
            PackageManager.PERMISSION_GRANTED -> {
                //populate contact list then initiate adapter
                val contactList = getContactList(this)
                Timber.d("[CONTACT] getContactList: $contactList")
                initiateAutocompleteAdapter(contactList)
            }
            else -> {
                //request permission
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_CONTACTS),
                    READ_CONTACT_PERMISSION_CODE
                )
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            READ_CONTACT_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //populate contact list then initiate adapter
                    val contactList = getContactList(this)
                    Timber.d("[CONTACT] getContactList: $contactList")
                    initiateAutocompleteAdapter(contactList)
                } else {
                    //initiate empty adapter
                    initiateAutocompleteAdapter(arrayListOf())
                }
            }
        }
    }

    private fun initiateAutocompleteAdapter(contactList: ArrayList<ContactModel>) {
        contactAutocompleteAdapter =
            ContactAutocompleteAdapter(this, contactList.distinctBy { it.name }.distinctBy { it.phoneNumber })
        editTextBuyer?.setAdapter(contactAutocompleteAdapter)
        editTextBuyer?.setOnItemClickListener { adapterView, view, i, l ->
            try {
                val contact = adapterView.getItemAtPosition(i) as ContactModel
                selectedContact = contact
                prefillContactData(selectedContact!!)
            } catch (e: Exception) {

            }
        }

    }

    private fun prefillContactData(contact: ContactModel) {
        editTextBuyer?.setText(contact.name)
        editTextAddress?.setText(contact.address)
        contactChannels.clear()
        contactChannels.addAll(contact.channels)
        editTextChannel?.setText("")
        constructChannel(editTextChannel!!, "")
        editTextPhone?.setText("")
        //set contact channel from first index if available or prepare then show if channel selected
        if (contactChannels.size > 0) {
            editTextChannel?.setText(contactChannels[0].type)
            constructChannel(editTextChannel!!, contactChannels[0].asset)
            editTextPhone?.setText(contactChannels[0].account)
        } else {
            editTextChannel?.setText("WhatsApp")
            constructChannel(
                editTextChannel!!,
                "https://kobold-test-asset.s3.ap-southeast-1.amazonaws.com/public/ic_channel_whatsApp.png"
            )
            editTextPhone?.setText(contact.phoneNumber)
        }
    }
}
