package com.kokatto.kobold.bank

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputLayout
import com.kokatto.kobold.R
import com.kokatto.kobold.api.impl.DashboardSessionExpiredEventHandler
import com.kokatto.kobold.api.impl.ErrorResponseValidator
import com.kokatto.kobold.api.model.basemodel.BankModel
import com.kokatto.kobold.api.model.basemodel.PropertiesModel
import com.kokatto.kobold.bank.dialog.DialogBankCancel
import com.kokatto.kobold.bank.dialog.DialogBankDelete
import com.kokatto.kobold.bank.recylerAdapeter.BankSpinnerAdapter
import com.kokatto.kobold.constant.ActivityConstantCode
import com.kokatto.kobold.constant.ActivityConstantCode.Companion.BANK_TYPE_BANK
import com.kokatto.kobold.constant.ActivityConstantCode.Companion.BANK_TYPE_OTHER
import com.kokatto.kobold.constant.PropertiesTypeConstant
import com.kokatto.kobold.databinding.ActivityBankInputBinding
import com.kokatto.kobold.extension.showSnackBar
import com.kokatto.kobold.extension.showToast
import java.util.concurrent.atomic.AtomicBoolean

class BankInputActivity : AppCompatActivity() {

    lateinit var uiBinding: ActivityBankInputBinding
    private var mode: Int? = null
    private var dataViewModel: BankViewModel? = BankViewModel()
    private var currentBank: BankModel? = null
    private var pickOptions = ArrayList<PropertiesModel>()
    private var selectedOption = PropertiesModel("", "", "", "")
    private var isChange = AtomicBoolean(false)

    private var isValidFormArray: BooleanArray = BooleanArray(3)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uiBinding = ActivityBankInputBinding.inflate(layoutInflater).apply {
            setContentView(root)
        }

        mode = intent.getIntExtra(ActivityConstantCode.EXTRA_MODE, ActivityConstantCode.EXTRA_CREATE)

        uiBinding.backButton.setOnClickListener {
            onBackPressed()
        }

        uiBinding.edittextBankAccount.addTextChangedListener {
            isValidFormArray[0] =
                editTextLengthValidation(uiBinding.edittextBankAccount, true, 1, 50, "edittextBankAccountError")
            isChange.set(true)
            formValidation()
        }

        uiBinding.edittextBankHolder.addTextChangedListener {
            isValidFormArray[1] =
                editTextLengthValidation(uiBinding.edittextBankHolder, true,1, 100, "edittextBankHolderError")
            isChange.set(true)
            formValidation()
        }

        uiBinding.submitButton.setOnClickListener {
            submitData()
        }

        uiBinding.deleteButton.setOnClickListener {
            confirmDeleteData()
        }

        apiCallBankSelection()
        activityModeFactory(mode!!)


        uiBinding.recyclerViewBank.layoutManager = LinearLayoutManager(this)
        uiBinding.recyclerViewBank.adapter = BankSpinnerAdapter(this, pickOptions, selectedOption) { result ->
            selectedOption = result
            isValidFormArray[2] = true
            isChange.set(true)
            formValidation()
        }

