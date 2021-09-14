package com.kokatto.kobold.template

import android.os.Bundle
import android.view.View
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.AutoTextModel
import com.kokatto.kobold.chattemplate.ChatTemplateViewModel
import com.kokatto.kobold.extension.showToast
import com.kokatto.kobold.extension.vertical
import com.kokatto.kobold.template.recycleradapter.ChatTemplateRecyclerAdapter

class TemplateDataListFragment : Fragment(R.layout.template_fragment_data_list), ChatTemplateRecyclerAdapter.OnClick {

    private var chatTemplateList: ArrayList<AutoTextModel> = arrayListOf()
    private var chatTemplateRecyclerAdapter: ChatTemplateRecyclerAdapter? = null

    private var chatTemplateViewModel: ChatTemplateViewModel? = ChatTemplateViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val chatTemplateRecycler = view.findViewById<RecyclerView>(R.id.chat_template_recycler)
        val btnCreate = view.findViewById<CardView>(R.id.create_template_button)

        if (chatTemplateRecyclerAdapter == null) {
            chatTemplateViewModel?.getChatTemplateList(
                onSuccess = { it ->
                    chatTemplateList.addAll(it.data.contents)
                    chatTemplateRecyclerAdapter!!.notifyDataSetChanged()
                },
                onError = {
                    showToast(it)
                }
            )
            chatTemplateRecyclerAdapter = ChatTemplateRecyclerAdapter(chatTemplateList, this)
            chatTemplateRecycler.adapter = chatTemplateRecyclerAdapter
            chatTemplateRecycler.vertical()
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

