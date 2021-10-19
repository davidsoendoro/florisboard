package com.kokatto.kobold.setting

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.kokatto.kobold.R
import com.kokatto.kobold.api.impl.DashboardSessionExpiredEventHandler
import com.kokatto.kobold.api.impl.ErrorResponseValidator
import com.kokatto.kobold.api.model.basemodel.BusinessFieldModel
import com.kokatto.kobold.api.model.basemodel.BusinessTypeModel
import com.kokatto.kobold.api.model.basemodel.MerchantModel
import com.kokatto.kobold.api.model.basemodel.MerchantModel.Companion.fromIntent
import com.kokatto.kobold.api.model.basemodel.toBundle
import com.kokatto.kobold.api.model.basemodel.toTextFormat
import com.kokatto.kobold.api.model.request.PostCreateMerchantRequest
import com.kokatto.kobold.databinding.ActivitySettingProfilTokoBinding
import com.kokatto.kobold.extension.showSnackBar
import com.kokatto.kobold.extension.showToast
import com.kokatto.kobold.registration.RegistrationActivity
import com.kokatto.kobold.registration.spinner.DialogBusinessFieldSelector
import com.kokatto.kobold.registration.spinner.DialogBusinessTypeSelector
import com.kokatto.kobold.registration.viewmodel.MerchantViewModel

