package com.kokatto.kobold.setting

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.kokatto.kobold.R
import com.kokatto.kobold.api.impl.DashboardSessionExpiredEventHandler
import com.kokatto.kobold.api.impl.ErrorResponseValidator
import com.kokatto.kobold.api.model.basemodel.BusinessFieldModel
import com.kokatto.kobold.api.model.basemodel.BusinessTypeModel
import com.kokatto.kobold.api.model.basemodel.MerchantModel
import com.kokatto.kobold.api.model.basemodel.MerchantModel.Companion.fromBundle
import com.kokatto.kobold.api.model.basemodel.toBundle
import com.kokatto.kobold.api.model.basemodel.toTextFormat
import com.kokatto.kobold.databinding.ActivitySettingProfilTokoBinding
import com.kokatto.kobold.registration.RegistrationActivity
import com.kokatto.kobold.registration.spinner.DialogBusinessFieldSelector
import com.kokatto.kobold.registration.spinner.DialogBusinessTypeSelector
import com.kokatto.kobold.registration.viewmodel.MerchantViewModel

class SettingProfilTokoActivity : AppCompatActivity(), DialogBusinessFieldSelector.OnBusinessFieldClicked,
    DialogBusinessTypeSelector.OnBusinessTypeClicked {
    lateinit var uiBinding: ActivitySettingProfilTokoBinding
    var settingViewModel: SettingViewModel? = null
    var merchantModel: MerchantModel? = MerchantModel()

    var businessFieldList = arrayListOf<BusinessFieldModel>()
    var businessTypeList = arrayListOf<BusinessTypeModel>()

    private var merchantViewModel: MerchantViewModel? = MerchantViewModel()

    private var spinnerBusinessFieldSelector: DialogBusinessFieldSelector? = DialogBusinessFieldSelector()
    private var spinnerBusinessTypeSelector: DialogBusinessTypeSelector? = DialogBusinessTypeSelector()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_profil_toko)

        uiBinding = ActivitySettingProfilTokoBinding.inflate(layoutInflater).apply {
            setContentView(root)
        }

        merchantModel = fromBundle(intent)
        getBusinessTypeList()
        getBusinessFieldList()

        uiBinding.edittextSettingStorename.setText(merchantModel?.name)
        uiBinding.edittextSettingStorephone.setText(merchantModel?.phone)

        uiBinding.backButton.setOnClickListener {
            onBackPressed()
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
        }

        uiBinding.edittextSettingBusinessType.setOnClickListener {
            spinnerBusinessTypeSelector = DialogBusinessTypeSelector().newInstance()

            spinnerBusinessTypeSelector?.arguments = businessTypeList.toBundle()
            spinnerBusinessTypeSelector?.show(supportFragmentManager, RegistrationActivity.BUSINESS_TYPE_TAG)
        }

        uiBinding.submitButton.setOnClickListener {

        }
    }

    override fun onDataBusinessFieldPass(data: ArrayList<BusinessFieldModel>) {
        businessFieldList.clear()
        businessFieldList.addAll(data)

        val temp = businessFieldList.filter { it.isSelected }
        uiBinding.edittextSettingBusinessField.setText(temp.toTextFormat())

//        createMerchantRequest?.businessField = listOf()
//        createMerchantRequest?.businessField = businessFieldList.filter { it.isSelected }.map { it.filedName }
    }

    override fun onDataBusinessTypePass(data: ArrayList<BusinessTypeModel>) {
        businessTypeList.clear()
        businessTypeList.addAll(data)

        val temp = businessTypeList.filter { it.isSelected }
        uiBinding.edittextSettingBusinessType.setText(temp.toTextFormat())

//        createMerchantRequest?.businessType = temp.toTextFormat()
//        isSaveButtonValid()
    }

    private fun getBusinessTypeList() {
        if (businessTypeList.size <= 0) {
            merchantViewModel?.getMerchantBusinessType(
                onLoading = {
                    uiBinding.fullscreenLoading.isVisible = it
                    uiBinding.scrollView.isVisible = it.not()
                },
                onSuccess = {
                    it.data.forEach { data ->
                        data.isSelected = data.title.contains(merchantModel?.businessType.toString(), true)
                        businessTypeList.add(data)
                    }

                    uiBinding.edittextSettingBusinessType.setText(merchantModel?.businessType)
                },
                onError = {
                    if (ErrorResponseValidator.isSessionExpiredResponse(it))
                        DashboardSessionExpiredEventHandler(baseContext).onSessionExpired()
                }
            )
        }
    }

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
                    val temp = businessFieldList.filter { it.isSelected }
                    uiBinding.edittextSettingBusinessField.setText(temp.toTextFormat())
                },
                onError = {
                    if (ErrorResponseValidator.isSessionExpiredResponse(it))
                        DashboardSessionExpiredEventHandler(baseContext).onSessionExpired()
                }
            )
        }
    }
}
