package com.kokatto.kobold.dashboardcreatetransaction

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.kokatto.kobold.R
import com.kokatto.kobold.dashboardcreatetransaction.recycleradapter.UnprocessedRecyclerAdapter
import com.kokatto.kobold.extension.showToast
import com.kokatto.kobold.extension.vertical

class SentFragment: Fragment(R.layout.fragment_sent) , UnprocessedRecyclerAdapter.OnClick {

    private var sentRecyclerAdapter: UnprocessedRecyclerAdapter? = null
    private var sentRecycler: RecyclerView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sentRecycler = view.findViewById(R.id.sent_recycler)
        sentRecyclerAdapter = UnprocessedRecyclerAdapter(this)

        sentRecycler!!.adapter = sentRecyclerAdapter
        sentRecycler!!.vertical()
    }

    override fun onClicked(data: String) {
        showToast(data)
    }
}
