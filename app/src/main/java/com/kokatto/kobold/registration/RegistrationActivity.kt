package com.kokatto.kobold.registration

import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputLayout
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.BusinessFieldModel
import com.kokatto.kobold.api.model.basemodel.BusinessTypeModel
import com.kokatto.kobold.api.model.basemodel.toBundle
import com.kokatto.kobold.api.model.basemodel.toTextFormat
import com.kokatto.kobold.api.model.request.PostCreateMerchantRequest
import com.kokatto.kobold.extension.createBottomSheetDialog
import com.kokatto.kobold.extension.showSnackBar
import com.kokatto.kobold.registration.spinner.DialogBusinessFieldSelector
import com.kokatto.kobold.registration.spinner.DialogBusinessTypeSelector
import com.kokatto.kobold.registration.viewmodel.MerchantViewModel

class RegistrationActivity : AppCompatActivity(), DialogBusinessFieldSelector.OnBusinessFieldClicked,
    DialogBusinessTypeSelector.OnBusinessTypeClicked {

    companion object {
        val BUSINESS_FIELD_TAG = "businessfield"
        val BUSINESS_TYPE_TAG = "businesstype"
    }

    var merchantViewModel: MerchantViewModel? = null

    var storeName: EditText? = null
    var storeNameError: TextView? = null

    var businessFieldLayout: TextInputLayout? = null
    var businessFieldEditText: EditText? = null
    var skipButton: TextView? = null
    var businessFieldList = arrayListOf<BusinessFieldModel>()
    var businessTypeList = arrayListOf<BusinessTypeModel>()
    var submitButton: CardView? = null
    var mainLayout: ConstraintLayout? = null
    var fullscreenLoading: LinearLayout? = null

    var businessTypeLayout: TextInputLayout? = null
    var businessTypeEditText: EditText? = null

    var createMerchantRequest: PostCreateMerchantRequest? = null

    private var spinnerBusinessFieldSelector: DialogBusinessFieldSelector? = DialogBusinessFieldSelector()
    private var spinnerBusinessTypeSelector: DialogBusinessTypeSelector? = DialogBusinessTypeSelector()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        merchantViewModel = MerchantViewModel()

        storeName = findViewById(R.id.edittext_storename)
        storeNameError = findViewById(R.id.edittext_storename_error)

        businessFieldLayout = findViewById(R.id.edittext_business_field_layout)
        businessFieldEditText = findViewById(R.id.edittext_business_field)

        businessTypeLayout = findViewById(R.id.edittext_business_type_layout)
        businessTypeEditText = findViewById(R.id.edittext_business_type)
        submitButton = findViewById(R.id.submit_button)
        mainLayout = findViewById(R.id.main_layout)
        fullscreenLoading = findViewById(R.id.fullcreen_loading)

        createMerchantRequest = PostCreateMerchantRequest()

        skipButton = findViewById(R.id.skip_button)

        skipButton?.setOnClickListener {
            createConfirmationDialog()
        }

        storeName?.doAfterTextChanged {
            createMerchantRequest?.name = it.toString()
            isSaveButtonValid()
        }

        businessFieldEditText?.setOnClickListener {
            spinnerBusinessFieldSelector = DialogBusinessFieldSelector().newInstance()

            spinnerBusinessFieldSelector?.arguments = businessFieldList.toBundle()
            spinnerBusinessFieldSelector?.show(supportFragmentManager, BUSINESS_FIELD_TAG)
        }

        businessTypeEditText?.setOnClickListener {
            spinnerBusinessTypeSelector = DialogBusinessTypeSelector().newInstance()

            spinnerBusinessTypeSelector?.arguments = businessTypeList.toBundle()
            spinnerBusinessTypeSelector?.show(supportFragmentManager, BUSINESS_TYPE_TAG)
        }

        submitButton?.setOnClickListener {

            if (isSaveButtonValid()) {
                merchantViewModel?.createMerchant(
                    createMerchantRequest!!,
                    onLoading = {
                        fullscreenLoading?.isVisible = it
                        mainLayout?.isVisible = it.not()
                    },
                    onSuccess = {
                        showSnackBar("Merchant created")
                    },
                    onError = {
                        showSnackBar(it, R.color.snackbar_error)
                    })
            }
        }
    }

    override fun onDataBusinessFieldPass(data: ArrayList<BusinessFieldModel>) {
        businessFieldList.clear()
        businessFieldList.addAll(data)

        val temp = businessFieldList.filter { it.isSelected }
        businessFieldEditText?.setText(temp.toTextFormat())

        createMerchantRequest?.businessField = listOf()
        createMerchantRequest?.businessField = businessFieldList.filter { it.isSelected }.map { it.filedName }
        isSaveButtonValid()
    }

    override fun onDataBusinessTypePass(data: ArrayList<BusinessTypeModel>) {
        businessTypeList.clear()
        businessTypeList.addAll(data)

        val temp = businessTypeList.filter { it.isSelected }
        businessTypeEditText?.setText(temp.toTextFormat())

        createMerchantRequest?.businessType = temp.toTextFormat()
        isSaveButtonValid()
    }

    private fun createConfirmationDialog() {
        val bottomDialog = createBottomSheetDialog(
            layoutInflater.inflate(
                R.layout.dialog_skip_confirmation,
                null
            )
        )

        val acceptButton = bottomDialog.findViewById<MaterialCardView>(R.id.save_button)
        val discardButton = bottomDialog.findViewById<MaterialCardView>(R.id.cancel_button)

        acceptButton?.setOnClickListener {


            bottomDialog.dismiss()
        }

        discardButton?.setOnClickListener {
            bottomDialog.dismiss()
        }

        bottomDialog.show()
    }

    fun isSaveButtonValid(): Boolean {
        var isInputValid =
            createMerchantRequest!!.name != "" && createMerchantRequest!!.businessField.isNotEmpty() && createMerchantRequest!!.businessType != ""

        if (isInputValid)
            submitButton!!.setBackgroundColor(resources.getColor(R.color.kobold_blue_button))
        else
            submitButton!!.setBackgroundColor(resources.getColor(R.color.kobold_blue_button_disabled))

        return isInputValid
    }

    override fun onDestroy() {
        businessFieldList.clear()
        merchantViewModel?.onDestroy()

        super.onDestroy()
    }
}
