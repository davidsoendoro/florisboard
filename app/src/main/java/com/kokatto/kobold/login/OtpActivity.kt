package com.kokatto.kobold.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kokatto.kobold.constant.ActivityConstantCode
import com.kokatto.kobold.databinding.ActivityOtpBinding
import com.kokatto.kobold.login.dialog.DialogCancelRegister

class OtpActivity : AppCompatActivity() {

    private lateinit var uiBinding: ActivityOtpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        uiBinding = ActivityOtpBinding.inflate(layoutInflater)
        setContentView(uiBinding.root)

        val phoneNumber = intent.getStringExtra(ActivityConstantCode.EXTRA_DATA)

        uiBinding.textviewPhone.text = phoneNumber

        uiBinding.backButton.setOnClickListener {
            confirmCancel()
        }

        uiBinding.changeButton.setOnClickListener {

        }

    }

    private fun changeDialog() {

    }

    private fun confirmCancel() {
        val dialog = DialogCancelRegister().newInstance()
        dialog?.openDialog(supportFragmentManager)

        dialog?.onConfirmClick = {
            onBackPressed()
        }

        dialog?.onCancelClick = {
            dialog?.closeDialog()
        }

    }
}
