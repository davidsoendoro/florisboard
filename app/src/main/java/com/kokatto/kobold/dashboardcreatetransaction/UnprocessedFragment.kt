package com.kokatto.kobold.dashboardcreatetransaction

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.kokatto.kobold.R
import com.kokatto.kobold.dashboardcreatetransaction.recycleradapter.UnprocessedRecyclerAdapter
import com.kokatto.kobold.extension.showToast
import com.kokatto.kobold.extension.vertical

class UnprocessedFragment: Fragment(R.layout.fragment_unprocessed), UnprocessedRecyclerAdapter.OnClick {

    private var unprocessedRecyclerAdapter: UnprocessedRecyclerAdapter? = null
    private var unprocesedRecycler: RecyclerView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        unprocesedRecycler = view.findViewById(R.id.unprocessed_recycler)
        unprocessedRecyclerAdapter = UnprocessedRecyclerAdapter(this)

        unprocesedRecycler!!.adapter = unprocessedRecyclerAdapter
        unprocesedRecycler!!.vertical()
    }

    override fun onClicked(data: String) {
        showToast(data)
    }
}
