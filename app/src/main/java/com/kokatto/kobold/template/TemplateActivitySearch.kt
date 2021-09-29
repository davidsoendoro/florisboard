package com.kokatto.kobold.template

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.AutoTextModel
import com.kokatto.kobold.api.model.basemodel.TransactionModel
import com.kokatto.kobold.chattemplate.ChatTemplateViewModel
import com.kokatto.kobold.component.DovesRecyclerViewPaginator
import com.kokatto.kobold.constant.ActivityConstantCode
import com.kokatto.kobold.dashboardcreatetransaction.DetailActivity
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
    private var searchResultNotFoundLayout: LinearLayout? = null
    private var searchEdittext: EditText? = null
    private var chatTemplateRecycler: RecyclerView? = null
    private var bottomLoading: LinearLayout? = null
    private var fullscreenLoading: LinearLayout? = null

    private var chatTemplateList: ArrayList<AutoTextModel> = arrayListOf()
    private var chatTemplateRecyclerAdapter: ChatTemplateRecyclerAdapter? = null

    private val isLoadingChatTemplate = AtomicBoolean(true)
    private val isLastChatTemplate = AtomicBoolean(false)

    private var launchInputActivity: ActivityResultLauncher<Intent>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.template_activity_search)

        chatTemplateRecycler = findViewById(R.id.chat_template_recycler)
        bottomLoading = findViewById(R.id.bottom_loading)
        fullscreenLoading = findViewById(R.id.fullcreen_loading)

        buttonBack = findViewById(R.id.back_button)
        buttonClear = findViewById(R.id.clear_button)
        searchEdittext = findViewById(R.id.search_edittext)
        searchResultFound = findViewById(R.id.search_result_found_layout)
        searchResultNotFoundLayout = findViewById(R.id.search_result_not_found_layout)

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

        launchInputActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == ActivityConstantCode.RESULT_OK_DELETED) {
                val data: Intent? = result.data
                chatTemplateList.remove(data?.getParcelableExtra<AutoTextModel>(ActivityConstantCode.EXTRA_DATA))
                chatTemplateRecyclerAdapter!!.notifyDataSetChanged()
            }
        }
    }

    private fun onKeyEdit(view: View, keyCode: Int?, event: KeyEvent?): Boolean {
        when (view.id) {
            R.id.search_edittext -> {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event?.action == KeyEvent.ACTION_DOWN) {
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
                if(chatTemplateList.size == 0){
                    searchResultNotFoundLayout?.isVisible = true
                    fullscreenLoading?.isVisible = false
                    return@getChatTemplateList
                }
                else
                    searchResultNotFoundLayout?.isVisible = false

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
        val intent = Intent(applicationContext, TemplateActivityInput::class.java)
        intent.putExtra(TemplateActivityInput.EXTRA_ID, data._id)
        intent.putExtra(ActivityConstantCode.EXTRA_DATA, data)
        intent.putExtra(ActivityConstantCode.EXTRA_MODE, ActivityConstantCode.EXTRA_EDIT)
        launchInputActivity?.launch(intent)
    }

    override fun onResume() {
        super.onResume()
        performSearch(1, search = searchEdittext?.text.toString())
    }
}
