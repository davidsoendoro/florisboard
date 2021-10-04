package com.kokatto.kobold.registration

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.BusinessFieldModel
import com.kokatto.kobold.registration.spinner.SpinnerBusinessFieldSelector

class RegistrationActivity : AppCompatActivity() {

    val BUSINESS_FIELD_TAG = "businessfield"

    var storeName: EditText? = null
    var storeNameError: TextView? = null

    var businessFieldLayout: TextInputLayout? = null
    var businessFieldEditText: TextInputEditText? = null

    var businessTypeLayout: TextInputLayout? = null
    var businessTypeEditText: TextInputEditText? = null

    var selectedBusinessField: BusinessFieldModel? = null

    private var spinnerBusinessFieldSelector: SpinnerBusinessFieldSelector? = SpinnerBusinessFieldSelector()

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
            val channel = businessFieldEditText?.text.toString()
            spinnerBusinessFieldSelector = SpinnerBusinessFieldSelector().newInstance()

            if (selectedBusinessField == null) {
                selectedBusinessField = BusinessFieldModel("", "")
            }

            spinnerBusinessFieldSelector?.show(supportFragmentManager, BUSINESS_FIELD_TAG)
//            spinnerBusinessFieldSelector?.onItemClick = {
//                selectedBusinessField = it
//                businessFieldEditText?.setText(it.assetDesc)
//            }
        }
    }
}
