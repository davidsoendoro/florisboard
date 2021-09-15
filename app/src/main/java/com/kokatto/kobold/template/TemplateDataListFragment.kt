package com.kokatto.kobold.template

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.AutoTextModel
import com.kokatto.kobold.api.model.response.GetPaginatedAutoTextResponse
import com.kokatto.kobold.chattemplate.ChatTemplateViewModel
import com.kokatto.kobold.component.DovesRecyclerViewPaginator
import com.kokatto.kobold.extension.showToast
import com.kokatto.kobold.extension.vertical
import com.kokatto.kobold.roomdb.AutoTextDatabase
import com.kokatto.kobold.template.recycleradapter.ChatTemplateRecyclerAdapter
import kotlinx.coroutines.InternalCoroutinesApi
import timber.log.Timber
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.collections.ArrayList

class TemplateDataListFragment : Fragment(R.layout.template_fragment_data_list), ChatTemplateRecyclerAdapter.OnClick {

    private var chatTemplateList: ArrayList<AutoTextModel> = arrayListOf()
    private var chatTemplateRecyclerAdapter: ChatTemplateRecyclerAdapter? = null

    private var chatTemplateViewModel: ChatTemplateViewModel? = ChatTemplateViewModel()

    private val isLoadingChatTemplate = AtomicBoolean(true)
    private val isLastChatTemplate = AtomicBoolean(false)

    private var autoTextDatabase: AutoTextDatabase? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        autoTextDatabase = AutoTextDatabase.getInstance(requireContext())

        val chatTemplateRecycler = view.findViewById<RecyclerView>(R.id.chat_template_recycler)
        val btnCreate = view.findViewById<CardView>(R.id.create_template_button)
        val bottomLoading = view.findViewById<LinearLayout>(R.id.bottom_loading)
        val fullscreenLoading = view.findViewById<LinearLayout>(R.id.fullcreen_loading)

        if (chatTemplateRecyclerAdapter == null) {
            chatTemplateViewModel?.getChatTemplateList(
                onLoading = {
                    Timber.e(it.toString())
                    isLoadingChatTemplate.set(it)
                },
                onSuccess = { it ->
                    chatTemplateList.addAll(it.data.contents)
                    fullscreenLoading.isVisible = false
                    chatTemplateRecycler.isVisible = true
                    //contoh insert data
//                    autoTextDatabase?.autoTextDao()?.insertAutoText(it.data.contents[1])
                    chatTemplateRecyclerAdapter!!.notifyDataSetChanged()
                },
                onError = {
                    showToast(it)
                    fullscreenLoading.isVisible = false
                    chatTemplateRecycler.isVisible = true
                }
            )
            chatTemplateRecyclerAdapter = ChatTemplateRecyclerAdapter(chatTemplateList, this)
            chatTemplateRecycler.adapter = chatTemplateRecyclerAdapter
            chatTemplateRecycler.vertical()
        }

        DovesRecyclerViewPaginator(
            recyclerView = chatTemplateRecycler,
            isLoading = { isLoadingChatTemplate.get() },
            loadMore = {
                bottomLoading.isVisible = true
                chatTemplateViewModel?.getChatTemplateList(
                    page = it,
                    onLoading = {
                        Timber.e(it.toString())
                        isLoadingChatTemplate.set(it)
                    },
                    onSuccess = { it ->
                        isLastChatTemplate.set(it.data.totalPages <= it.data.page)
                        Timber.e("isLastChatTemplate " + isLastChatTemplate.get().toString())

                        isLoadingChatTemplate.set(false)
                        chatTemplateList.addAll(it.data.contents)
                        chatTemplateRecyclerAdapter!!.notifyDataSetChanged()

                        bottomLoading.isVisible = false
                    },
                    onError = {
                        showToast(it)
                        bottomLoading.isVisible = false
                    }
                )
            },
            onLast = { isLastChatTemplate.get() }
        ).run {
            threshold = 3
        }

        btnCreate?.let { button -> button.setOnClickListener { onCreateClicked(button) } }
    }

    private fun onCreateClicked(view: View) {
        when (view.id) {
            R.id.create_template_button -> {
                val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
                val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction
                    .setReorderingAllowed(true)
                    .replace(
                        R.id.template_fragment_container_view,
                        TemplateInputFragment()
                    )
                    .commit()
            }
        }
    }

    override fun onDestroy() {
        chatTemplateViewModel?.onDestroy()
        super.onDestroy()
    }

    override fun onClicked(data: AutoTextModel) {
        showToast(data.toString())
    }

}

