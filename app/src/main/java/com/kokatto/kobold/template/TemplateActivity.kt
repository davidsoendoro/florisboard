package com.kokatto.kobold.template

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.recyclerview.widget.RecyclerView
import com.kokatto.kobold.api.model.response.PaginatedAutoTextResponse
import com.kokatto.kobold.chattemplate.ChatTemplateViewModel
import com.kokatto.kobold.extension.showToast
import com.kokatto.kobold.extension.vertical
import com.kokatto.kobold.template.recycleradapter.ChatTemplateRecyclerAdapter
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.setup.SetupActivity
import dev.patrickgold.florisboard.util.checkIfImeIsEnabled

class TemplateActivity : AppCompatActivity(), ChatTemplateRecyclerAdapter.OnClick {

    private var activeButton: Button? = null
    private var warningLayout: LinearLayout? = null

    private var chatTemplateList: ArrayList<PaginatedAutoTextResponse.Content> = arrayListOf()
    private var chatTemplateRecyclerAdapter: ChatTemplateRecyclerAdapter? = null

    private var chatTemplateViewModel: ChatTemplateViewModel? = ChatTemplateViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.template_activity)

        activeButton = findViewById(R.id.popup_keyboard_active_button)
        warningLayout = findViewById(R.id.layout_active_keyboard)
        val chatTemplateRecycler = findViewById<RecyclerView>(R.id.chat_template_recycler)

        activeButton?.let { button -> button.setOnClickListener { onButtonClicked(button) } }

//        check if data is available or not
//        if (savedInstanceState == null) {
//            supportFragmentManager.commit {
//                setReorderingAllowed(true)
//                add<TemplateEmptyFragment>(R.id.template_fragment_container_view)
//            }
//        }

        if (checkIfImeIsEnabled(this)) {
            warningLayout?.let { layout -> layout.visibility = View.GONE }
        } else {
            warningLayout?.let { layout -> layout.visibility = View.VISIBLE }
        }

        if(chatTemplateRecyclerAdapter == null) {
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
    }

    override fun onDestroy() {
        chatTemplateViewModel?.onDestroy()
        super.onDestroy()
    }

    private fun onButtonClicked(view: View) {
        when (view.id) {
            R.id.popup_keyboard_active_button -> {
                Intent(this, SetupActivity::class.java).apply {
                    putExtra(SetupActivity.EXTRA_SHOW_SINGLE_STEP, SetupActivity.Step.ENABLE_IME)
                    startActivity(this)
                    warningLayout?.let { layout -> layout.visibility = View.GONE }
                }
            }
        }
    }

//    on chat template data clicked
    override fun onClicked(data: PaginatedAutoTextResponse.Content) {
        showToast(data.toString())
    }

}
