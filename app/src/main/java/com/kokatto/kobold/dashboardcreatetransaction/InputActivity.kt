package com.kokatto.kobold.dashboardcreatetransaction

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.textfield.TextInputLayout
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.BankModel
import com.kokatto.kobold.api.model.basemodel.PropertiesModel
import com.kokatto.kobold.api.model.basemodel.TransactionModel
import com.kokatto.kobold.constant.ActivityConstantCode.Companion.BANK_TYPE_OTHER
import com.kokatto.kobold.dashboardcreatetransaction.spinner.SpinnerBankSelector
import com.kokatto.kobold.dashboardcreatetransaction.spinner.SpinnerChannelSelector
import com.kokatto.kobold.dashboardcreatetransaction.spinner.SpinnerLogisticSelector
import com.kokatto.kobold.extension.showToast

class InputActivity : AppCompatActivity() {

    companion object {
        const val CREATE = -1
        const val EDIT = 1
        const val EDIT_COMPLETE = 2
        const val FROM_KEYBOARD = 3
        const val MODE = "MODE"
        const val EXTRA_ID = "EXTRA_ID"
        const val EXTRA_DATA = "EXTRA_DATA"
    }

    private var editTextBuyer: EditText? = null
    private var editTextChannel: EditText? = null
    private var editTextChannelLayout: TextInputLayout? = null
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
    private var isEditable: Boolean = true
    private var isLoading: Boolean = false
    private var extraID: String = ""

    private var selectedBank: BankModel? = null
    private var selectedLogistic: PropertiesModel? = null
    private var selectedChannel: PropertiesModel? = null
    private var currentTransaction: TransactionModel? = null

