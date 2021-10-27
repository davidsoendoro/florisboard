package com.kokatto.kobold.template

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.kokatto.kobold.R
import com.kokatto.kobold.api.impl.DashboardSessionExpiredEventHandler
import com.kokatto.kobold.api.impl.ErrorResponseValidator
import com.kokatto.kobold.api.model.basemodel.AutoTextModel
import com.kokatto.kobold.chattemplate.ChatTemplateViewModel
import com.kokatto.kobold.component.DashboardThemeActivity
import com.kokatto.kobold.constant.ActivityConstantCode
import com.kokatto.kobold.roomdb.AutoTextDatabase
import com.kokatto.kobold.template.dialog.DialogClearConfirm
import com.kokatto.kobold.template.dialog.DialogCloseConfirm
import com.kokatto.kobold.template.dialog.DialogCloseEditConfirm
import java.util.concurrent.atomic.AtomicInteger


class TemplateActivityInput : DashboardThemeActivity(), TemplateDialogSelectionClickListener {

    companion object {
        const val EXTRA_STATE_INPUT = "EXTRA_STATE_INPUT"
        const val EXTRA_ID = "ID"

        const val EXTRA_TEMPLATE = "TEMPLATE"
        const val EXTRA_TITLE = "TITLE"
        const val EXTRA_CONTENT = "CONTENT"
        const val EXTRA_STATE_CREATE = -1
        const val EXTRA_STATE_EDIT = 1
    }

    private var titleText: TextView? = null
    private var textInputTemplate: TextInputEditText? = null
    private var textInputTitle: TextInputEditText? = null
    private var textInputContent: TextInputEditText? = null
    private var buttonSave: Button? = null
    private var buttonBack: ImageButton? = null
    private var buttonDelete: ImageButton? = null
    private var extraStateInput: Int? = -1
    private var extraId: String? = ""
    private val maxTitleLength = AtomicInteger(50)
    private val maxContentLength = AtomicInteger(1000)
    private var data: AutoTextModel? = null

