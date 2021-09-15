package com.kokatto.kobold.template

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.AutoTextModel
import com.kokatto.kobold.chattemplate.ChatTemplateViewModel
import com.kokatto.kobold.component.DovesRecyclerViewPaginator
import com.kokatto.kobold.extension.showToast
import com.kokatto.kobold.extension.vertical
import com.kokatto.kobold.template.recycleradapter.ChatTemplateRecyclerAdapter
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

class TemplateActivitySearch : AppCompatActivity(), ChatTemplateRecyclerAdapter.OnClick {

    private var chatTemplateViewModel: ChatTemplateViewModel? = ChatTemplateViewModel()

    private var buttonBack: ImageView? = null
    private var buttonClear: ImageView? = null
    private var searchResultFound: LinearLayout? = null
    private var searchResultNotFound: LinearLayout? = null
    private var searchEdittext: EditText? = null
    private var chatTemplateRecycler: RecyclerView? = null
    private var bottomLoading: LinearLayout? = null
    private var fullscreenLoading: LinearLayout? = null

    private var chatTemplateList: ArrayList<AutoTextModel> = arrayListOf()
    private var chatTemplateRecyclerAdapter: ChatTemplateRecyclerAdapter? = null

    private val isLoadingChatTemplate = AtomicBoolean(true)
    private val isLastChatTemplate = AtomicBoolean(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.template_activity_search)

        chatTemplateRecycler = findViewById<RecyclerView>(R.id.chat_template_recycler)
        bottomLoading = findViewById<LinearLayout>(R.id.bottom_loading)
        fullscreenLoading = findViewById<LinearLayout>(R.id.fullcreen_loading)

        buttonBack = findViewById(R.id.back_button)
        buttonClear = findViewById(R.id.clear_button)
        searchEdittext = findViewById(R.id.search_edittext)
        searchResultFound = findViewById(R.id.search_result_found_layout)
        searchResultNotFound = findViewById(R.id.search_result_not_found)

        buttonBack?.let { button -> button.setOnClickListener { onClicked(button) } }
        buttonClear?.let { button -> button.setOnClickListener { onClicked(button) } }
        searchEdittext!!.requestFocus()
        searchEdittext?.let { editText ->
            editText.setOnKeyListener { v, keyCode, event ->
                onKeyEdit(
                    v,
                    keyCode,
                    event
                )
            }
        }

//        searchEdittext!!.doOnTextChanged { text, start, before, count ->
//            showToast(text.toString())
//            performSearch(1, text.toString())
//        }

        if (chatTemplateRecyclerAdapter == null) {
            Log.e("From", "from search button")
            performSearch(1)

            chatTemplateRecyclerAdapter = ChatTemplateRecyclerAdapter(chatTemplateList, this)
            chatTemplateRecycler!!.adapter = chatTemplateRecyclerAdapter
            chatTemplateRecycler!!.vertical()
        }

        DovesRecyclerViewPaginator(
            recyclerView = chatTemplateRecycler!!,
            isLoading = { isLoadingChatTemplate.get() },
            loadMore = { page ->
                bottomLoading!!.isVisible = true
                Log.e("From", "from paginator")
                performSearch(page + 1)
            },
            onLast = { isLastChatTemplate.get() }
        ).run {
            threshold = 3
        }
    }

    private fun onKeyEdit(view: View, keyCode: Int?, event: KeyEvent?): Boolean {
        when (view.id) {
            R.id.search_edittext -> {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    chatTemplateList.clear()

                    chatTemplateRecycler?.isVisible = false
                    fullscreenLoading?.isVisible = true

                    Log.e("From", "from search button")
                    performSearch(1, search = searchEdittext?.text.toString())
                    return true
                }
            }
        }
        return false
    }

    private fun performSearch(page: Int = 1, search: String = "") {
        chatTemplateViewModel?.getChatTemplateList(
            page = page,
            search = search,
            onLoading = {
                Timber.e(it.toString())
                isLoadingChatTemplate.set(it)
            },
            onSuccess = { it ->
                chatTemplateList.addAll(it.data.contents)
                isLastChatTemplate.set(it.data.totalPages <= it.data.page)
//                if first page
                if (page == 1) {
                    fullscreenLoading!!.isVisible = false
                    chatTemplateRecycler!!.isVisible = true
                } else {
                    isLoadingChatTemplate.set(false)

                    bottomLoading!!.isVisible = false
                }
                //contoh insert data
//                    autoTextDatabase?.autoTextDao()?.insertAutoText(it.data.contents[1])
                chatTemplateRecyclerAdapter!!.notifyDataSetChanged()
            },
            onError = {
                showToast(it)
                fullscreenLoading!!.isVisible = false
                chatTemplateRecycler!!.isVisible = true
            }
        )
    }

    private fun onClicked(view: View) {
        when (view.id) {
            R.id.back_button -> {
                super.onBackPressed()
            }
            R.id.clear_button -> {
                searchEdittext?.setText("");
            }
        }
    }

    override fun onClicked(data: AutoTextModel) {
        Intent(applicationContext, TemplateActivityInput::class.java).apply {
            putExtra(TemplateActivityInput.EXTRA_STATE_INPUT, TemplateActivityInput.EXTRA_STATE_EDIT)
            putExtra(TemplateActivityInput.EXTRA_ID, data._id)
            putExtra(TemplateActivityInput.EXTRA_TEMPLATE, data.template)
            putExtra(TemplateActivityInput.EXTRA_TITLE, data.title)
            putExtra(TemplateActivityInput.EXTRA_CONTENT, data.content)
            startActivity(this)
        }
    }
}