    private var transactionViewModel: TransactionViewModel? = TransactionViewModel()
    private var spinnerChannelSelector: SpinnerChannelSelector? = SpinnerChannelSelector()
    private var spinnerBankSelector: SpinnerBankSelector? = SpinnerBankSelector()
    private var spinnerLogisticSelector: SpinnerLogisticSelector? = SpinnerLogisticSelector()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_transaction)

        editTextBuyer = findViewById<EditText>(R.id.edittext_buyername)
        editTextChannel = findViewById<EditText>(R.id.edittext_channel)
        editTextChannelLayout = findViewById<TextInputLayout>(R.id.edittext_channel_layout)
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


        editTextChannel?.setOnClickListener {
            val channel = editTextChannel?.text.toString()
            spinnerChannelSelector = SpinnerChannelSelector().newInstance()

            if (selectedChannel == null) {
                selectedChannel = PropertiesModel("", "", "", channel)
            }

            spinnerChannelSelector?.openSelector(supportFragmentManager, selectedChannel!!)
            spinnerChannelSelector?.onItemClick = {
                selectedChannel = it
                editTextChannel?.setText(it.assetDesc)
                constructChannel(editTextChannel!!, it.assetUrl)
            }
        }

        editTextPayment?.setOnClickListener {
            spinnerBankSelector = SpinnerBankSelector().newInstance()

            if (selectedBank == null) {
                selectedBank = BankModel("", BANK_TYPE_OTHER, "Cash", "", "", "")
            }

            spinnerBankSelector?.openSelector(supportFragmentManager, selectedBank!!)
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
            isValidFormArray[4] =
                editTextLengthValidation(editTextPrice!!, 1, 15, "editTextPriceError", "Required Field")
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

            var deliveryFee = 0

            editTextdeliveryFee?.let { editText ->
                if (!editText.text.isEmpty()) {
                    deliveryFee = editText.text.toString().toInt()
                }
            }

            val model = TransactionModel(
                _id = _id,
                buyer = editTextBuyer?.text.toString(),
                channel = editTextChannel?.text.toString(),
                channelAsset = selectedChannel?.assetUrl.toString(),
                phone = editTextPhone?.text.toString(),
                address = editTextAddress?.text.toString(),
                notes = editTextNote?.text.toString(),
                price = editTextPrice?.text.toString().toInt(),
                payingMethod = editTextPayment?.text.toString(),
                bankType = selectedBank?.bankType.toString(),
                bankAccountNo = selectedBank?.accountNo.toString(),
                bankAccountName = selectedBank?.accountHolder.toString(),
                bankAsset = selectedBank?.asset.toString(),
                logistic = editTextLogistic?.text.toString(),
                logisticAsset = selectedLogistic?.assetUrl.toString(),
                deliveryFee = deliveryFee,
                latestStatus = "",
                createdAt = 0L
            )

            when (mode) {
                CREATE -> {
                    transactionViewModel?.createTransaction(
                        model,
                        onSuccess = {
                            setActivityResultOK(model)
                            progressSubmit(false)
                        },
                        onError = {
                            progressSubmit(false)
                            showToast(it)
                        }
                    )
                }
                else -> {
                    transactionViewModel?.updateTransactionById(
                        _id,
                        model,
                        onSuccess = {
                            setActivityResultOK(model)
                            progressSubmit(false)
                        },
                        onError = {
                            super.finish()
                            progressSubmit(false)
                            showToast(it)
                        }
                    )
                }
            }

        } else {
            showToast("Form Validation Failed!!")
        }
    }

    private fun formValidation() {
        println("formValidation ::")
        isValidFormArray.forEach { println(it.toString()) }
        saveButtonDisable(isNotValid())
    }

    private fun progressSubmit(_isLoading: Boolean) {
        isLoading = _isLoading
        btnSubmit?.isEnabled = !_isLoading
        btnSubmitText?.isVisible = !_isLoading
        btnSubmitProgress?.isVisible = _isLoading
        btnSubmit?.setBackgroundColor(resources.getColor(R.color.kobold_blue_button_disabled, null))
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
            btnSubmitText?.setBackgroundColor(resources.getColor(R.color.kobold_blue_button_disabled, null))
            btnSubmit?.preventCornerOverlap = true
        } else {
            btnSubmit?.isEnabled = true
            btnSubmitText?.setBackgroundColor(resources.getColor(R.color.colorPrimary50, null))
            btnSubmit?.preventCornerOverlap = true
        }
    }

    private fun setupDisplay(model: TransactionModel) {
        model.buyer.let { s -> editTextBuyer?.setText(s) }
        model.channel.let { s -> editTextChannel?.setText(s) }
        constructChannel(editTextChannel!!, model.channelAsset)
        selectedChannel = PropertiesModel("", "", model.channelAsset, model.channel)
        model.phone.let { s -> editTextPhone?.setText(s) }
        model.address.let { s -> editTextAddress?.setText(s) }
        model.notes.let { s -> editTextNote?.setText(s) }
        model.price.let { s -> editTextPrice?.setText(s.toString()) }
        model.payingMethod.let { s -> editTextPayment?.setText(s) }

        if(model.bankType !=null) {
            selectedBank = BankModel(
                "",
                model.bankType, model.payingMethod, model.bankAccountNo, model.bankAccountName, model.bankAsset
            )
        }

        model.logistic.let { s -> editTextLogistic?.setText(s) }
        selectedLogistic = PropertiesModel("", "", model.logisticAsset, model.logistic)
        model.deliveryFee.let { s -> editTextdeliveryFee?.setText(s.toString()) }
    }

    private fun constructChannel(editText: EditText, assetUrl: String) {
        println("constructChannel :: constructChannel")
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
        val textColor = this.resources.getColor(R.color.colorEditTextDisableText, null)
        val backgroundColor = this.resources.getColor(R.color.colorEditTextDisable, null)
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

    private fun setActivityResultOK(model: TransactionModel) {
        val intent = Intent()
        intent.putExtra(EXTRA_DATA, model)
        setResult(RESULT_OK, intent)
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
            errorTextView.setTextAppearance(R.style.error_edit_text)
            textInputLayout.addView(errorTextView, 1)
            textInputLayout.boxStrokeColor = resources.getColor(R.color.colorEditTextError, null)
        }

    }

    private fun removeErrorTextView(editText: EditText, viewTag: String) {
        val textInputLayout = editText.parent.parent as TextInputLayout
        textInputLayout.let { til ->
            til.findViewWithTag<TextView>(viewTag).let {
                til.removeView(it)
                til.boxStrokeColor = resources.getColor(R.color.colorEditTextDefault, null)
            }
        }
    }

    private fun editTextLengthValidation(
        editText: EditText,
        minChar: Int,
        maxChar: Int,
        viewTag: String,
        message: String = resources.getString(R.string.template_text_error_length)
    ): Boolean {

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

}
