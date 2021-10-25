package com.kokatto.kobold.transaction

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.createTransactionChat
import com.kokatto.kobold.checkshippingcost.KeyboardCheckShippingCost
import com.kokatto.kobold.crm.ContactViewModel
import com.kokatto.kobold.extension.showSnackBar
import com.kokatto.kobold.extension.showToast
import com.kokatto.kobold.persistance.AppPersistence
import dev.patrickgold.florisboard.ime.core.FlorisBoard

class KeyboardCreateTransactionSaveConfirmation : ConstraintLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    val florisboard: FlorisBoard? = FlorisBoard.getInstanceOrNull()

    var cancelButton: Button? = null
    var submitButton: Button? = null
    var dontShowAgainButton: RadioButton? = null
    var fullscreenLoading: LinearLayout? = null

    var contactViewModel = ContactViewModel()

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        cancelButton = findViewById(R.id.cancel_button)
        submitButton = findViewById(R.id.submit_button)
        dontShowAgainButton = findViewById(R.id.dont_show_again_button)
        fullscreenLoading = findViewById(R.id.fullscreen_loading)

        cancelButton?.setOnClickListener {
            onClick(this)
        }
        submitButton?.setOnClickListener {
            onClick(this)
        }
        dontShowAgainButton?.isSelected = false
        dontShowAgainButton?.setOnCheckedChangeListener { buttonView, isChecked ->
            AppPersistence.showContactUpdateMessage = isChecked
        }
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (changedView is KeyboardCheckShippingCost && visibility == View.VISIBLE && changedView == this) {
//            invalidateSaveButton()
        } else {
            contactViewModel.onDelete()
        }
    }

    fun onClick(id: View) {
        if (id == submitButton) {
            //call  is profile change API
            showToast("change profile")
            contactViewModel.updateContactByTransaction(
                contactId = florisboard?.createTransactionModel?.contactId?._id ?: "",
                transactionId = florisboard?.createTransactionModel?._id ?: "",
                onLoading = {
                    fullscreenLoading?.isVisible = it
                },
                onSuccess = {},
                onError = {}
            )
        }
        florisboard?.inputFeedbackManager?.keyPress()
        florisboard?.textInputManager?.activeEditorInstance?.commitText(
            createTransactionChat(florisboard.createTransactionModel)
        )
        showSnackBar("Transaksi baru berhasil dibuat dan terpasang di chat.")
        florisboard?.setActiveInput(R.id.text_input)
    }
}
