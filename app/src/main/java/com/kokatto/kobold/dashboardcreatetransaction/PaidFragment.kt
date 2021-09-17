package com.kokatto.kobold.dashboardcreatetransaction

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.kokatto.kobold.R
import com.kokatto.kobold.dashboardcreatetransaction.recycleradapter.UnprocessedRecyclerAdapter
import com.kokatto.kobold.extension.showToast
import com.kokatto.kobold.extension.vertical

class PaidFragment: Fragment(R.layout.fragment_paid) , UnprocessedRecyclerAdapter.OnClick {

    private var paidRecyclerAdapter: UnprocessedRecyclerAdapter? = null
    private var paidRecycler: RecyclerView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        paidRecycler = view.findViewById(R.id.paid_recycler)
        paidRecyclerAdapter = UnprocessedRecyclerAdapter(this)

        paidRecycler!!.adapter = paidRecyclerAdapter
        paidRecycler!!.vertical()
    }

    override fun onClicked(data: String) {
        showToast(data)
    }
}