    private var chatTemplateViewModel: ChatTemplateViewModel? = ChatTemplateViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.template_fragment_input)

        textInputTemplate = findViewById(R.id.choose_template_edittext)
        textInputTemplate?.isFocusable = false
        textInputTitle = findViewById(R.id.title_template_edittext)
        textInputContent = findViewById(R.id.content_template_edittext)
        buttonSave = findViewById(R.id.create_template_button)
        buttonBack = findViewById(R.id.back_button)
        titleText = findViewById(R.id.title_text)
        buttonDelete = findViewById(R.id.delete_button)

        buttonSave?.let { button -> button.setOnClickListener { onClicked(button) } }
        buttonBack?.let { button -> button.setOnClickListener { onClicked(button) } }
        buttonDelete?.let { button -> button.setOnClickListener { onClicked(button) } }
        textInputTemplate?.let { textInput -> textInput.setOnClickListener { onClicked(textInput) } }

        // textInputTitleError?.isVisible = false
        // textInputContentError?.isVisible = false

        textInputTitle?.addTextChangedListener { text ->
            val textInputLayout = findViewById<TextInputLayout>(R.id.title_template_textinputlayout)
            val textInputError = findViewById<TextView>(R.id.title_template_edittext_error)

            text?.let { _text ->
                if (_text.length > maxTitleLength.toInt()) {
                    textInputLayout.boxStrokeColor = resources.getColor(R.color.colorEditTextError, null)
                    textInputError.visibility = View.VISIBLE
                    validateInput()
                } else {
                    textInputLayout.boxStrokeColor = resources.getColor(R.color.colorEditTextDefault, null)
                    textInputError.visibility = View.GONE
                    validateInput()
                }
            }
        }

        textInputContent?.addTextChangedListener { text ->
            val textInputLayout = findViewById<TextInputLayout>(R.id.content_template_textinputlayout)
            val textInputError = findViewById<TextView>(R.id.content_template_edittext_error)

            text?.let { _text ->
                if (_text.length > maxContentLength.toInt()) {
                    textInputLayout.boxStrokeColor = resources.getColor(R.color.colorEditTextError, null)
                    textInputLayout.isErrorEnabled = true
                    textInputError.visibility = View.VISIBLE
                    validateInput()
                } else {
                    textInputLayout.boxStrokeColor = resources.getColor(R.color.colorEditTextDefault, null)
                    textInputError.visibility = View.GONE
                    validateInput()
                }
            }
        }

        extraStateInput = intent.getIntExtra(ActivityConstantCode.EXTRA_MODE, ActivityConstantCode.EXTRA_CREATE)
        data = intent.getParcelableExtra<AutoTextModel>(ActivityConstantCode.EXTRA_DATA)

        val extraTemplate = data?.template
        val extraTitle = data?.title
        val extraContent = data?.content
        markButtonSaveDisable(true)

        if (extraStateInput!! >= 0) {
            extraId = intent.getStringExtra(EXTRA_ID)
            titleText?.text = resources.getString(R.string.detail_template)
            buttonDelete?.isVisible = true
            setEditTextValue(extraTemplate, extraTitle, extraContent)
            markButtonSaveDisable(true)
        } else {
            extraId = "NEW"
            titleText?.text = resources.getString(R.string.buat_template)
            buttonDelete?.isVisible = false

            // accepted form expand view or button create
            if (extraTemplate == null || extraTemplate == "") {
                setEditTextValue("Pesan Pembuka", "", "")
                markButtonSaveDisable(true)
            } else {
                setEditTextValue(extraTemplate, extraTitle, extraContent)
                // Allow to save
                markButtonSaveDisable(false)
            }
        }

    }

    override fun onItemClick(item: String?) {

        val titleLen = textInputTitle?.text.toString().length
        val contentLen = textInputContent?.text.toString().length

        if (titleLen > 0 || contentLen > 0) {
            val dialog = DialogClearConfirm().newInstance()

            dialog?.openDialog(supportFragmentManager)
            dialog?.onConfirmClick = {
                textInputTemplate?.setText(item)
                prefillByTemplate(item)
                dialog?.closeDialog()
            }

            dialog?.onCancelClick = {
                textInputTemplate?.setText(item)
                dialog?.closeDialog()
            }


        } else {
            textInputTemplate?.setText(item)
            prefillByTemplate(item)
        }

        validateInput()

    }

    private fun onClicked(view: View) {
        when (view.id) {
            R.id.create_template_button -> {
                extraId?.let { _extraId ->

                    if (extraStateInput!! >= 0) {
                        val model = AutoTextModel(
                            _id = _extraId,
                            template = textInputTemplate?.text.toString(),
                            title = textInputTitle?.text.toString(),
                            content = textInputContent?.text.toString()
                        )

                        chatTemplateViewModel?.updateAutotextById(
                            _extraId,
                            model,
                            onSuccess = {
                                val intent = Intent()
                                intent.putExtra(ActivityConstantCode.EXTRA_DATA, model)
                                setResult(ActivityConstantCode.RESULT_OK_UPDATED, intent)
                                finish()
                            },
                            onError = {
                                if(ErrorResponseValidator.isSessionExpiredResponse(it)) {
                                    DashboardSessionExpiredEventHandler(this).onSessionExpired()
                                } else {
                                    val intent = Intent()
                                    intent.putExtra(ActivityConstantCode.EXTRA_DATA, model)
                                    setResult(ActivityConstantCode.RESULT_FAILED_SAVE, intent)
                                    finish()
                                }
                            }
                        )
                    } else {
                        val model = AutoTextModel(
                            template = textInputTemplate?.text.toString(),
                            title = textInputTitle?.text.toString(),
                            content = textInputContent?.text.toString()
                        )

                        chatTemplateViewModel?.createChatTemplate(
                            model,
                            onSuccess = {
                                AutoTextDatabase.getInstance(this)?.autoTextDao()?.insertAutoText(model)
                                val intent = Intent()
                                intent.putExtra(ActivityConstantCode.EXTRA_DATA, model)
                                setResult(ActivityConstantCode.RESULT_OK_CREATED, intent)
                                finish()
                            },
                            onError = {
                                if(ErrorResponseValidator.isSessionExpiredResponse(it)) {
                                    DashboardSessionExpiredEventHandler(this).onSessionExpired()
                                } else {
                                    val intent = Intent()
                                    intent.putExtra(ActivityConstantCode.EXTRA_DATA, model)
                                    setResult(ActivityConstantCode.RESULT_FAILED_SAVE, intent)
                                    finish()
                                }
                            }
                        )
                    }
                }
            }
            R.id.back_button -> {
                onCloseEvent()
            }
            R.id.choose_template_edittext -> {
                // val modalSheetView = TemplateDialogActionBottom.newInstance()
                val modalSheetView = textInputTemplate?.text.toString().let { TemplateDialogSelection.newInstance(it) }
                modalSheetView.show(supportFragmentManager, TemplateDialogSelection.TAG)
            }
            R.id.delete_button -> {
                val dialogDelete = extraId?.let { TemplateDialogDelete.newInstance(it) }
                dialogDelete?.openDialog(supportFragmentManager)

                dialogDelete?.onConfirmClick = { _id ->
                    dialogDelete?.performLoading(true)
                    chatTemplateViewModel?.deleteAutotextById(
                        _id,
                        onSuccess = { it ->
                            val intent = Intent()
                            intent.putExtra(ActivityConstantCode.EXTRA_DATA, data)
                            setResult(ActivityConstantCode.RESULT_OK_DELETED, intent)
                            finish()
                        },
                        onError = {
                            dialogDelete?.performLoading(false)
                            if(ErrorResponseValidator.isSessionExpiredResponse(it))
                                DashboardSessionExpiredEventHandler(this).onSessionExpired()
                        }
                    )
                }

                dialogDelete?.onCancelClick = {
                    dialogDelete?.closeDialog()
                }

                //val modalSheetView = extraId?.let { TemplateDialogDelete.newInstance(it) }
                //modalSheetView?.show(supportFragmentManager, TemplateDialogDelete.TAG)
            }
        }
    }

    fun markButtonSaveDisable(isDisable: Boolean) {
        if (isDisable) {
            buttonSave?.isAllCaps = false
            buttonSave?.isEnabled = false
            buttonSave?.setBackgroundColor(ContextCompat.getColor(this, R.color.primary_10))
            buttonSave?.setTextColor(ContextCompat.getColor(this, R.color.text_color_white))
        } else {
            buttonSave?.isAllCaps = false
            buttonSave?.isEnabled = true
            buttonSave?.setBackgroundColor(ContextCompat.getColor(this, R.color.primary_50))
            buttonSave?.setTextColor(ContextCompat.getColor(this, R.color.text_color_white))
        }
    }

    private fun setEditTextValue(template: String?, title: String?, content: String?) {
        textInputTemplate?.setText(template)
        textInputTitle?.setText(title)
        textInputContent?.setText(content)
    }

    fun validateInput() {
        var titleLength = 0
        var contentLength = 0
        textInputTitle?.let { text ->
            titleLength = text.length()
        }

        textInputContent?.let { text ->
            contentLength = text.length()
        }


        if (titleLength < maxTitleLength.toInt() && contentLength < maxContentLength.toInt()
            && titleLength > 0 && contentLength > 0
        ) {
            markButtonSaveDisable(false)
        } else {
            markButtonSaveDisable(true)
        }
    }

    private fun prefillByTemplate(template: String?) {
        when (template) {
            "Pesan Pembuka" -> {
                val title = "Halo2"
                val conntent = "Halo, selamat datang di [Nama Toko]. Ada yang bisa kami bantu?"
                textInputTitle?.setText(title)
                textInputContent?.setText(conntent)
            }
            "Form Pesanan" -> {
                val title = "Form"
                val conntent =
                    "Agar pesanan lebih cepat diproses, silakan langsung isi data diri sesuai format berikut ya:" +
                        "\nNama:" +
                        "\nNo. handphone:" +
                        "\nAlamat:" +
                        "\nPesanan:" +
                        "\n[nama barang] - [jumlah]" +
                        "\n[nama barang] - [jumlah]" +
                        "\nCatatan:"
                textInputTitle?.setText(title)
                textInputContent?.setText(conntent)
            }
            "Ucapan Terimakasih" -> {
                val title = "Trims"
                val conntent =
                    "Terima kasih telah berbelanja di toko kami. Kami harap kamu puas dengan pelayanan kami. Ditunggu order berikutnya :)"
                textInputTitle?.setText(title)
                textInputContent?.setText(conntent)
            }
            "Ketersediaan Barang" -> {
                val title = "Barang Ready"
                val conntent = "Barang ready, kak. Silakan dipesan."
                textInputTitle?.setText(title)
                textInputContent?.setText(conntent)
            }
            "Cek Barang" -> {
                val title = "Cek Dulu"
                val conntent =
                    "Baik kak, mohon tunggu sebentar. Kami akan cek ketersediaan barangnya terlebih dahulu ya."
                textInputTitle?.setText(title)
                textInputContent?.setText(conntent)
            }
            else -> {
                textInputTitle?.setText("")
                textInputContent?.setText("")
            }
        }
    }

    private fun onCloseEvent(){
        if (buttonSave?.isEnabled == true) {

            if(extraStateInput!! >= 0) {
                val dialog = DialogCloseEditConfirm().newInstance()
                dialog.openDialog(supportFragmentManager)
                dialog.onConfirmClick = {
                    onBackPressed()
                }

                dialog.onCancelClick = {
                    dialog.closeDialog()
                }
            } else {
                val dialog = DialogCloseConfirm().newInstance()
                dialog.openDialog(supportFragmentManager)
                dialog.onConfirmClick = {
                    onBackPressed()
                }

                dialog.onCancelClick = {
                    dialog.closeDialog()
                }
            }

        } else {
            super.onBackPressed()
        }
    }

}
