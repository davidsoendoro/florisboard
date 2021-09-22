package com.kokatto.kobold.dashboardcreatetransaction

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.textfield.TextInputLayout
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.BankModel
import com.kokatto.kobold.api.model.basemodel.PropertiesModel
import com.kokatto.kobold.api.model.basemodel.TransactionModel
import com.kokatto.kobold.constant.TransactionStatusConstant
import com.kokatto.kobold.dashboardcreatetransaction.dialog.DialogAction
import com.kokatto.kobold.dashboardcreatetransaction.dialog.DialogCancel
import com.kokatto.kobold.dashboardcreatetransaction.dialog.DialogFinish
import com.kokatto.kobold.dashboardcreatetransaction.dialog.DialogPaid
import com.kokatto.kobold.dashboardcreatetransaction.dialog.DialogSent
import com.kokatto.kobold.dashboardcreatetransaction.dialog.DialogUnpaid
import com.kokatto.kobold.dashboardcreatetransaction.dialog.DialogUnsent
import com.kokatto.kobold.extension.showToast

class DetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ID = "EXTRA_ID"
        const val EXTRA_DATA = "EXTRA_DATA"
        const val EDIT = "EDIT"
        const val EDIT_COMPLETE = "EDIT_COMPLETE"
    }

    private var editTextBuyer: EditText? = null
    private var editTextBuyerLayout: TextInputLayout? = null
    private var editTextChannel: EditText? = null
    private var editTextChannelLayout: TextInputLayout? = null
    private var editTextPhone: EditText? = null
    private var editTextPhoneLayout: TextInputLayout? = null
    private var editTextAddress: EditText? = null
    private var editTextAddressLayout: TextInputLayout? = null
    private var editTextNote: EditText? = null
    private var editTextNoteLayout: TextInputLayout? = null
    private var editTextPrice: EditText? = null
    private var editTextPriceLayout: TextInputLayout? = null
    private var editTextPayment: EditText? = null
    private var editTextPaymentLayout: TextInputLayout? = null
    private var editTextLogistic: EditText? = null
    private var editTextLogisticLayout: TextInputLayout? = null
    private var editTextdeliveryFee: EditText? = null
    private var editTextDeliveryFeeLayout: TextInputLayout? = null
    private var btnSubmit: CardView? = null
    private var btnSubmitText: TextView? = null
    private var btnSubmitProgress: ProgressBar? = null
    private var layoutTitleText: TextView? = null
    private var btnBack: ImageView? = null
    private var btnAction: CardView? = null

    private var isLoading: Boolean = false
    private var currentTransaction: TransactionModel? = null
    private var transactionViewModel: TransactionViewModel? = TransactionViewModel()

    // Dialog Popup
    private var dialogAction: DialogAction? = null
    private var dialogCancel: DialogCancel? = null
    private var dialogFinish: DialogFinish? = null
    private var dialogSent: DialogSent? = null
    private var dialogPaid: DialogPaid? = null
    private var dialogUnpaid: DialogUnpaid? = null
    private var dialogUnsent: DialogUnsent? = null

    private var fullscreenLoading: LinearLayout? = null
    private var scrollviewLayout: ScrollView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_transaction)

        editTextBuyer = findViewById<EditText>(R.id.edittext_buyername)
        editTextBuyerLayout = findViewById<TextInputLayout>(R.id.edittext_buyername_layout)
        editTextChannel = findViewById<EditText>(R.id.edittext_channel)
        editTextChannelLayout = findViewById<TextInputLayout>(R.id.edittext_channel_layout)
        editTextPhone = findViewById<EditText>(R.id.edittext_phone)
        editTextPhoneLayout = findViewById<TextInputLayout>(R.id.edittext_phone_layout)
        editTextAddress = findViewById<EditText>(R.id.edittext_buyeraddress)
        editTextAddressLayout = findViewById<TextInputLayout>(R.id.edittext_buyeraddress_layout)
        editTextNote = findViewById<EditText>(R.id.edittext_note)
        editTextNoteLayout = findViewById<TextInputLayout>(R.id.edittext_note_layout)
        editTextPrice = findViewById<EditText>(R.id.edittext_price)
        editTextPriceLayout = findViewById<TextInputLayout>(R.id.edittext_price_layout)
        editTextPayment = findViewById<EditText>(R.id.edittext_paymentmethod)
        editTextPaymentLayout = findViewById<TextInputLayout>(R.id.edittext_paymentmethod_layout)
        editTextLogistic = findViewById<EditText>(R.id.edittext_logistic)
        editTextLogisticLayout = findViewById<TextInputLayout>(R.id.edittext_logistic_layout)
        editTextdeliveryFee = findViewById<EditText>(R.id.edittext_deliveryfee)
        editTextDeliveryFeeLayout = findViewById<TextInputLayout>(R.id.edittext_deliveryfee_layout)
        btnSubmit = findViewById<CardView>(R.id.submit_button)
        btnAction = findViewById<CardView>(R.id.action_button)
        btnBack = findViewById<ImageView>(R.id.back_button)
        btnSubmitText = findViewById<TextView>(R.id.submit_button_text)
        btnSubmitProgress = findViewById<ProgressBar>(R.id.submit_button_loading)
        layoutTitleText = findViewById<TextView>(R.id.title_text)
        layoutTitleText?.setText(R.string.form_trx_detail)

        fullscreenLoading = findViewById<LinearLayout>(R.id.fullcreen_loading)
        scrollviewLayout = findViewById<ScrollView>(R.id.scroll_layout)

        btnSubmit?.let { button -> button.setOnClickListener { onClicked(button) } }
        btnBack?.let { button -> button.setOnClickListener { onClicked(button) } }
        btnAction?.let { button -> button.setOnClickListener { onClicked(button) } }

        getTransactionModelFromIntent()
    }

    private fun onClicked(view: View) {
        when (view.id) {
            R.id.submit_button -> {
                val _id = currentTransaction?._id

                when (currentTransaction?.latestStatus) {
                    TransactionStatusConstant.PENDING -> {
                        // Sudah Bayar ?
                        dialogPaid = DialogPaid().newInstance()
                        dialogPaid?.openDialog(supportFragmentManager)
                        dialogPaid?.onConfirmClick = {
                            dialogPaid?.progressLoading(true)
                            // CALL API
                            if (_id != null) {
                                transactionViewModel?.paidTransactionById(_id,
                                    onSuccess = { it ->
                                        dialogPaid?.progressLoading(false)
                                        dialogPaid?.dismiss()
                                        super.finish()
                                    },
                                    onError = {
                                        progressLoading(false)
                                    }
                                )
                            }
                        }
                    }
                    TransactionStatusConstant.PAID -> {
                        // Sudah Dikirim ?
                        dialogSent = DialogSent().newInstance()
                        dialogSent?.openDialog(supportFragmentManager)
                        dialogSent?.onConfirmClick = {
                            dialogSent?.progressLoading(true)
                            // CALL API
                            if (_id != null) {
                                transactionViewModel?.sentTransactionById(_id,
                                    onSuccess = { it ->
                                        dialogSent?.progressLoading(false)
                                        dialogSent?.dismiss()
                                        super.finish()
                                    },
                                    onError = {
                                        progressLoading(false)
                                    }
                                )
                            }
                        }
                    }
                    TransactionStatusConstant.SENT -> {
                        // Transaksi Selesai ?
                        dialogFinish = DialogFinish().newInstance()
                        dialogFinish?.openDialog(supportFragmentManager)
                        dialogFinish?.onConfirmClick = {
                            dialogFinish?.progressLoading(true)
                            // CALL API
                            if (_id != null) {
                                transactionViewModel?.completeTransactionById(_id,
                                    onSuccess = { it ->
                                        dialogSent?.progressLoading(false)
                                        dialogSent?.dismiss()
                                        super.finish()
                                    },
                                    onError = {
                                        progressLoading(false)
                                    }
                                )
                            }
                        }
                    }
                }
            }
            R.id.back_button -> {
                super.onBackPressed()
            }
            R.id.action_button -> {
                val currentStatus = currentTransaction?.latestStatus
                val _id = currentTransaction?._id
                println("currentTransaction?.latestStatus :: " + currentStatus)

                when (currentStatus) {
                    TransactionStatusConstant.PENDING -> {
                        println("currentTransaction?.latestStatus :: " + currentStatus)
                        dialogAction = DialogAction().newInstance(currentStatus)
                        dialogAction?.openDialog(supportFragmentManager)

                        dialogAction?.onCancelClick = {
                            // Show Dialog Confirm Cancel
                            dialogCancel = DialogCancel().newInstance()
                            dialogCancel?.openDialog(supportFragmentManager)
                            dialogCancel?.onConfirmClick = {
                                dialogCancel?.progressLoading(true)
                                // API CALL
                                if (_id != null) {
                                    transactionViewModel?.cancelTransactionById(_id,
                                        onSuccess = { it ->
                                            dialogCancel?.progressLoading(false)
                                            dialogCancel?.dismiss()
                                            super.finish()
                                        },
                                        onError = {
                                            progressLoading(false)
                                        }
                                    )
                                }
                            }
                        }

                        dialogAction?.onEditClick = {
                            // Show Dialog Confirm
                            dialogAction?.dismiss()
                            Intent(this, InputActivity::class.java).apply {
                                currentTransaction?._id.let { id ->
                                    run {
                                        putExtra(InputActivity.EXTRA_ID, id)
                                        putExtra(InputActivity.MODE, InputActivity.EDIT)
                                        startActivity(this)
                                    }
                                }

                            }
                        }

                        dialogAction?.onSendClick = {
                            // Show Dialog Confirm
                            val clipboardData = "Halo ini detail transaksi nya ya :\n" +
                                "Pembeli : " + currentTransaction?.buyer.toString() + "\n" +
                                "Nomor Telp : " + currentTransaction?.phone.toString() + "\n" +
                                "Alamat : " + currentTransaction?.address.toString() + "\n" +
                                "\n" +
                                "===\n" +
                                "\n" +
                                "Untuk Pembayaran :" + currentTransaction?.notes.toString() + "\n" +
                                "Harga : Rp." + currentTransaction?.price.toString() + "\n" +
                                "Metode Bayar : " + currentTransaction?.payingMethod.toString() + "\n" +
                                "Ongkir : Rp." + currentTransaction?.deliveryFee.toString() + "\n" +
                                "Kurir : " + currentTransaction?.logistic.toString() + "\n" +
                                "\n" +
                                "Silahkan, proses pembayaran bisa via :\n" +
                                "\n" +
                                "[Account No] - [Account Holder]\n" +
                                "\n" +
                                "Terima Kasih :-)"
                            val myClipboard =
                                this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val myClip: ClipData = ClipData.newPlainText("Label", clipboardData)
                            myClipboard.setPrimaryClip(myClip)
                            showToast(resources.getString(R.string.toast_copy_clipboard))
                            super.finish()
                        }
                    }
                    TransactionStatusConstant.PAID -> {
                        println("currentTransaction?.latestStatus :: " + currentStatus)
                        dialogAction = DialogAction().newInstance(currentStatus)
                        dialogAction?.openDialog(supportFragmentManager)

                        dialogAction?.onCancelClick = {
                            // Show Dialog Confirm Cancel
                            dialogCancel = DialogCancel().newInstance()
                            dialogCancel?.openDialog(supportFragmentManager)
                            dialogCancel?.onConfirmClick = {
                                dialogCancel?.progressLoading(true)
                                super.finish()
                            }
                        }

                        dialogAction?.onEditClick = {
                            // Show Dialog Edit
                            dialogAction?.dismiss()
                            Intent(this, InputActivity::class.java).apply {
                                currentTransaction?._id.let { id ->
                                    run {
                                        putExtra(InputActivity.EXTRA_ID, id)
                                        putExtra(InputActivity.MODE, InputActivity.EDIT)
                                        startActivity(this)
                                    }
                                }
                            }
                        }

                        dialogAction?.onChatClick = {
                            //Redirect ke respective channel
                            showToast("Redirect ke respective channel")
                        }

                        dialogAction?.onUnpaidClick = {
                            // Show Dialog Confirm Unpaid
                            dialogUnpaid = DialogUnpaid().newInstance()
                            dialogUnpaid?.openDialog(supportFragmentManager)
                            dialogUnpaid?.onConfirmClick = {
                                dialogUnpaid?.progressLoading(true)
                                // Call API Update to Unpaid
                                dialogAction?.dismiss()
                                dialogUnpaid?.dismiss()
                            }
                        }
                    }
                    TransactionStatusConstant.SENT -> {
                        println("currentTransaction?.latestStatus :: " + currentStatus)
                        dialogAction = DialogAction().newInstance(currentStatus)
                        dialogAction?.openDialog(supportFragmentManager)

                        dialogAction?.onCancelClick = {
                            // Show Dialog Confirm Cancel
                            dialogCancel = DialogCancel().newInstance()
                            dialogCancel?.openDialog(supportFragmentManager)
                            dialogCancel?.onConfirmClick = {
                                dialogCancel?.progressLoading(true)
                                // API CALL
                                if (_id != null) {
                                    transactionViewModel?.cancelTransactionById(_id,
                                        onSuccess = { it ->
                                            dialogCancel?.progressLoading(false)
                                            dialogCancel?.dismiss()
                                            super.finish()
                                        },
                                        onError = {
                                            progressLoading(false)
                                        }
                                    )
                                }
                            }
                        }

                        dialogAction?.onEditClick = {
                            // Show Dialog Edit
                            dialogAction?.dismiss()
                            openIntentInput(EDIT_COMPLETE)
                        }

                        dialogAction?.onChatClick = {
                            //Redirect ke respective channel
                            showToast("Redirect ke respective channel")
                        }

                        dialogAction?.onUnpaidClick = {
                            // Show Dialog Confirm Unpaid
                            dialogUnpaid = DialogUnpaid().newInstance()
                            dialogUnpaid?.openDialog(supportFragmentManager)
                            dialogUnpaid?.onConfirmClick = {
                                dialogUnpaid?.progressLoading(true)
                                // API CALL
                                if (_id != null) {
                                    transactionViewModel?.pendingTransactionById(_id,
                                        onSuccess = { it ->
                                            dialogUnpaid?.progressLoading(false)
                                            dialogUnpaid?.dismiss()
                                            dialogAction?.dismiss()
                                            super.finish()
                                        },
                                        onError = {
                                            dialogUnpaid?.progressLoading(false)
                                        }
                                    )
                                }
                            }
                        }

                        dialogAction?.onUnsentClick = {
                            // Show Dialog Confirm Unpaid
                            dialogUnsent = DialogUnsent().newInstance()
                            dialogUnsent?.openDialog(supportFragmentManager)
                            dialogUnsent?.onConfirmClick = {
                                dialogUnsent?.progressLoading(true)
                                // API CALL
                                if (_id != null) {
                                    transactionViewModel?.paidTransactionById(_id,
                                        onSuccess = { it ->
                                            dialogUnsent?.progressLoading(false)
                                            dialogUnsent?.dismiss()
                                            dialogAction?.dismiss()
                                            super.finish()
                                        },
                                        onError = {
                                            dialogUnsent?.progressLoading(false)
                                        }
                                    )
                                }
                            }
                        }

                    }
                    else -> {
                        dialogAction = currentStatus?.let { DialogAction().newInstance(it) }
                        dialogAction?.openDialog(supportFragmentManager)

                        dialogAction?.onChatClick = {
                            //Redirect ke respective channel
                            showToast("Redirect ke respective channel")
                        }

                        dialogAction?.onCompleteEditClick = {
                            // Show Dialog Edit
                            dialogAction?.dismiss()
                            openIntentInput(EDIT_COMPLETE)
                        }
                    }
                }
            }
        }

    }

    private fun disableFormInput() {
        val textColor = this.resources.getColor(R.color.colorEditTextDisableText, null)
        val backgroundColor = this.resources.getColor(R.color.colorEditTextDisable, null)
        val isEditable = false

        editTextBuyer?.isEnabled = isEditable
        editTextBuyer?.setTextColor(textColor)
        editTextBuyer?.setFocusableInTouchMode(isEditable)
        editTextBuyer?.setFocusable(isEditable)
        editTextBuyerLayout?.setBackgroundColor(backgroundColor)

        editTextChannel?.isEnabled = isEditable
        editTextChannel?.setTextColor(textColor)
        editTextChannel?.setFocusableInTouchMode(isEditable)
        editTextChannel?.setFocusable(isEditable)
        editTextChannelLayout?.setBackgroundColor(backgroundColor)

        editTextPhone?.isEnabled = isEditable
        editTextPhone?.setTextColor(textColor)
        editTextPhone?.setFocusableInTouchMode(isEditable)
        editTextPhone?.setFocusable(isEditable)
        editTextPhoneLayout?.setBackgroundColor(backgroundColor)

        editTextAddress?.isEnabled = isEditable
        editTextAddress?.setTextColor(textColor)
        editTextAddress?.setFocusableInTouchMode(isEditable)
        editTextAddress?.setFocusable(isEditable)
        editTextAddressLayout?.setBackgroundColor(backgroundColor)

        editTextNote?.isEnabled = isEditable
        editTextNote?.setTextColor(textColor)
        editTextNote?.setFocusableInTouchMode(isEditable)
        editTextNote?.setFocusable(isEditable)
        editTextNoteLayout?.setBackgroundColor(backgroundColor)

        editTextPrice?.isEnabled = isEditable
        editTextPrice?.setTextColor(textColor)
        editTextPrice?.setFocusableInTouchMode(isEditable)
        editTextPrice?.setFocusable(isEditable)
        editTextPriceLayout?.setBackgroundColor(backgroundColor)

        editTextPayment?.isEnabled = isEditable
        editTextPayment?.setTextColor(textColor)
        editTextPayment?.setFocusableInTouchMode(isEditable)
        editTextPayment?.setFocusable(isEditable)
        editTextPaymentLayout?.setBackgroundColor(backgroundColor)
        editTextLogistic?.isEnabled = isEditable

        editTextLogistic?.setTextColor(textColor)
        editTextLogistic?.setFocusableInTouchMode(isEditable)
        editTextLogistic?.setFocusable(isEditable)
        editTextLogisticLayout?.setBackgroundColor(backgroundColor)

        editTextdeliveryFee?.isEnabled = isEditable
        editTextdeliveryFee?.setTextColor(textColor)
        editTextdeliveryFee?.setFocusableInTouchMode(isEditable)
        editTextdeliveryFee?.setFocusable(isEditable)
        editTextDeliveryFeeLayout?.setBackgroundColor(backgroundColor)
    }

    private fun progressLoading(_isLoading: Boolean) {
        if (_isLoading) {
            fullscreenLoading?.visibility = View.VISIBLE
            scrollviewLayout?.visibility = View.GONE
        } else {
            fullscreenLoading?.visibility = View.GONE
            scrollviewLayout?.visibility = View.VISIBLE
        }
    }

    private fun setupDisplay(model: TransactionModel) {
        model.buyer.let { s -> editTextBuyer?.setText(s) }
        model.channel.let { s -> editTextChannel?.setText(s) }
        constructChannel(editTextChannel!!, model.channelAsset)
        model.phone.let { s -> editTextPhone?.setText(s) }
        model.address.let { s -> editTextAddress?.setText(s) }
        model.notes.let { s -> editTextNote?.setText(s) }
        model.price.let { s -> editTextPrice?.setText(s.toString()) }
        model.payingMethod.let { s -> editTextPayment?.setText(s) }
        model.logistic.let { s -> editTextLogistic?.setText(s) }
        model.deliveryFee.let { s -> editTextdeliveryFee?.setText(s.toString()) }
    }

    private fun getTransactionModelFromIntent() {
        progressLoading(true)
        currentTransaction = intent.getParcelableExtra<TransactionModel>(EXTRA_DATA)
        currentTransaction?.let { setupDisplay(it) }
        currentTransaction?.latestStatus?.let { setupSubmitButton(it) }
        progressLoading(false)
        disableFormInput()
    }



    private fun setupSubmitButton(latestStatus: String) {
        when (latestStatus) {
            TransactionStatusConstant.PENDING -> {
                btnSubmitText?.text = resources.getString(R.string.form_trx_btn_action_paid)
                btnSubmitText?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_checked, 0, 0, 0);
                btnSubmitProgress?.visibility = View.GONE
            }
            TransactionStatusConstant.PAID -> {
                btnSubmitText?.text = resources.getString(R.string.form_trx_btn_action_sent)
                btnSubmitText?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sent, 0, 0, 0);
                btnSubmitProgress?.visibility = View.GONE
            }
            TransactionStatusConstant.SENT -> {
                btnSubmitText?.text = resources.getString(R.string.form_trx_btn_action_complete)
                btnSubmitText?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_circle_checked, 0, 0, 0);
                btnSubmitProgress?.visibility = View.GONE
            }
            else -> {
                btnSubmitText?.text = resources.getString(R.string.form_trx_btn_action_complete)
                btnSubmitText?.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                btnSubmitText?.setBackgroundColor(resources.getColor(R.color.kobold_blue_button_disabled));
                btnSubmit?.setCardBackgroundColor(resources.getColor(R.color.kobold_blue_button_disabled))
                btnSubmitProgress?.visibility = View.GONE
            }
        }
    }

    private fun openIntentInput(mode: String) {
        when(mode) {
            EDIT -> {
                Intent(this, InputActivity::class.java).apply {
                    currentTransaction?._id.let { id ->
                        run {
                            putExtra(InputActivity.EXTRA_DATA, currentTransaction)
                            putExtra(InputActivity.MODE, InputActivity.EDIT)
                            startActivity(this)
                        }
                    }
                }
            }
            EDIT_COMPLETE -> {
                Intent(this, InputActivity::class.java).apply {
                    currentTransaction?._id.let { id ->
                        run {
                            putExtra(InputActivity.EXTRA_DATA, currentTransaction)
                            putExtra(InputActivity.MODE, InputActivity.EDIT_COMPLETE)
                            startActivity(this)
                        }
                    }
                }
            }
        }
    }

    private fun constructChannel(editText: EditText, assetUrl: String) {
        Glide.with(this).load(assetUrl).apply(RequestOptions().fitCenter()).into(
            object : CustomTarget<Drawable>(50,50){
                override fun onLoadCleared(placeholder: Drawable?) {
                    editText?.setCompoundDrawablesWithIntrinsicBounds(placeholder, null,
                        resources.getDrawable(R.drawable.ic_subdued), null)
                    editText?.compoundDrawablePadding = 12
                }

                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    editText?.setCompoundDrawablesWithIntrinsicBounds(resource, null,
                        resources.getDrawable(R.drawable.ic_subdued), null)
                    editText?.compoundDrawablePadding = 12
                }
            }
        )
    }
}
