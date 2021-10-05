package com.kokatto.kobold.login

import android.R
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.kokatto.kobold.api.model.request.PostOTPVerificationRequest
import com.kokatto.kobold.constant.ActivityConstantCode
import com.kokatto.kobold.databinding.ActivityOtpBinding
import com.kokatto.kobold.extension.showToast
import com.kokatto.kobold.login.dialog.DialogCancelRegister
import com.kokatto.kobold.login.dialog.DialogChangeNumber
import com.kokatto.kobold.login.dialog.DialogLoading
import com.kokatto.kobold.persistance.AppPersistence
import android.app.Activity
import android.content.Intent
import android.view.View
import com.kokatto.kobold.dashboardcreatetransaction.SearchTransactionActivity
import com.kokatto.kobold.extension.hideKeyboard
import com.kokatto.kobold.extension.showKeyboard
import com.kokatto.kobold.extension.showSnackBar
import com.kokatto.kobold.registration.RegistrationActivity


class OtpActivity : AppCompatActivity() {

    private lateinit var uiBinding: ActivityOtpBinding
    private lateinit var countdown_timer: CountDownTimer

    private var itemViews: MutableList<TextInputEditText> = ArrayList()
    private var START_MILLI_SECONDS = 30000L // 30 second
    private var isRunning: Boolean = false
    private var time_in_milli_seconds = 0L

