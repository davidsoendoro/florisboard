package com.kokatto.kobold.dashboardcreatetransaction

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.BankModel
import com.kokatto.kobold.api.model.basemodel.TransactionModel
import com.kokatto.kobold.dashboardcreatetransaction.spinner.SpinnerBankSelector
import com.kokatto.kobold.dashboardcreatetransaction.spinner.SpinnerChannelSelector
import com.kokatto.kobold.dashboardcreatetransaction.spinner.SpinnerLogisticSelector
import com.kokatto.kobold.extension.showToast

class InputActivity : AppCompatActivity() {

    private companion object {
        const val CREATE = -1
        const val EDIT = 1
        const val MODE = "MODE"
        const val EXTRA_ID = "EXTRA_ID"
    }

    private var editTextBuyer: EditText? = null
    private var editTextChannel: EditText? = null
    private var editTextPhone: EditText? = null
    private var editTextAddress: EditText? = null
    private var editTextNote: EditText? = null
    private var editTextPrice: EditText? = null
    private var editTextPayment: EditText? = null
    private var editTextLogistic: EditText? = null
    private var editTextdeliveryFee: EditText? = null
    private var btnSubmit: CardView? = null
    private var btnSubmitText: TextView? = null
    private var btnSubmitProgress: ProgressBar? = null
    private var layoutTitleText: TextView? = null

    private var mode: Int? = -1
    private var isValidForm: Boolean = false
    private var isEditable: Boolean = true
    private var isLoading: Boolean = false
    private var extraID: String = ""

    private var selectedBank: BankModel? = null

    private var transactionViewModel: TransactionViewModel? = TransactionViewModel()
    private var spinnerChannelSelector: SpinnerChannelSelector? = SpinnerChannelSelector()
    private var spinnerBankSelector: SpinnerBankSelector? = SpinnerBankSelector()
    private var spinnerLogisticSelector: SpinnerLogisticSelector? = SpinnerLogisticSelector()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_transaction)

        editTextBuyer = findViewById<EditText>(R.id.edittext_buyername)
        editTextChannel = findViewById<EditText>(R.id.edittext_channel)
        editTextPhone = findViewById<EditText>(R.id.edittext_phone)
        editTextAddress = findViewById<EditText>(R.id.edittext_buyeraddress)
        editTextNote = findViewById<EditText>(R.id.edittext_note)
        editTextPrice = findViewById<EditText>(R.id.edittext_price)
        editTextPayment = findViewById<EditText>(R.id.edittext_paymentmethod)
        editTextLogistic = findViewById<EditText>(R.id.edittext_logistic)
        editTextdeliveryFee = findViewById<EditText>(R.id.edittext_deliveryfee)
        btnSubmit = findViewById<CardView>(R.id.submit_button)
        btnSubmitText = findViewById<TextView>(R.id.submit_button_text)
        btnSubmitProgress = findViewById<ProgressBar>(R.id.submit_button_loading)
        layoutTitleText = findViewById<TextView>(R.id.title_text)

        mode = intent.getIntExtra(MODE, -1)

        if (mode == CREATE) {
            layoutTitleText?.text = resources.getString(R.string.form_trx_create)
            disableFormInput(false)
        } else {
            layoutTitleText?.text = resources.getString(R.string.form_trx_edit)
            disableFormInput(true)
        }

        btnSubmit?.let { button -> button.setOnClickListener { onClicked(button) } }

        editTextPrice?.addTextChangedListener { text ->
            formValidation()
        }

        editTextChannel?.setOnClickListener {
            val channel = editTextChannel?.text.toString()
            spinnerChannelSelector = SpinnerChannelSelector().newInstance()
            spinnerChannelSelector?.openSelector(supportFragmentManager, SpinnerChannelItem(channel))
            spinnerChannelSelector?.onItemClick = {
                editTextChannel?.setText(it.label)
            }
        }

        editTextPayment?.setOnClickListener {
            spinnerBankSelector = SpinnerBankSelector().newInstance()

            if(selectedBank == null) {
                selectedBank = BankModel("","","Cash","")
            }

            spinnerBankSelector?.openSelector(supportFragmentManager, selectedBank!!)
            spinnerBankSelector?.onItemClick = {
                selectedBank = it

                if (it.accountNo == "Cash") {
                    editTextPayment?.setText(it.accountNo)
                } else {
                    editTextPayment?.setText(it.bank)
                }

                formValidation()
            }
        }

        editTextLogistic?.setOnClickListener {
            val logistic = editTextLogistic?.text.toString()
            spinnerLogisticSelector = SpinnerLogisticSelector().newInstance()
            spinnerLogisticSelector?.openSelector(supportFragmentManager, SpinnerLogisticItem(logistic))
            spinnerLogisticSelector?.onItemClick = {
                editTextLogistic?.setText(it.label)
            }
        }

        saveButtonDisable(true)
    }

    private fun onClicked(view: View) {
        when (view.id) {
            R.id.submit_button -> {
                submitForm(extraID)
            }
        }

    }

    private fun submitForm(_id: String) {

        if (isValidForm) {
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
                phone = editTextPhone?.text.toString(),
                address = editTextAddress?.text.toString(),
                notes = editTextNote?.text.toString(),
                price = editTextPrice?.text.toString().toInt(),
                payingMethod = editTextPayment?.text.toString(),
                logistic = editTextLogistic?.text.toString(),
                deliveryFee = deliveryFee,
                latestStatus = "",
                createdAt = 0L
            )

            if (mode == CREATE) {
                transactionViewModel?.createTransaction(
                    model,
                    onSuccess = {
                        progressSubmit(false)
                        showToast(resources.getString(R.string.template_create_success))
                    },
                    onError = {
                        progressSubmit(false)
                        showToast(it)
                    }
                )
            } else {
                transactionViewModel?.updateTransactionById(
                    _id,
                    model,
                    onSuccess = {
                        progressSubmit(false)
                        showToast(resources.getString(R.string.template_create_success))
                    },
                    onError = {
                        progressSubmit(false)
                        showToast(it)
                    }
                )
            }
        } else {
            showToast("Form Validation Failed!!")
        }
    }

    private fun formValidation() {
        println("formValidation")
        editTextPrice?.text?.let { price ->
            editTextPayment?.text.let { payment ->
                println("payment " + payment.toString())
                println("price " + price.toString())
                isValidForm = price.length > 0 && payment != null
                println("isValidForm " + isValidForm.toString())
                saveButtonDisable(!isValidForm)
            }
        }
    }

    private fun progressSubmit(_isLoading: Boolean) {
        isLoading = _isLoading
        btnSubmit?.isEnabled = !_isLoading
        btnSubmitText?.isVisible = !_isLoading
        btnSubmitProgress?.isVisible = _isLoading
        btnSubmit?.setBackgroundColor(resources.getColor(R.color.kobold_blue_button_disabled))
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
}
