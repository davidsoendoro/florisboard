package com.kokatto.kobold.template

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.kokatto.kobold.R
import com.kokatto.kobold.api.impl.DashboardSessionExpiredEventHandler
import com.kokatto.kobold.api.impl.ErrorResponseValidator
import com.kokatto.kobold.api.model.basemodel.AutoTextModel
import com.kokatto.kobold.chattemplate.ChatTemplateViewModel
import com.kokatto.kobold.component.DovesRecyclerViewPaginator
import com.kokatto.kobold.constant.ActivityConstantCode
import com.kokatto.kobold.extension.vertical
import com.kokatto.kobold.roomdb.AutoTextDatabase
import com.kokatto.kobold.template.recycleradapter.ChatTemplateRecyclerAdapter
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

class TemplateDataListFragment : Fragment(R.layout.template_fragment_data_list), ChatTemplateRecyclerAdapter.OnClick {

    var templateActivityListener: TemplateActivityListener? = null

    private var chatTemplateList: ArrayList<AutoTextModel> = arrayListOf()
    private var chatTemplateRecyclerAdapter: ChatTemplateRecyclerAdapter? = null

    private var chatTemplateViewModel: ChatTemplateViewModel? = ChatTemplateViewModel()

    private var chatTemplateRecycler: RecyclerView? = null
    private var bottomLoading: LinearLayout? = null
    private var fullscreenLoading: LinearLayout? = null

    private val isLoadingChatTemplate = AtomicBoolean(true)
    private val isLastChatTemplate = AtomicBoolean(false)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            templateActivityListener = context as TemplateActivityListener
        } catch (castException: ClassCastException) {
            // Listener cannot be attached
        }
    }

    private var autoTextDatabase: AutoTextDatabase? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        autoTextDatabase = AutoTextDatabase.getInstance(requireContext())

        chatTemplateRecycler = view.findViewById<RecyclerView>(R.id.chat_template_recycler)
        val btnCreate = view.findViewById<CardView>(R.id.create_template_button)
        bottomLoading = view.findViewById<LinearLayout>(R.id.bottom_loading)
        fullscreenLoading = view.findViewById<LinearLayout>(R.id.fullcreen_loading)

        if (chatTemplateRecyclerAdapter == null) {
            fullscreenLoading?.isVisible = true
            getChatTemplateList(1)
            chatTemplateRecyclerAdapter = ChatTemplateRecyclerAdapter(chatTemplateList, this)
            chatTemplateRecycler!!.adapter = chatTemplateRecyclerAdapter
            chatTemplateRecycler!!.vertical()
        }

        DovesRecyclerViewPaginator(
            recyclerView = chatTemplateRecycler!!,
            isLoading = { isLoadingChatTemplate.get() },
            loadMore = {
                bottomLoading!!.isVisible = true
                getChatTemplateList(it + 1)
            },
            onLast = { isLastChatTemplate.get() }
        ).run {
            threshold = 3
        }

        btnCreate?.let { button -> button.setOnClickListener { onCreateClicked(button) } }
    }

    private fun getChatTemplateList(page: Int = 1) {
        chatTemplateViewModel?.getChatTemplateList(
            page = page,
            onLoading = {
                Timber.e(it.toString())
                isLoadingChatTemplate.set(it)
            },
            onSuccess = { it ->

                // State if Empty
                if (it.data.totalRecord <= 0) {
                    templateActivityListener?.openEmptyFragment()
                } else {
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
                }
            },
            onError = {
                if(ErrorResponseValidator.isSessionExpiredResponse(it))
                    DashboardSessionExpiredEventHandler(requireContext()).onSessionExpired()
                templateActivityListener?.openErrorFragment()
                fullscreenLoading!!.isVisible = false
                chatTemplateRecycler!!.isVisible = true
            }
        )
    }

    private fun onCreateClicked(view: View) {
        when (view.id) {
            R.id.create_template_button -> {
                templateActivityListener?.openInputTemplate()
            }
        }
    }

    override fun onDestroy() {
        chatTemplateViewModel?.onDestroy()
        super.onDestroy()
    }

    override fun onClicked(data: AutoTextModel) {
        templateActivityListener?.openEditTemplate(data)
    }

}