class SettingProfilTokoActivity : AppCompatActivity(), DialogBusinessFieldSelector.OnBusinessFieldClicked,
    DialogBusinessTypeSelector.OnBusinessTypeClicked {
    lateinit var uiBinding: ActivitySettingProfilTokoBinding
    var settingViewModel: SettingViewModel? = null
    var merchantModel: MerchantModel? = MerchantModel()

    var businessFieldList = arrayListOf<BusinessFieldModel>()/////////////
    var businessTypeList = arrayListOf<BusinessTypeModel>()/////////////

    private var merchantViewModel: MerchantViewModel? = MerchantViewModel()//////////////
    var createMerchantRequest: PostCreateMerchantRequest = PostCreateMerchantRequest()/////////

    private var spinnerBusinessFieldSelector: DialogBusinessFieldSelector? = DialogBusinessFieldSelector()/////////////
    private var spinnerBusinessTypeSelector: DialogBusinessTypeSelector? = DialogBusinessTypeSelector()/////////////

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_profil_toko)

        uiBinding = ActivitySettingProfilTokoBinding.inflate(layoutInflater).apply {
            setContentView(root)
        }

        merchantModel = fromIntent(intent)
        createMerchantRequest.name = merchantModel?.name ?: ""

        getBusinessTypeList()
        getBusinessFieldList()

        uiBinding.edittextSettingStorename.setText(merchantModel?.name)
        uiBinding.edittextSettingStorephone.setText(merchantModel?.phone)

        uiBinding.backButton.setOnClickListener {
            onBackPressed()
        }

        uiBinding.edittextSettingStorename.doAfterTextChanged {
            createMerchantRequest.name = it.toString()
            isSaveButtonValid()
        }

        uiBinding.settingStoreHelpText.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data =
                Uri.parse("http://api.whatsapp.com/send?phone=+6285692689187&text=Halo, saya ingin mengganti nomor telpon...")
            startActivity(intent)
        }

        uiBinding.edittextSettingBusinessField.setOnClickListener {
            spinnerBusinessFieldSelector = DialogBusinessFieldSelector().newInstance()

            spinnerBusinessFieldSelector?.arguments = businessFieldList.toBundle()
            spinnerBusinessFieldSelector?.show(supportFragmentManager, RegistrationActivity.BUSINESS_FIELD_TAG)
        }///////////////////////////

        uiBinding.edittextSettingBusinessType.setOnClickListener {
            spinnerBusinessTypeSelector = DialogBusinessTypeSelector().newInstance()

            spinnerBusinessTypeSelector?.arguments = businessTypeList.toBundle()
            spinnerBusinessTypeSelector?.show(supportFragmentManager, RegistrationActivity.BUSINESS_TYPE_TAG)
        }///////////////////////////

        uiBinding.submitButton.setOnClickListener {
            if (isSaveButtonValid()) {
                merchantViewModel?.createMerchant(
                    request = createMerchantRequest,
                    onLoading = {
                        uiBinding.fullscreenLoading.isVisible = it
                        uiBinding.scrollView.isVisible = it.not()
                    },
                    onSuccess = {
                        showToast("Berhasil mengubah data!")
                        finish()
                    },
                    onError = {
                        showSnackBar(it, R.color.snackbar_error)
                    }
                )
            }
        }

        isSaveButtonValid()
    }

    override fun onDataBusinessFieldPass(data: ArrayList<BusinessFieldModel>) {
        businessFieldList.clear()
        businessFieldList.addAll(data)

        uiBinding.edittextSettingBusinessField.setText(businessFieldList.filter { it.isSelected }.toTextFormat())

        createMerchantRequest.businessField = listOf()
        createMerchantRequest.businessField = businessFieldList.filter { it.isSelected }.map { it.filedName }
    }///////////////////////////

    override fun onDataBusinessTypePass(data: ArrayList<BusinessTypeModel>) {
        businessTypeList.clear()
        businessTypeList.addAll(data)

        uiBinding.edittextSettingBusinessType.setText(businessTypeList.filter { it.isSelected }.toTextFormat())

        createMerchantRequest.businessType = businessTypeList.filter { it.isSelected }.toTextFormat()
        isSaveButtonValid()
    }///////////////////////////

    private fun getBusinessFieldList() {
        if (businessFieldList.size <= 0) {
            merchantViewModel?.getMerchantBusinessField(
                onLoading = {
                    uiBinding.fullscreenLoading.isVisible = it
                    uiBinding.scrollView.isVisible = it.not()
                },
                onSuccess = {
                    it.data.forEach { data ->
                        merchantModel?.businessField?.forEach { model ->
                            if (data.filedName.contains(model, true)) {
                                data.isSelected = true
                                return@forEach
                            }
                        }
                        businessFieldList.add(data)
                    }

                    createMerchantRequest.businessField = listOf()
                    createMerchantRequest.businessField =
                        businessFieldList.filter { it.isSelected }.map { it.filedName }

                    val temp = businessFieldList.filter { it.isSelected }
                    uiBinding.edittextSettingBusinessField.setText(temp.toTextFormat())
                    isSaveButtonValid()
                },
                onError = {
                    if (ErrorResponseValidator.isSessionExpiredResponse(it))
                        DashboardSessionExpiredEventHandler(baseContext).onSessionExpired()
                }
            )
        }
    }///////////////////////////

    private fun getBusinessTypeList() {
        if (businessTypeList.size <= 0) {
            merchantViewModel?.getMerchantBusinessType(
                onLoading = {
                    uiBinding.fullscreenLoading.isVisible = it
                    uiBinding.scrollView.isVisible = it.not()
                },
                onSuccess = {
                    it.data.forEach { data ->
                        if (merchantModel?.businessType!!.isNotEmpty())
                            data.isSelected = data.title.contains(merchantModel?.businessType.toString(), true)
                        businessTypeList.add(data)
                    }

                    createMerchantRequest.businessType = businessTypeList.filter { it.isSelected }.toTextFormat()

                    uiBinding.edittextSettingBusinessType.setText(merchantModel?.businessType)
                    isSaveButtonValid()
                },
                onError = {
                    if (ErrorResponseValidator.isSessionExpiredResponse(it))
                        DashboardSessionExpiredEventHandler(baseContext).onSessionExpired()
                }
            )
        }
    }///////////////////////////

    fun isSaveButtonValid(): Boolean {
        var isInputValid =
            createMerchantRequest.name != "" && createMerchantRequest.businessField.isNotEmpty() && createMerchantRequest.businessType != ""

        if (isInputValid)
            uiBinding.submitButton.setBackgroundColor(resources.getColor(R.color.kobold_blue_button))
        else
            uiBinding.submitButton.setBackgroundColor(resources.getColor(R.color.kobold_blue_button_disabled))

        return isInputValid
    }
}
