package com.kokatto.kobold.identity

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.kokatto.kobold.api.Network
import com.kokatto.kobold.extension.get
import com.kokatto.kobold.extension.set
import com.kokatto.kobold.extension.showToast
import com.kokatto.kobold.persistance.AppPersistence
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.onSuccess
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.ime.core.FlorisBoard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class KeyboardIdentityLogin : LinearLayout {
    private val florisboard: FlorisBoard? = FlorisBoard.getInstanceOrNull()

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var loginViewModel: LoginViewModel? = LoginViewModel()

    var loginButton: CardView? = null
        private set

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        loginButton = findViewById(R.id.login_button)
        loginButton?.let { button -> button.setOnClickListener { onLoginButtonClicked(button) } }

        AppPersistence.test = "hahahahahaha"
    }

    override fun onViewRemoved(child: View?) {
        super.onViewRemoved(child)

        loginViewModel?.onDestroy()
        loginViewModel = null
    }

    private fun onLoginButtonClicked(view: View) {
        showToast(AppPersistence.private()["test"])
//        loginViewModel!!.getRestaurant(
//            onSuccess = {
//                showToast("YAY")
//                        loginButton?.findViewById<TextView>(R.id.login_button_text)?.text = "HAHAHA"
//            },
//            onError = {
//                showToast("NAY")
//            }
//        )
//
        florisboard?.inputFeedbackManager?.keyPress()
        when (view.id) {
            R.id.login_button -> florisboard?.setActiveInput(R.id.kobold_mainmenu)
        }
    }
}
