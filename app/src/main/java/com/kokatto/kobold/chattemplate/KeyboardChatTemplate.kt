package com.kokatto.kobold.chattemplate

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import com.kokatto.kobold.R
import com.kokatto.kobold.api.impl.ErrorResponseValidator
import com.kokatto.kobold.api.model.basemodel.AutoTextModel
import com.kokatto.kobold.component.DovesRecyclerViewPaginator
import com.kokatto.kobold.extension.isConnectedToInternet
import com.kokatto.kobold.extension.showSnackBar
import com.kokatto.kobold.extension.showToast
import com.kokatto.kobold.extension.vertical
import com.kokatto.kobold.roomdb.AutoTextDatabase
import com.kokatto.kobold.template.recycleradapter.ChatTemplateRecyclerAdapter
import dev.patrickgold.florisboard.ime.core.FlorisBoard
import dev.patrickgold.florisboard.ime.text.key.KeyCode
import dev.patrickgold.florisboard.ime.text.keyboard.TextKeyData
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.properties.Delegates

class KeyboardChatTemplate : ConstraintLayout, ChatTemplateRecyclerAdapter.Delegate {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val florisboard: FlorisBoard? = FlorisBoard.getInstanceOrNull()

    private var chatTemplateList: ArrayList<AutoTextModel> = arrayListOf()
    private var adapter: ChatTemplateRecyclerAdapter? = null

    private var chatTemplateRecycler: RecyclerView? = null
    private var chatTemplateRecyclerLoading: ProgressBar? = null

    private var chatTemplateViewModel: ChatTemplateViewModel? = ChatTemplateViewModel()

    private var isLoadingChatTemplate: Boolean by Delegates.observable(true) { _, oldValue, newValue ->
        if (oldValue != newValue) {
            if (newValue) {
                chatTemplateRecyclerLoading?.visibility = VISIBLE
            } else {
                chatTemplateRecyclerLoading?.visibility = GONE
            }
        }
    }
    private val isLastChatTemplate = AtomicBoolean(false)

    private var messageSnackbar: Snackbar? = null

    var dataUnavailableLayout: LinearLayout? = null
    var dataAvailableLayout: LinearLayout? = null
    var connectionErrorLayout: LinearLayout? = null
    var reloadButton: MaterialCardView? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val searchButton: ImageView = findViewById(R.id.search_button)
        val backButton: TextView = findViewById(R.id.back_button)
        val toolbarCreateTemplateButton: LinearLayout = findViewById(R.id.toolbar_create_template_button)
        val createTemplateButton: MaterialCardView = findViewById(R.id.create_template_button)
        chatTemplateRecycler = findViewById(R.id.chat_template_recycler)
        chatTemplateRecyclerLoading = findViewById(R.id.chat_template_recycler_loading)
        dataAvailableLayout = findViewById(R.id.data_available_layout)
        dataUnavailableLayout = findViewById(R.id.data_unavailable_layout)
        connectionErrorLayout = findViewById(R.id.connection_error_layout)
        reloadButton = findViewById(R.id.reload_button)