    private var authenticationViewModel: AuthenticationViewModel? = AuthenticationViewModel()
    private var imm: InputMethodManager? = null

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
            uiBinding.edittextOtp1.hideKeyboard()
            showChangeNumberDialog()
        }

        uiBinding.resendAction.setOnClickListener {
            val phone = uiBinding.textviewPhone.text.toString()
            resendOTP(phone)
        }

        itemViews.add(uiBinding.edittextOtp1)
        itemViews.add(uiBinding.edittextOtp2)
        itemViews.add(uiBinding.edittextOtp3)
        itemViews.add(uiBinding.edittextOtp4)
        itemViews.add(uiBinding.edittextOtp5)

        uiBinding.edittextOtp1.addTextChangedListener(CustomTextWatcher(
            afterChanged = {
                if (it != null && !it.toString().equals("")) {
                    uiBinding.edittextOtp2.requestFocus()
                } else {
                    setDefaultColor()
                }
            }
        ))

        uiBinding.edittextOtp2.addTextChangedListener(CustomTextWatcher(
            afterChanged = {
                if (it != null && !it.toString().equals("")) {
                    uiBinding.edittextOtp3.requestFocus()
                } else {
                    uiBinding.edittextOtp1.requestFocus()
                    setDefaultColor()
                }
            }
        ))

        uiBinding.edittextOtp3.addTextChangedListener(CustomTextWatcher(
            afterChanged = {
                if (it != null && !it.toString().equals("")) {
                    uiBinding.edittextOtp4.requestFocus()
                } else {
                    uiBinding.edittextOtp2.requestFocus()
                    setDefaultColor()
                }
            }
        ))

        uiBinding.edittextOtp4.addTextChangedListener(CustomTextWatcher(
            afterChanged = {
                if (it != null && !it.toString().equals("")) {
                    uiBinding.edittextOtp5.requestFocus()
                } else {
                    uiBinding.edittextOtp3.requestFocus()
                    setDefaultColor()
                }
            }
        ))

        uiBinding.edittextOtp5.addTextChangedListener(CustomTextWatcher(
            afterChanged = {
                if (it != null && !it.toString().equals("")) {
                    var isValid = true
                    // validate test in filled
                    for (v in itemViews) {
                        if (v.text.toString().length <= 0) {
                            isValid = false
                            break
                        }
                    }

                    if (isValid) {
                        submitOTP()
                    }

                } else {
                    uiBinding.edittextOtp4.requestFocus()
                    setDefaultColor()
                }
            }
        ))

        setDefaultColor()
        startTimer(START_MILLI_SECONDS)
        //imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        //imm!!.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        uiBinding.edittextOtp1.showKeyboard()
        uiBinding.edittextOtp1.requestFocus()
    }

    private fun submitOTP() {
        val loading = DialogLoading(this)
        loading.startLoading()

        val model = PostOTPVerificationRequest(
            uiBinding.textviewPhone.text.toString(),
            getOTPString()
        )

        authenticationViewModel?.verifyOTP(
            model,
            onSuccess = {
                AppPersistence.token = it.data.token
                AppPersistence.refreshToken = it.data.refreshToken
                loading.isDismiss()
                startActivity(Intent(this, RegistrationActivity::class.java))
            },
            onError = {
                loading.isDismiss()
                setErrorColor()
                showToast(it)
            }
        )
    }

    private fun showChangeNumberDialog() {
        val dialog = DialogChangeNumber().newInstance()
        dialog.openDialog(supportFragmentManager)

        dialog.onComplete = {
            uiBinding.textviewPhone.setText(it)

            for (v in itemViews){
                v.text?.clear()
            }

            resendOTP(it)

        }

        dialog.onClose = {
            //imm!!.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        }

    }

    private fun resendOTP(phone: String) {
        authenticationViewModel?.requestOTP(
            phone,
            onSuccess = {
                showSnackBar(uiBinding.rootLayout, "Kode Rahasia berhasil dikirim ulang")
                resetTimer()
            },
            onError = {
                showToast(it)
            }
        )
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

    private fun startTimer(time_in_seconds: Long) {
        updateTextUI()
        countdown_timer = object : CountDownTimer(time_in_seconds, 1000) {
            override fun onFinish() {
                // do action
                uiBinding.resendAction.isVisible = true
                uiBinding.resendCounter.isVisible = false
            }

            override fun onTick(p0: Long) {
                updateTextUI()
                time_in_milli_seconds = p0
            }
        }
        uiBinding.resendAction.isVisible = false
        uiBinding.resendCounter.isVisible = true
        countdown_timer.start()
        isRunning = true
    }

    private fun resetTimer() {
        uiBinding.resendAction.isVisible = false
        uiBinding.resendCounter.isVisible = true
        time_in_milli_seconds = START_MILLI_SECONDS
        updateTextUI()
        startTimer(START_MILLI_SECONDS)
    }

    private fun updateTextUI() {
        val seconds = ((time_in_milli_seconds / 1000) % 60)
        uiBinding.resendCounter.text = "Tunggu $seconds detik"
    }

    private fun setDefaultColor() {
        val states = arrayOf(
            intArrayOf(android.R.attr.state_focused),
            intArrayOf()
        )

        val colors = intArrayOf(
            Color.parseColor("#2B4AC7"),
            Color.parseColor("#D6D6D6")
        )
        val colorList = ColorStateList(states, colors)

        for (v in itemViews) {
            val inputlayout = v.parent.parent as TextInputLayout
            inputlayout.setBoxStrokeColorStateList(colorList)
            inputlayout.boxStrokeWidth = 2
        }

        uiBinding.errorOtp.isVisible = false

    }

    private fun setErrorColor() {
        val states = arrayOf(
            intArrayOf(android.R.attr.state_focused),
            intArrayOf()
        )

        val colors = intArrayOf(
            Color.parseColor("#EC0C0C"),
            Color.parseColor("#EC0C0C")
        )

        val colorList = ColorStateList(states, colors)

        for (v in itemViews) {
            val inputlayout = v.parent.parent as TextInputLayout
            inputlayout.setBoxStrokeColorStateList(colorList)
            inputlayout.boxStrokeWidth = 5
        }

        uiBinding.errorOtp.isVisible = true
    }

    private fun getOTPString(): String {
        return itemViews.map { editText -> editText.text.toString() }.joinToString("")
    }

    inner class CustomTextWatcher(
        private val afterChanged: ((Editable?) -> Unit) = {},
        private val beforeChanged: ((CharSequence?, Int, Int, Int) -> Unit) = { _, _, _, _ -> },
        private val onChanged: ((CharSequence?, Int, Int, Int) -> Unit) = { _, _, _, _ -> }
    ) : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            afterChanged(s)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            beforeChanged(s, start, count, after)
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onChanged(s, start, before, count)
        }
    }

}