        formValidation()
    }

    override fun onBackPressed() {
        if (isChange.get() && mode == ActivityConstantCode.EXTRA_CREATE) {
            confirmCancelData()
        } else {
            super.onBackPressed()
        }
    }

    private fun activityModeFactory(mode: Int) {
        when (mode) {
            ActivityConstantCode.EXTRA_CREATE -> {
                uiBinding.titleText.text = resources.getString(R.string.kobold_bank_input_toolbar_title)
                uiBinding.deleteButton.visibility = View.GONE
                initBooleanArray(isValidFormArray, false)
            }
            ActivityConstantCode.EXTRA_EDIT -> {
                uiBinding.titleText.text = resources.getString(R.string.kobold_bank_edit_toolbar_title)
                uiBinding.deleteButton.visibility = View.VISIBLE
                intent.getParcelableExtra<BankModel>(ActivityConstantCode.EXTRA_DATA).let {
                    if (it != null) {
                        currentBank = it

                        val bankType = currentBank?.bankType?.uppercase()
                        if (bankType == BANK_TYPE_OTHER) {
                            selectedOption = PropertiesModel(
                                "",
                                BANK_TYPE_OTHER,
                                it.asset,
                                BANK_TYPE_OTHER,
                                it.bank
                            )
                        } else {
                            selectedOption = PropertiesModel(
                                "",
                                BANK_TYPE_BANK,
                                it.asset,
                                it.bank,
                                "",
                            )
                        }

                        constructDataFormIntentData(it)
                    }
                }
                initBooleanArray(isValidFormArray, true)
            }
        }
    }

    private fun progressSubmit(_isLoading: Boolean) {
        uiBinding.submitButton.isEnabled = !_isLoading
        uiBinding.submitButtonText.isVisible = !_isLoading
        uiBinding.submitButtonLoading.isVisible = _isLoading
        if (_isLoading) {
            uiBinding.submitButton.setBackgroundColor(resources.getColor(R.color.kobold_blue_button_disabled, null))
        } else {
            uiBinding.submitButton.setBackgroundColor(resources.getColor(R.color.kobold_blue_button, null))
        }

    }

    private fun setActivityResult(result: Int, model: BankModel?) {
        intent.putExtra(ActivityConstantCode.EXTRA_DATA, model)
        setResult(result)
        finish()
    }

    private fun constructDataFormIntentData(model: BankModel) {
        uiBinding.edittextBankAccount.setText(model.accountNo)
        uiBinding.edittextBankHolder.setText(model.accountHolder)
    }

    private fun submitData() {

        if (validateSubmit()) {

            if (mode?.equals(ActivityConstantCode.EXTRA_CREATE) == true) {

                selectedOption.let {
                    if (it.assetDesc.equals("Other")) {
                        currentBank = BankModel(
                            "",
                            "Other",
                            it.param1,
                            uiBinding.edittextBankAccount.text.toString(),
                            uiBinding.edittextBankHolder.text.toString(),
                            ""
                        )
                    } else {
                        currentBank = BankModel(
                            "",
                            "bank",
                            it.assetDesc,
                            uiBinding.edittextBankAccount.text.toString(),
                            uiBinding.edittextBankHolder.text.toString(),
                            it.assetUrl
                        )
                    }
                }

                currentBank?.let {
                    apiCallCreateBank(it)
                }

            } else {

                if (currentBank != null) {
                    selectedOption.let {
                        if (it.assetDesc.equals("Other")) {
                            currentBank = BankModel(
                                currentBank!!._id,
                                "Other",
                                it.param1,
                                uiBinding.edittextBankAccount.text.toString(),
                                uiBinding.edittextBankHolder.text.toString(),
                                ""
                            )
                        } else {
                            currentBank = BankModel(
                                currentBank!!._id,
                                "Bank",
                                it.assetDesc,
                                uiBinding.edittextBankAccount.text.toString(),
                                uiBinding.edittextBankHolder.text.toString(),
                                it.assetUrl
                            )
                        }
                    }

                    currentBank?.let {
                        apiCallUpdateBank(it)
                    }
                }
            }
        } else {
            showToast("Form Not Valid")
        }


    }

    private fun confirmDeleteData() {
        val dialog = DialogBankDelete().newInstance()
        dialog?.openDialog(supportFragmentManager)

        dialog?.onConfirmClick = {
            dialog?.progressLoading(true)

            currentBank?.let { bank ->
                dataViewModel?.deleteById(
                    bank._id,
                    onSuccess = {
                        dialog?.progressLoading(false)
                        dialog?.closeDialog()
                        setActivityResult(ActivityConstantCode.RESULT_OK_DELETED, bank)
                    },
                    onError = {
                        if(ErrorResponseValidator.isSessionExpiredResponse(it))
                            DashboardSessionExpiredEventHandler(this).onSessionExpired()
                    }
                )
            }
        }
    }

    private fun confirmCancelData() {
        val dialog = DialogBankCancel().newInstance()
        dialog?.openDialog(supportFragmentManager)

        dialog?.onConfirmClick = {
            dialog?.closeDialog()
            finish()
        }

    }

    private fun validateSubmit(): Boolean {
        val bank = selectedOption != null
        val bankAccount = uiBinding.edittextBankAccount.text.toString().isNotEmpty()
        val bankHolder = uiBinding.edittextBankHolder.text.toString().isNotEmpty()
        return (bank && bankAccount && bankHolder)
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
        required: Boolean = false,
        minChar: Int,
        maxChar: Int,
        viewTag: String,
        message: String = resources.getString(R.string.template_text_error_length)
    ): Boolean {

        if(required && editText.text.length < minChar) {
            addErrorTextView(editText, viewTag, resources.getString(R.string.text_error_required))
            return false
        } else {
            if (editText.text.length > maxChar || editText.text.length < minChar) {
                addErrorTextView(editText, viewTag, message)
                return false
            } else {
                removeErrorTextView(editText, viewTag)
                return true
            }
        }
    }

    private fun initBooleanArray(arr: BooleanArray, initVal: Boolean) {
        for (i in arr.indices) {
            arr[i] = initVal
        }
    }

    private fun formValidation() {
        println("formValidation isNotValid :: ${isNotValid()}")
        if (isNotValid()) {
            uiBinding.submitButton.isEnabled = false
            uiBinding.submitButtonText.setBackgroundColor(resources.getColor(R.color.kobold_blue_button_disabled, null))
        } else {
            uiBinding.submitButton.isEnabled = true
            uiBinding.submitButtonText.setBackgroundColor(resources.getColor(R.color.kobold_blue_button, null))

        }
    }

    private fun isNotValid(): Boolean {
        return isValidFormArray.any { b -> !b }
    }

    private fun apiCallCreateBank(model: BankModel) {
        progressSubmit(true)
        dataViewModel?.create(
            model,
            onSuccess = {
                progressSubmit(false)
                setActivityResult(ActivityConstantCode.RESULT_OK_CREATED, model)
            },
            onError = {
                if(ErrorResponseValidator.isSessionExpiredResponse(it))
                    DashboardSessionExpiredEventHandler(this).onSessionExpired()
                progressSubmit(false)
                showSnackBar(uiBinding.rootLayout, resources.getString(R.string.kobold_bank_maximum), R.color.snackbar_error)
            }
        )
    }

    private fun apiCallUpdateBank(model: BankModel) {
        progressSubmit(true)
        dataViewModel?.updateById(
            model._id,
            model,
            onSuccess = {
                progressSubmit(false)
                setActivityResult(ActivityConstantCode.RESULT_OK_UPDATED, model)
            },
            onError = {
                progressSubmit(false)
                if(ErrorResponseValidator.isSessionExpiredResponse(it))
                    DashboardSessionExpiredEventHandler(this).onSessionExpired()
            }
        )
    }

    private fun apiCallBankSelection() {
        if (pickOptions.size <= 0) {
            uiBinding.fullcreenLoading.visibility = View.VISIBLE
            dataViewModel?.getStandardListProperties(
                type = PropertiesTypeConstant.bank,
                onSuccess = { it ->
                    if (it.data.size > 0) {
                        pickOptions.addAll(it.data)
                        uiBinding.recyclerViewBank.adapter?.notifyDataSetChanged()
                    }
                    uiBinding.fullcreenLoading.visibility = View.GONE
                },
                onError = {
                    uiBinding.fullcreenLoading.visibility = View.GONE
                    if(ErrorResponseValidator.isSessionExpiredResponse(it))
                        DashboardSessionExpiredEventHandler(this).onSessionExpired()
                })
        }
    }
}
