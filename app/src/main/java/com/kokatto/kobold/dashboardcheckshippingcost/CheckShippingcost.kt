package com.kokatto.kobold.dashboardcheckshippingcost

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.DeliveryAddressModel
import com.kokatto.kobold.api.model.basemodel.DeliveryFeeModel
import com.kokatto.kobold.api.model.basemodel.ShippingCostModel
import com.kokatto.kobold.checkshippingcost.ShippingCostViewModel
import com.kokatto.kobold.dashboardcheckshippingcost.adapter.ShippingCostRecylerAdapter
import com.kokatto.kobold.extension.addAffixToString
import com.kokatto.kobold.extension.showSnackBar
import com.kokatto.kobold.extension.showToast
import com.kokatto.kobold.utility.CurrencyUtility

class CheckShippingcost : AppCompatActivity() {
    private var shippingCost = ShippingCostModel()

    private var backButton: ImageView? = null

    private var senderAddressEdittext: EditText? = null
    private var receiverAddressEdittext: EditText? = null

    private var minusWeightButton: ImageView? = null
    private var packageWeightText: TextView? = null
    private var plusWeightButton: ImageView? = null
    private var closeInfoButton: ImageView? = null
    private var infoLayout: LinearLayout? = null
    private var bottomLoading: LinearLayout? = null

    private var recyclerView: RecyclerView? = null
    private var recyclerAdapter: ShippingCostRecylerAdapter? = null

    private var submitButton: CardView? = null
    private var copyButton: CardView? = null
    private var resetButton: TextView? = null
    private var lastPosition: String? = null


    private var shippingCostViewModel: ShippingCostViewModel? = ShippingCostViewModel()
    private var shippingCostDataList: ArrayList<DeliveryFeeModel> = arrayListOf()
    private var selectedDataList: ArrayList<DeliveryFeeModel> = arrayListOf()

    private val suffixKilogram: String = " kg"

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
        copyButton = findViewById(R.id.copy_button)
        resetButton = findViewById(R.id.reset_button_text)
        closeInfoButton = findViewById(R.id.close_info_button)
        infoLayout = findViewById(R.id.layout_info)
        bottomLoading = findViewById(R.id.bottom_loading)

        recyclerView = findViewById(R.id.recycler_view_shippingcost)
        recyclerView?.layoutManager = LinearLayoutManager(this)
        recyclerView?.setHasFixedSize(true)

        recyclerAdapter = ShippingCostRecylerAdapter( lastPosition,this, shippingCostDataList, selectedDataList)
        recyclerAdapter?.onItemClick = {
            if(selectedDataList.contains(it)){
                selectedDataList.remove(it)
            } else {
                selectedDataList.add(it)
            }

            copyButton?.isVisible = selectedDataList.size > 0

            recyclerAdapter!!.notifyDataSetChanged()
        }
        recyclerView?.adapter = recyclerAdapter

        minusWeightButton?.setOnClickListener {
            if (shippingCost.packageWeight == 1)
                showSnackBar(findViewById(R.id.parent_layout), "Paket tidak boleh lebih ringan dari 1 kg")
            else {
                shippingCost.packageWeight--
                packageWeightText?.text = shippingCost.packageWeight.addAffixToString(suffix = suffixKilogram)
            }
        }

        closeInfoButton?.setOnClickListener {
            val layoutInfo = findViewById<LinearLayout>(R.id.layout_info)
            layoutInfo.visibility = View.GONE
        }

        resetButton?.setOnClickListener {
           resetLayout()
        }

        copyButton?.setOnClickListener {
            copyDataToClipboard()
        }

        plusWeightButton?.setOnClickListener {
            if (shippingCost.packageWeight == 7)
                showSnackBar(findViewById(R.id.parent_layout), "Paket tidak boleh lebih berat dari 7 kg")
            else {
                shippingCost.packageWeight++
                packageWeightText?.text = shippingCost.packageWeight.addAffixToString(suffix = suffixKilogram)
            }
        }

        submitButton?.setOnClickListener {
            lastPosition = null
            shippingCostDataList.clear()
            selectedDataList.clear()
            infoLayout?.isVisible = false

            shippingCost = ShippingCostModel(
                senderAddress = DeliveryAddressModel(
                    "","","Jakarta Selatan","Mampang Prapatan"
                ),
                receiverAddress = DeliveryAddressModel(
                    "","","Jakarta Selatan","Mampang Prapatan"
                ),
                packageWeight = packageWeightText?.text.toString().replace(suffixKilogram,"").toInt()
            )

            callAPIShippingCost(shippingCost)
        }

    }

    private fun copyDataToClipboard() {
       if(selectedDataList.size > 0) {

           var message = ""
           selectedDataList.forEach { model ->
               message += "${model.service} - ${CurrencyUtility.currencyFormatter(model.price)}・${model.service} (${model.eta})\n\n"
           }

           if(message!= "") {
               val myClipboard =
                   this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
               val myClip: ClipData = ClipData.newPlainText("ongkir", message)
               myClipboard.setPrimaryClip(myClip)

               showSnackBar(findViewById(R.id.parent_layout), "Informasi ongkir berhasil disalin")
           }

       } else {
           showSnackBar(findViewById(R.id.parent_layout), "Tidak ada ongkos kirim terpilih", R.color.snackbar_error)
       }
    }

    private fun resetLayout() {
        val defaultWeight: Int = 1

        bottomLoading?.isVisible = false
        senderAddressEdittext?.text?.clear()
        senderAddressEdittext?.text?.clear()
        packageWeightText?.text = defaultWeight.addAffixToString(suffix = suffixKilogram)
        shippingCostDataList.clear()
        selectedDataList.clear()
        infoLayout?.isVisible = false
        recyclerView?.isVisible = false
        copyButton?.isVisible = false
        recyclerAdapter?.notifyDataSetChanged()
    }

    private fun processSubmit(isLoading: Boolean){
        bottomLoading?.isVisible = isLoading
        recyclerView?.isVisible = !isLoading
    }

    private fun callAPIShippingCost(shippingcost: ShippingCostModel) {
        processSubmit(true)

        shippingCostViewModel?.getListDeliveryFee(
            fromCity = shippingcost.senderAddress.city,
            fromDistrict = shippingcost.senderAddress.district,
            fromPostalcode = shippingcost.senderAddress.postalcode,
            toCity = shippingcost.receiverAddress.city,
            toDistrict = shippingcost.receiverAddress.district,
            toPostalcode = shippingcost.receiverAddress.postalcode,
            weight = shippingcost.packageWeight * 1000,
            onSuccess = { it ->
                if (it.data.size > 0) {
                    val sorted = it.data.sortedBy { model -> model.service }
                    shippingCostDataList.addAll(sorted)
                    recyclerAdapter!!.notifyDataSetChanged()
                    infoLayout?.isVisible = true
                }
                else {
                    showSnackBar(findViewById(R.id.parent_layout), "Tidak dapat mendapatkan kurir", R.color.snackbar_error)
                }
                processSubmit(false)
            },
            onError = {
                //showToast(it)
                processSubmit(false)
                showSnackBar(findViewById(R.id.parent_layout), "Tidak dapat mendapatkan kurir", R.color.snackbar_error)
                recyclerView!!.isVisible = true
            }
        )
    }
}
