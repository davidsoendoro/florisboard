package com.kokatto.kobold.bank

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.BankModel
import com.kokatto.kobold.api.model.basemodel.PropertiesModel
import com.kokatto.kobold.bank.dialog.DialogBankCancel
import com.kokatto.kobold.bank.dialog.DialogBankDelete
import com.kokatto.kobold.bank.recylerAdapeter.BankSpinnerAdapter
import com.kokatto.kobold.constant.ActivityConstantCode
import com.kokatto.kobold.constant.ActivityConstantCode.Companion.BANK_TYPE_BANK
import com.kokatto.kobold.constant.PropertiesTypeConstant
import com.kokatto.kobold.databinding.ActivityBankInputBinding
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uiBinding = ActivityBankInputBinding.inflate(layoutInflater).apply {
            setContentView(root)
        }

        mode = intent.getIntExtra(ActivityConstantCode.EXTRA_MODE, ActivityConstantCode.EXTRA_CREATE)
        activityModeFactory(mode!!)

        uiBinding.backButton.setOnClickListener {
            if (isChange.get()) {
                confirmCancelData()
            } else {
                onBackPressed()
            }
        }

        uiBinding.edittextBankAccount.addTextChangedListener {
            isChange.set(true)
        }

        uiBinding.edittextBankHolder.addTextChangedListener {
            isChange.set(true)
        }

        uiBinding.submitButton.setOnClickListener {
            submitData()
        }

        uiBinding.deleteButton.setOnClickListener {
            confirmDeleteData()
        }

        uiBinding.recyclerViewBank.layoutManager = LinearLayoutManager(this)
        uiBinding.recyclerViewBank.adapter = BankSpinnerAdapter(this, pickOptions, selectedOption) { result ->
            selectedOption = result
            isChange.set(true)
        }

        apiCallBankSelection()

    }

    override fun onBackPressed() {
        if (isChange.get()) {
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
            }
            ActivityConstantCode.EXTRA_EDIT -> {
                uiBinding.titleText.text = resources.getString(R.string.kobold_bank_edit_toolbar_title)
                uiBinding.deleteButton.visibility = View.VISIBLE
                intent.getParcelableExtra<BankModel>(ActivityConstantCode.EXTRA_DATA).let {
                    if (it != null) {
                        currentBank = it
                        selectedOption = PropertiesModel(
                            "",
                            BANK_TYPE_BANK,
                            it.asset,
                            it.bank
                        )
                        constructDataFormIntentData(it)
                    }
                }
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
                            it.assetUrl,
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
                    if (apiCallCreateBank(it)) {
                        intent.putExtra(ActivityConstantCode.EXTRA_DATA, currentBank)
                        setResult(RESULT_OK)
                        finish()
                    }
                }

            } else {

                if (currentBank != null) {
                    selectedOption.let {
                        if (it.assetDesc.equals("Other")) {
                            currentBank = BankModel(
                                currentBank!!._id,
                                "Other",
                                it.assetDesc,
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
                        if (apiCallUpdateBank(it)) {
                            intent.putExtra(ActivityConstantCode.EXTRA_DATA, currentBank)
                            setResult(RESULT_OK)
                            finish()
                        }
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
                        showToast(resources.getString(R.string.kobold_bank_input_action_delete_success))
                        setActivityResult(RESULT_OK, bank)
                    },
                    onError = {
                        showToast(it)
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
            setActivityResult(RESULT_OK, null)
        }

    }

    private fun validateSubmit(): Boolean {
        val bank = selectedOption != null
        val bankAccount = uiBinding.edittextBankAccount.text.toString().isNotEmpty()
        val bankHolder = uiBinding.edittextBankHolder.text.toString().isNotEmpty()
        return (bank && bankAccount && bankHolder)
    }

    private fun apiCallCreateBank(model: BankModel): Boolean {
        progressSubmit(true)
        dataViewModel?.create(
            model,
            onSuccess = {
                setActivityResult(RESULT_OK, model)
                progressSubmit(false)
                return@create
            },
            onError = {
                progressSubmit(false)
                showToast(it)
            }
        )

        return false
    }

    private fun apiCallUpdateBank(model: BankModel): Boolean {
        progressSubmit(true)
        dataViewModel?.updateById(
            model._id,
            model,
            onSuccess = {
                setActivityResult(RESULT_OK, model)
                progressSubmit(false)
                return@updateById
            },
            onError = {
                progressSubmit(false)
                showToast(it)
            }
        )

        return false
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
                    showToast(it)
                })
        }
    }
}