        searchButton.setOnClickListener {
            florisboard?.inputFeedbackManager?.keyPress()
            florisboard?.openSearchEditor(R.id.kobold_search_result)
        }
        toolbarCreateTemplateButton.setOnClickListener {
            florisboard?.inputFeedbackManager?.keyPress()
            florisboard?.setActiveInput(R.id.kobold_menu_create_chat_template)
        }
        createTemplateButton.setOnClickListener {
            florisboard?.inputFeedbackManager?.keyPress()
            florisboard?.setActiveInput(R.id.kobold_menu_create_chat_template)
        }
        backButton.setOnClickListener {
            florisboard?.inputFeedbackManager?.keyPress(TextKeyData(code = KeyCode.CANCEL))
            florisboard?.setActiveInput(R.id.kobold_mainmenu)
        }
        reloadButton?.setOnClickListener {
            loadChatTemplate()
        }
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (changedView is KeyboardChatTemplate && visibility == View.VISIBLE && changedView == this) {
            adapter = ChatTemplateRecyclerAdapter(chatTemplateList, this)
            chatTemplateRecycler?.adapter = adapter
            val previousSize = adapter?.dataList?.size
            adapter?.dataList?.clear()
            previousSize?.let { adapter?.notifyItemRangeRemoved(0, it) }
            isLastChatTemplate.set(false)
            loadChatTemplate()
        }
    }

    fun loadChatTemplate() {
        florisboard?.koboldState = FlorisBoard.KoboldState.NORMAL
        connectionErrorLayout?.isVisible = this.isConnectedToInternet().not()

//        isLoadingChatTemplate = true
        chatTemplateViewModel?.getChatTemplateList(
            onLoading = {
//                Timber.e(it.toString())
                isLoadingChatTemplate = it
            },
            onSuccess = { it ->
                AutoTextDatabase.getInstance(context)?.autoTextDao()?.clearAutoTextTable()
                it.data.contents.forEach { item ->
                    AutoTextDatabase.getInstance(context)?.autoTextDao()?.insertAutoText(item)
                }
                chatTemplateList.addAll(it.data.contents)
//                chatTemplateList.addAll(arrayListOf())
                adapter?.notifyItemRangeInserted(0, it.data.contents.size)

                dataAvailableLayout?.isVisible = chatTemplateList.isNullOrEmpty().not()
                dataUnavailableLayout?.isVisible = chatTemplateList.isNullOrEmpty()
            },
            onError = {
                if(ErrorResponseValidator.isSessionExpiredResponse(it))
                    florisboard?.setActiveInput(R.id.kobold_login)
                else
                    showSnackBar("Koneksi internet tidak tersedia.", R.color.snackbar_error)
            }
        )

        chatTemplateRecycler?.let {
            val bottomLoading = findViewById<LinearLayout>(R.id.bottom_loading)

            DovesRecyclerViewPaginator(
                recyclerView = it,
                isLoading = { isLoadingChatTemplate },
                loadMore = { loadMoreData ->
                    bottomLoading.isVisible = true
                    chatTemplateViewModel?.getChatTemplateList(
                        page = loadMoreData + 1,
                        onLoading = { loadData ->
//                            Timber.e(loadData.toString())
                            isLoadingChatTemplate = loadData
                        },
                        onSuccess = { successData ->
                            isLastChatTemplate.set(successData.data.totalPages <= successData.data.page)

                            val initialSize = chatTemplateList.size
                            chatTemplateList.addAll(successData.data.contents)
                            successData.data.contents.forEach { item ->
                                AutoTextDatabase.getInstance(context)?.autoTextDao()?.insertAutoText(item)
                            }
                            val finalSize = successData.data.contents.size
                            adapter?.notifyItemRangeInserted(initialSize, finalSize)

                            isLoadingChatTemplate = false
                            bottomLoading.isVisible = false
                        },
                        onError = { errorMessage ->
                            //showToast(errorMessage)
                            isLoadingChatTemplate = false
                            bottomLoading.isVisible = false

                            if(ErrorResponseValidator.isSessionExpiredResponse(errorMessage))
                                florisboard?.setActiveInput(R.id.kobold_login)
                            else
                                showToast(errorMessage)
                        }
                    )
                },
                onLast = { isLastChatTemplate.get() }
            ).run {
                threshold = 5
            }
        }
        chatTemplateRecycler?.vertical()
    }

    override fun onClicked(data: AutoTextModel) {
        florisboard?.inputFeedbackManager?.keyPress()
        florisboard?.textInputManager?.activeEditorInstance?.commitText(data.content.toString())

        florisboard?.setActiveInput(R.id.text_input)
    }

    override fun getSearchText(): String = ""
}
