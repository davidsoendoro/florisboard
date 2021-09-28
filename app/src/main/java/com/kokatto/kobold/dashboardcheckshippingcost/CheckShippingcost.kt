package com.kokatto.kobold.dashboardcheckshippingcost

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.ShippingCostModel
import com.kokatto.kobold.extension.addAffixToString
import com.kokatto.kobold.extension.showSnackBar

class CheckShippingcost : AppCompatActivity() {
    private var shippingCost = ShippingCostModel()

    private var backButton: ImageView? = null

    private var senderAddressEdittext: EditText? = null
    private var receiverAddressEdittext: EditText? = null

    private var minusWeightButton: ImageView? = null
    private var packageWeightText: TextView? = null
    private var plusWeightButton: ImageView? = null

    private var submitButton: CardView? = null
    private var resetButton: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_shippingcost)

        backButton = findViewById(R.id.back_button)
        senderAddressEdittext = findViewById(R.id.sender_address_edittext)
        receiverAddressEdittext = findViewById(R.id.receiver_address_edittext)

        minusWeightButton = findViewById(R.id.minus_weight_button)
        packageWeightText = findViewById(R.id.package_weight_text)
        plusWeightButton = findViewById(R.id.plus_weight_button)
        submitButton = findViewById(R.id.submit_button)
        resetButton = findViewById(R.id.reset_button_text)

        minusWeightButton?.setOnClickListener {
            if (shippingCost.packageWeight == 1)
                showSnackBar(findViewById(R.id.parent_layout), "Paket tidak boleh lebih ringan dari 1 kg")
            else {
                shippingCost.packageWeight--
                packageWeightText?.text = shippingCost.packageWeight.addAffixToString(suffix = " kg")
            }
        }

        plusWeightButton?.setOnClickListener {
            if (shippingCost.packageWeight == 7)
                showSnackBar(findViewById(R.id.parent_layout), "Paket tidak boleh lebih berat dari 7 kg")
            else {
                shippingCost.packageWeight++
                packageWeightText?.text = shippingCost.packageWeight.addAffixToString(suffix = " kg")
            }
        }
    }
}
