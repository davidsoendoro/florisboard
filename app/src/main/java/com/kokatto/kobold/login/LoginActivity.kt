package com.kokatto.kobold.login

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.ViewTreeObserver
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import com.kokatto.kobold.R
import com.kokatto.kobold.constant.ActivityConstantCode
import com.kokatto.kobold.databinding.ActivityLoginBinding
import com.kokatto.kobold.extension.hideKeyboard
import com.kokatto.kobold.extension.showKeyboard
import com.kokatto.kobold.extension.showToast
import com.kokatto.kobold.login.listener.PhoneKeyboardLifecycleObserver
import com.kokatto.kobold.login.slider.SliderAdapter


class LoginActivity : FragmentActivity() {

    private lateinit var sliderAdapter: SliderAdapter
    private lateinit var indicatorContainer: LinearLayout
    private lateinit var uiBinding: ActivityLoginBinding
    private var authenticationViewModel: AuthenticationViewModel? = AuthenticationViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uiBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(uiBinding.root)

//        val window: Window = window
//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
//            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
//        )

        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.statusBarColor = resources.getColor(R.color.primary_50, null)

        indicatorContainer = findViewById(R.id.indicator_container)

        sliderAdapter = SliderAdapter(this)
        uiBinding.introSliderViewpager.adapter = sliderAdapter

        setupIndicator()
        //setCurrentIndicator(0)

        uiBinding.introSliderViewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })

        uiBinding.buttonNext.setOnClickListener {
            apiCallRequestOTP()
        }

        uiBinding.edittextPhoneTrigger.setOnClickListener {
            uiBinding.edittextPhone.showKeyboard()
        }

        uiBinding.edittextPhone.setOnKeyListener { v, keyCode, event -> onKeyEdit(v, keyCode, event) }

        uiBinding.rootLayout.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    val heightDiff: Int = uiBinding.rootLayout.rootView.height - uiBinding.rootLayout.height
                    if (heightDiff > dpToPx(uiBinding.rootLayout.context, 200f)) {
                        // if more than 200 dp, it's probably a keyboard...
                        showStateLayout(true)
                    } else {
                        showStateLayout(false)
                    }
                }
            })
    }

    private fun setupIndicator() {
        val indicator = arrayOfNulls<ImageView>(sliderAdapter.itemCount)
        val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        layoutParams.setMargins(8, 0, 8, 0)
        for (i in indicator.indices) {
            indicator[i] = ImageView(applicationContext)
            indicator[i].apply {
                this?.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.ic_indicator_default
                    )
                )
                this?.layoutParams = layoutParams
            }
            indicatorContainer.addView(indicator[i])
        }
    }

    private fun setCurrentIndicator(index: Int) {
        val childCount = indicatorContainer.childCount
        for (i in 0 until childCount) {
            val imageView = indicatorContainer[i] as ImageView

            if (i == index) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.ic_indicator_selected
                    )
                )
            } else {
                if( i < index){
                    imageView.setImageDrawable(
                        ContextCompat.getDrawable(
                            applicationContext,
                            R.drawable.ic_indicator_active
                        )
                    )
                } else {
                    imageView.setImageDrawable(
                        ContextCompat.getDrawable(
                            applicationContext,
                            R.drawable.ic_indicator_default
                        )
                    )
                }
            }

        }
    }

    private fun apiCallRequestOTP() {
        val phone = uiBinding.edittextPhone.text.toString()
        uiBinding.edittextPhoneTrigger.setText(phone)
        uiBinding.fullcreenLoading.isVisible = true
        uiBinding.edittextPhone.hideKeyboard()

        authenticationViewModel?.requestOTP(
            phone,
            onSuccess = {
                val intent = Intent(this, OtpActivity::class.java)
                intent.putExtra(ActivityConstantCode.EXTRA_DATA, phone)
                startActivity(intent)

                uiBinding.fullcreenLoading.isVisible = false
                uiBinding.edittextPhone.text?.clear()
                uiBinding.edittextPhoneTrigger.text?.clear()
            },
            onError = {
                uiBinding.edittextPhoneTrigger.text?.clear()
                uiBinding.fullcreenLoading.isVisible = false
                showToast(it)
            }
        )

    }

    private fun onKeyEdit(view: View, keyCode: Int?, event: KeyEvent?): Boolean {
        when (view.id) {
            R.id.edittext_phone -> {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event?.action == KeyEvent.ACTION_DOWN) {
                    apiCallRequestOTP()
                    return true
                }
            }
        }
        return false
    }

    private fun dpToPx(context: Context, valueInDp: Float): Float {
        val metrics: DisplayMetrics = context.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics)
    }

    private fun showStateLayout(isInputmode: Boolean = false){
        if(isInputmode){
            uiBinding.layoutInput.visibility = View.VISIBLE
            uiBinding.loginLayout.visibility = View.GONE
        }else{
            uiBinding.layoutInput.visibility = View.GONE
            uiBinding.loginLayout.visibility = View.VISIBLE
        }
    }

}
