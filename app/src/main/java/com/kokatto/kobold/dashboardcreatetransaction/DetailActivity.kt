package com.kokatto.kobold.dashboardcreatetransaction

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
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
import com.kokatto.kobold.api.model.basemodel.TransactionModel
import com.kokatto.kobold.api.model.basemodel.createTransactionChat
import com.kokatto.kobold.constant.ActivityConstantCode
import com.kokatto.kobold.constant.TransactionStatusConstant
import com.kokatto.kobold.dashboardcreatetransaction.dialog.DialogAction
import com.kokatto.kobold.dashboardcreatetransaction.dialog.DialogCancel
import com.kokatto.kobold.dashboardcreatetransaction.dialog.DialogFinish
import com.kokatto.kobold.dashboardcreatetransaction.dialog.DialogPaid
import com.kokatto.kobold.dashboardcreatetransaction.dialog.DialogSent
import com.kokatto.kobold.dashboardcreatetransaction.dialog.DialogUnpaid
import com.kokatto.kobold.dashboardcreatetransaction.dialog.DialogUnsent
import com.kokatto.kobold.extension.showSnackBar
import com.kokatto.kobold.extension.showToast
import com.kokatto.kobold.utility.CurrencyUtility
import android.content.pm.ResolveInfo
import android.content.ComponentName
import android.content.pm.ActivityInfo
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.kokatto.kobold.api.impl.DashboardSessionExpiredEventHandler
import com.kokatto.kobold.api.impl.ErrorResponseValidator


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

    private var currentTransaction: TransactionModel? = null
    private var transactionViewModel: TransactionViewModel? = TransactionViewModel()

    // Dialog Popup
    private var dialogAction: DialogAction? = null
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
                    // Sudah Dibayar
                    TransactionStatusConstant.PENDING -> {
                        currentTransaction?.let { m -> onPaidDialog(m) }
                        dialogAction?.dismiss()
                    }
                    TransactionStatusConstant.PAID -> {
                        // Sudah Dikirim
                        currentTransaction?.let { m -> onSentDialog(m) }
                        dialogAction?.dismiss()
                    }
                    TransactionStatusConstant.SENT -> {
                        // Transaksi Selesai
                        currentTransaction?.let { m -> onCompleteDialog(m) }
                        dialogAction?.dismiss()
                    }
                }
            }
            R.id.back_button -> {
                super.onBackPressed()
            }
            R.id.action_button -> {
                val currentStatus = currentTransaction?.latestStatus
                val _id = currentTransaction?._id

                when (currentStatus) {
                    TransactionStatusConstant.PENDING -> {
                        dialogAction = DialogAction().newInstance(currentStatus)
                        dialogAction?.openDialog(supportFragmentManager)

                        dialogAction?.onCancelClick = {
                            currentTransaction?.let { m -> onCancelDialog(m) }
                            dialogAction?.dismiss()
                        }

                        dialogAction?.onEditClick = {
                            currentTransaction?.let { m -> onEditDialog(m) }
                            dialogAction?.dismiss()
                        }

                        dialogAction?.onSendClick = {
                            currentTransaction?.let { m -> onCopyChat(m) }
                            dialogAction?.dismiss()
                        }
                    }
                    TransactionStatusConstant.PAID -> {
                        dialogAction = DialogAction().newInstance(currentStatus)
                        dialogAction?.openDialog(supportFragmentManager)

                        dialogAction?.onCancelClick = {
                            currentTransaction?.let { m -> onCancelDialog(m) }
                            dialogAction?.dismiss()
                        }

                        dialogAction?.onEditClick = {
                            currentTransaction?.let { m -> onEditDialog(m) }
                            dialogAction?.dismiss()
                        }

                        dialogAction?.onChatClick = {
                            currentTransaction?.let { m -> onCopyChat(m) }
                            dialogAction?.dismiss()
                        }

                        dialogAction?.onUnpaidClick = {
                            currentTransaction?.let { m -> onUnpaid(m) }
                            dialogAction?.dismiss()
                        }
                    }
                    TransactionStatusConstant.SENT -> {
                        println("currentTransaction?.latestStatus :: " + currentStatus)
                        dialogAction = DialogAction().newInstance(currentStatus)
                        dialogAction?.openDialog(supportFragmentManager)

                        dialogAction?.onCancelClick = {
                            currentTransaction?.let { m -> onCancelDialog(m) }
                            dialogAction?.dismiss()
                        }

                        dialogAction?.onEditClick = {
                            currentTransaction?.let { m -> onEditDialog(m) }
                            dialogAction?.dismiss()
                        }

                        dialogAction?.onChatClick = {
                            currentTransaction?.let { m -> onCopyChat(m) }
                            dialogAction?.dismiss()
                        }

                        dialogAction?.onUnpaidClick = {
                            currentTransaction?.let { m -> onUnpaid(m) }
                            dialogAction?.dismiss()
                        }

                        dialogAction?.onUnsentClick = {
                            currentTransaction?.let { m -> onUnsentDialog(m) }
                            dialogAction?.dismiss()
                        }

                    }
                    else -> {
                        dialogAction = currentStatus?.let { DialogAction().newInstance(it) }
                        dialogAction?.openDialog(supportFragmentManager)

                        dialogAction?.onChatClick = {
                            currentTransaction?.let { m -> onCopyChat(m) }
                            dialogAction?.dismiss()
                        }

                        dialogAction?.onCompleteEditClick = {
                            currentTransaction?.let { m -> onEditCompleteDialog(m) }
                            dialogAction?.dismiss()
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
        editTextBuyer?.isFocusableInTouchMode = isEditable
        editTextBuyer?.isFocusable = isEditable
        editTextBuyerLayout?.setBackgroundColor(backgroundColor)

        editTextChannel?.isEnabled = isEditable
        editTextChannel?.setTextColor(textColor)
        editTextChannel?.isFocusableInTouchMode = isEditable
        editTextChannel?.isFocusable = isEditable
        editTextChannelLayout?.setBackgroundColor(backgroundColor)

        editTextPhone?.isEnabled = isEditable
        editTextPhone?.setTextColor(textColor)
        editTextPhone?.isFocusableInTouchMode = isEditable
        editTextPhone?.isFocusable = isEditable
        editTextPhoneLayout?.setBackgroundColor(backgroundColor)

        editTextAddress?.isEnabled = isEditable
        editTextAddress?.setTextColor(textColor)
        editTextAddress?.isFocusableInTouchMode = isEditable
        editTextAddress?.isFocusable = isEditable
        editTextAddressLayout?.setBackgroundColor(backgroundColor)

        editTextNote?.isEnabled = isEditable
        editTextNote?.setTextColor(textColor)
        editTextNote?.isFocusableInTouchMode = isEditable
        editTextNote?.isFocusable = isEditable
        editTextNoteLayout?.setBackgroundColor(backgroundColor)

        editTextPrice?.isEnabled = isEditable
        editTextPrice?.setTextColor(textColor)
        editTextPrice?.isFocusableInTouchMode = isEditable
        editTextPrice?.isFocusable = isEditable
        editTextPriceLayout?.setBackgroundColor(backgroundColor)

        editTextPayment?.isEnabled = isEditable
        editTextPayment?.setTextColor(textColor)
        editTextPayment?.isFocusableInTouchMode = isEditable
        editTextPayment?.isFocusable = isEditable
        editTextPaymentLayout?.setBackgroundColor(backgroundColor)
        editTextLogistic?.isEnabled = isEditable

        editTextLogistic?.setTextColor(textColor)
        editTextLogistic?.isFocusableInTouchMode = isEditable
        editTextLogistic?.isFocusable = isEditable
        editTextLogisticLayout?.setBackgroundColor(backgroundColor)

        editTextdeliveryFee?.isEnabled = isEditable
        editTextdeliveryFee?.setTextColor(textColor)
        editTextdeliveryFee?.isFocusableInTouchMode = isEditable
        editTextdeliveryFee?.isFocusable = isEditable
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
        model.price.let { s -> editTextPrice?.setText(CurrencyUtility.currencyFormatterNoPrepend(s)) }
        model.payingMethod.let { s -> editTextPayment?.setText(s) }
        model.logistic.let { s -> editTextLogistic?.setText(s) }
        model.deliveryFee.let { s -> editTextdeliveryFee?.setText(CurrencyUtility.currencyFormatterNoPrepend(s)) }
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
                btnSubmitText?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_checked, 0, 0, 0)
                btnSubmitProgress?.visibility = View.GONE
            }
            TransactionStatusConstant.PAID -> {
                btnSubmitText?.text = resources.getString(R.string.form_trx_btn_action_sent)
                btnSubmitText?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sent, 0, 0, 0)
                btnSubmitProgress?.visibility = View.GONE
            }
            TransactionStatusConstant.SENT -> {
                btnSubmitText?.text = resources.getString(R.string.form_trx_btn_action_complete)
                btnSubmitText?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_circle_checked, 0, 0, 0)
                btnSubmitProgress?.visibility = View.GONE
            }
            else -> {
                btnSubmitText?.text = resources.getString(R.string.form_trx_btn_action_complete)
                btnSubmitText?.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                btnSubmitText?.setBackgroundColor(resources.getColor(R.color.kobold_blue_button_disabled))
                btnSubmit?.setCardBackgroundColor(resources.getColor(R.color.kobold_blue_button_disabled))
                btnSubmitProgress?.visibility = View.GONE
            }
        }
    }

    private fun constructChannel(editText: EditText, assetUrl: String) {
        Glide.with(this).load(assetUrl).apply(RequestOptions().fitCenter()).into(
            object : CustomTarget<Drawable>(50, 50) {
                override fun onLoadCleared(placeholder: Drawable?) {
                    editText.setCompoundDrawablesWithIntrinsicBounds(
                        placeholder, null,
                        resources.getDrawable(R.drawable.ic_subdued), null
                    )
                    editText.compoundDrawablePadding = 12
                }

                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    editText.setCompoundDrawablesWithIntrinsicBounds(
                        resource, null,
                        resources.getDrawable(R.drawable.ic_subdued), null
                    )
                    editText.compoundDrawablePadding = 12
                }
            }
        )
    }

    private fun onCancelDialog(model: TransactionModel) {
        val dialogCancel = DialogCancel().newInstance()
        dialogCancel?.openDialog(supportFragmentManager)
        dialogCancel?.onConfirmClick = {
            dialogCancel?.progressLoading(true)
            transactionViewModel?.cancelTransactionById(
                model._id,
                onSuccess = {
                    dialogCancel?.progressLoading(false)
                    dialogCancel?.dismiss()

                    val intent = Intent()
                    intent.putExtra(ActivityConstantCode.EXTRA_DATA, currentTransaction)
                    setResult(ActivityConstantCode.STATUS_TO_CANCEL, intent)
                    finish()
                },
                onError = {
                    if(ErrorResponseValidator.isSessionExpiredResponse(it))
                        DashboardSessionExpiredEventHandler(this).onSessionExpired()

                    progressLoading(false)
                }
            )
        }
    }

    private fun onEditDialog(model: TransactionModel) {
        val intent = Intent()
        intent.putExtra(ActivityConstantCode.EXTRA_DATA, model)
        setResult(ActivityConstantCode.RESULT_OPEN_EDIT, intent)
        finish()
    }

    private fun onEditCompleteDialog(model: TransactionModel) {
        val intent = Intent()
        intent.putExtra(ActivityConstantCode.EXTRA_DATA, model)
        setResult(ActivityConstantCode.RESULT_OPEN_EDIT_COMPLETE, intent)
        finish()
    }

    private fun onCopyChat(model: TransactionModel) {
        // Show Dialog Confirm
        val message = createTransactionChat(model)
        val myClipboard =
            this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val myClip: ClipData = ClipData.newPlainText("chat", message)
        myClipboard.setPrimaryClip(myClip)

        if (model.channel != null && model.channel !== ActivityConstantCode.BELUM_ADA && !model.phone.isEmpty()) {
            when (model.channel) {
                ActivityConstantCode.WHATSAPP -> {
                    if (!model.phone.isEmpty()) {
                        val phone = parsePhoneToCountryCode(model.phone)
                        openWhatsappAndDirectToNumber(phone, message, this, ActivityConstantCode.WHATSAPP_PKG)
                    } else {
                        showSnackBar(
                            findViewById(R.id.root_layout),
                            resources.getString(R.string.kobold_transaction_action_nota_toast_error),
                            R.color.snackbar_error
                        )
                    }
                }
                ActivityConstantCode.WHATSAPP_BUSINESS -> {
                    if (!model.phone.isEmpty()) {
                        val phone = parsePhoneToCountryCode(model.phone)
                        openWhatsappAndDirectToNumber(phone, message, this, ActivityConstantCode.WHATSAPP_BUSINESS_PKG)
                    } else {
                        showSnackBar(
                            findViewById(R.id.root_layout),
                            resources.getString(R.string.kobold_transaction_action_nota_toast_error),
                            R.color.snackbar_error
                        )
                    }
                }
                ActivityConstantCode.LINE -> {
                    openApplicationActivity(this, ActivityConstantCode.LINE_PKG)
                }
                ActivityConstantCode.FACEBOOK_MESSENGER -> {
                    openApplicationActivity(this, ActivityConstantCode.FACEBOOK_MESSENGER_PKG)
                }
                ActivityConstantCode.INSTAGRAM -> {
                    openApplicationActivity(this, ActivityConstantCode.INSTAGRAM_PKG)
                }
                ActivityConstantCode.BUKALAPAK_CHAT -> {
                    openApplicationActivity(this, ActivityConstantCode.BUKALAPAK_CHAT_PKG)
                }
                ActivityConstantCode.TOKOPEDIA_CHAT -> {
                    openApplicationActivity(this, ActivityConstantCode.TOKOPEDIA_CHAT_PKG)
                }
                ActivityConstantCode.SHOPEE_CHAT -> {
                    openApplicationActivity(this, ActivityConstantCode.SHOPEE_CHAT_PKG)
                }
                else ->
                    showSnackBar(
                        findViewById(R.id.root_layout),
                        resources.getString(R.string.kobold_transaction_action_nota_toast)
                    )
            }
        } else {
            showSnackBar(
                findViewById(R.id.root_layout),
                resources.getString(R.string.kobold_transaction_action_nota_toast_error),
                R.color.snackbar_error
            )
        }
    }

    private fun onUnpaid(model: TransactionModel) {
        val dialog = DialogUnpaid().newInstance()
        dialog?.openDialog(supportFragmentManager)
        dialog?.onConfirmClick = {
            dialog?.progressLoading(true)
            transactionViewModel?.pendingTransactionById(
                model._id,
                onSuccess = {
                    dialog?.progressLoading(false)
                    dialog?.dismiss()

                    val intent = Intent()
                    intent.putExtra(ActivityConstantCode.EXTRA_DATA, currentTransaction)
                    setResult(ActivityConstantCode.STATUS_TO_UNPAID, intent)
                    finish()
                    //showSnackBar(findViewById(R.id.root_layout), resources.getString(R.string.kobold_transaction_unpaid_toast))
                    //showToast(resources.getString(R.string.kobold_transaction_unpaid_toast))
                },
                onError = {
                    progressLoading(false)
                    if(ErrorResponseValidator.isSessionExpiredResponse(it))
                        DashboardSessionExpiredEventHandler(this).onSessionExpired()
                }
            )
        }
    }

    private fun onPaidDialog(model: TransactionModel) {
        val dialog = DialogPaid().newInstance()
        dialog?.openDialog(supportFragmentManager)
        dialog?.onConfirmClick = {
            dialog?.progressLoading(true)
            transactionViewModel?.paidTransactionById(
                model._id,
                onSuccess = {
                    dialog?.progressLoading(false)
                    dialog?.dismiss()

                    val intent = Intent()
                    intent.putExtra(ActivityConstantCode.EXTRA_DATA, currentTransaction)
                    setResult(ActivityConstantCode.STATUS_TO_PAID, intent)
                    finish()
                    //showSnackBar(findViewById(R.id.root_layout), resources.getString(R.string.kobold_transaction_paid_toast))
                    //showToast(resources.getString(R.string.kobold_transaction_paid_toast))
                },
                onError = {
                    progressLoading(false)
                }
            )
        }
    }

    private fun onSentDialog(model: TransactionModel) {
        val dialog = DialogSent().newInstance()
        dialog?.openDialog(supportFragmentManager)
        dialog?.onConfirmClick = {
            dialog?.progressLoading(true)
            transactionViewModel?.sentTransactionById(
                model._id,
                onSuccess = {
                    dialog?.progressLoading(false)
                    dialog?.dismiss()

                    val intent = Intent()
                    intent.putExtra(ActivityConstantCode.EXTRA_DATA, currentTransaction)
                    setResult(ActivityConstantCode.STATUS_TO_SENT, intent)
                    finish()
                    //showSnackBar(findViewById(R.id.root_layout), resources.getString(R.string.kobold_transaction_sent_toast))
                    //showToast(resources.getString(R.string.kobold_transaction_sent_toast))
                },
                onError = {
                    progressLoading(false)
                }
            )
        }
    }

    private fun onCompleteDialog(model: TransactionModel) {
        val dialog = DialogFinish().newInstance()
        dialog?.openDialog(supportFragmentManager)
        dialog?.onConfirmClick = {
            dialog?.progressLoading(true)
            transactionViewModel?.completeTransactionById(
                model._id,
                onSuccess = {
                    dialog?.progressLoading(false)
                    dialog?.dismiss()

                    val intent = Intent()
                    intent.putExtra(ActivityConstantCode.EXTRA_DATA, currentTransaction)
                    setResult(ActivityConstantCode.STATUS_TO_COMPLETE, intent)
                    finish()
                    //showSnackBar(findViewById(R.id.root_layout), resources.getString(R.string.kobold_transaction_finish_toast) )
                    //showToast(resources.getString(R.string.kobold_transaction_finish_toast))
                },
                onError = {
                    progressLoading(false)
                }
            )
        }
    }

    private fun onUnsentDialog(model: TransactionModel) {
        val dialog = DialogUnsent().newInstance()
        dialog?.openDialog(supportFragmentManager)
        dialog?.onConfirmClick = {
            dialog?.progressLoading(true)
            transactionViewModel?.paidTransactionById(
                model._id,
                onSuccess = {
                    dialog?.progressLoading(false)
                    dialog?.dismiss()

                    val intent = Intent()
                    intent.putExtra(ActivityConstantCode.EXTRA_DATA, currentTransaction)
                    setResult(ActivityConstantCode.STATUS_TO_CANCEL, intent)
                    finish()
                    showSnackBar(findViewById(R.id.root_layout), resources.getString(R.string.kobold_transaction_finish_toast))
                    //showToast(resources.getString(R.string.kobold_transaction_unsent_toast))
                },
                onError = {
                    progressLoading(false)
                }
            )
        }
    }

    fun openWhatsappAndDirectToNumber(phoneNo: String, message: String, context: Context, packageName: String) {

        if(checkAppInstalledOrNot(packageName)) {
            val url = Uri.parse("https://api.whatsapp.com/send?phone=${phoneNo}&text=${message}")
            val intent = Intent(Intent.ACTION_VIEW, url)
            intent.setPackage(packageName)
            startActivity(intent);

//            var intent = context.packageManager.getLaunchIntentForPackage(packageName)
//            if (intent != null) {
//                val sendIntent = Intent("android.intent.action.MAIN")
//                sendIntent.action = Intent.ACTION_SEND
//                sendIntent.type = "text/plain"
//                sendIntent.putExtra(Intent.EXTRA_TEXT, message)
//                sendIntent.putExtra("jid", phoneNo + "@s.whatsapp.net")
//                sendIntent.setPackage(packageName)
//                startActivity(sendIntent)
//            } else {
//
//            }
        } else {
            // Bring user to the market or let them choose an app?
            intent = Intent(Intent.ACTION_VIEW)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.data = Uri.parse("market://details?id=$packageName")
            context.startActivity(intent)
        }
    }

    fun openApplicationActivity(context: Context, packageName: String) {

        if(checkAppInstalledOrNot(packageName)){
            var packMan: PackageManager = packageManager
            val intent = Intent(Intent.ACTION_MAIN, null)
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            intent.setPackage(packageName)
            val launchables: List<ResolveInfo> = packMan.queryIntentActivities(intent, 0)

            if(launchables.size > 0) {
                val activity: ActivityInfo = launchables.get(0).activityInfo
                val name = ComponentName(
                    activity.applicationInfo.packageName,
                    activity.name
                )
                val intentToLauch = Intent(Intent.ACTION_MAIN)
                intentToLauch.addCategory(Intent.CATEGORY_LAUNCHER)
                intentToLauch.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                intentToLauch.component = name
                startActivity(intentToLauch)
                showToast(resources.getString(R.string.kobold_transaction_action_nota_toast), Toast.LENGTH_LONG)
            } else {

            }
        } else {
            // Bring user to the market or let them choose an app?
            intent = Intent(Intent.ACTION_VIEW)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.data = Uri.parse("market://details?id=$packageName")
            context.startActivity(intent)
        }
//        var intent = context.packageManager.getLaunchIntentForPackage(packageName)
//
//        if (intent != null) {
//            // We found the activity now start the activity
//            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            intent.setFlags(0);
//            context.startActivity(intent)
//            showToast(resources.getString(R.string.kobold_transaction_action_nota_toast))
//        } else {
//
//        }
    }

    fun checkAppInstalledOrNot(packageName: String) : Boolean {
        var packageManager: PackageManager = packageManager
        try {
            packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            return true
        } catch (e: PackageManager.NameNotFoundException) {
            return false
        }
    }

    private fun parsePhoneToCountryCode(phone: String) : String {
        if(phone.get(0).toString() == "0")
        {
            return "62${phone.substring(1)}"
        } else if(phone.get(0).toString() == "6" && phone.get(1).toString() == "2") {
            return "62${phone.substring(1)}"
        } else {
            return "62${phone}"
        }
    }

}
