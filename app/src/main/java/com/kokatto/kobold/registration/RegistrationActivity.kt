package com.kokatto.kobold.registration

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.BusinessFieldModel
import com.kokatto.kobold.api.model.basemodel.BusinessTypeModel
import com.kokatto.kobold.api.model.basemodel.toBundle
import com.kokatto.kobold.api.model.basemodel.toTextFormat
import com.kokatto.kobold.registration.spinner.DialogBusinessFieldSelector
import com.kokatto.kobold.registration.spinner.DialogBusinessTypeSelector

class RegistrationActivity : AppCompatActivity(), DialogBusinessFieldSelector.OnBusinessFieldClicked,
    DialogBusinessTypeSelector.OnBusinessTypeClicked {

    companion object {
        val BUSINESS_FIELD_TAG = "businessfield"
        val BUSINESS_TYPE_TAG = "businesstype"
    }

    var storeName: EditText? = null
    var storeNameError: TextView? = null

    var businessFieldLayout: TextInputLayout? = null
    var businessFieldEditText: EditText? = null
    var businessFieldList = arrayListOf<BusinessFieldModel>()
    var businessTypeList = arrayListOf<BusinessTypeModel>()

    var businessTypeLayout: TextInputLayout? = null
    var businessTypeEditText: EditText? = null

    private var spinnerBusinessFieldSelector: DialogBusinessFieldSelector? = DialogBusinessFieldSelector()
    private var spinnerBusinessTypeSelector: DialogBusinessTypeSelector? = DialogBusinessTypeSelector()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        storeName = findViewById(R.id.edittext_storename)
        storeNameError = findViewById(R.id.edittext_storename_error)

        businessFieldLayout = findViewById(R.id.edittext_business_field_layout)
        businessFieldEditText = findViewById(R.id.edittext_business_field)

        businessTypeLayout = findViewById(R.id.edittext_business_type_layout)
        businessTypeEditText = findViewById(R.id.edittext_business_type)

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
    }

    override fun onDataBusinessFieldPass(data: ArrayList<BusinessFieldModel>) {
        businessFieldList.clear()
        businessFieldList.addAll(data)

        val temp = businessFieldList.filter { it.isSelected }
        businessFieldEditText?.setText(temp.toTextFormat())
    }

    override fun onDestroy() {
        businessFieldList.clear()

        super.onDestroy()
    }

    override fun onDataBusinessTypePass(data: ArrayList<BusinessTypeModel>) {
        businessTypeList.clear()
        businessTypeList.addAll(data)

        val temp = businessTypeList.filter { it.isSelected }
        businessTypeEditText?.setText(temp.toTextFormat())
    }
}
