package com.kokatto.kobold.crm.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.kokatto.kobold.R
import com.kokatto.kobold.api.model.basemodel.BankModel
import com.kokatto.kobold.api.model.basemodel.fromIntent
import com.kokatto.kobold.constant.ContactSortEnum
import com.kokatto.kobold.crm.adapter.ContactSortRecylerAdapter
import com.kokatto.kobold.databinding.BottomsheetSortingBinding
import com.kokatto.kobold.extension.RoundedBottomSheet
import com.kokatto.kobold.extension.vertical

class DialogContactSort : RoundedBottomSheet() {

    val TAG = "DialogContactSort"

    fun newInstance(): DialogContactSort {
        return DialogContactSort()
    }

    var onConfirmClick: ((selected: ContactSortEnum) -> Unit)? = null

    private var itemList: MutableList<ContactSortEnum> = arrayListOf()
    private var selecteditem: ContactSortEnum? = ContactSortEnum.NEWEST
    private var recyclerAdapter: ContactSortRecylerAdapter? = null
    private var recyclerView: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return BottomsheetSortingBinding.inflate(inflater, container, false).root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val backButton = view.findViewById<ImageView>(R.id.selector_back_button)
        val confirm_button = view.findViewById<CardView>(R.id.confirm_button)
        recyclerView = view.findViewById<RecyclerView>(R.id.spinner_selector_recycler_view)

        backButton.setOnClickListener {
            dismiss()
        }

        confirm_button.setOnClickListener {
            onConfirmClick?.invoke(selecteditem!!)
            dismiss()
        }

        initList()
        bindAdapterSort()
    }

    private fun initList() {
        itemList.add(ContactSortEnum.NEWEST)
        itemList.add(ContactSortEnum.NAMEASC)
        itemList.add(ContactSortEnum.NAMEDESC)
    }

    private fun bindAdapterSort() {
        recyclerAdapter = ContactSortRecylerAdapter(selecteditem, itemList)
        recyclerView?.adapter = recyclerAdapter
        recyclerView?.vertical(false)
        recyclerAdapter!!.notifyDataSetChanged()
        recyclerAdapter!!.onItemClick = {
            selecteditem = it
        }
    }

    fun openSelector(fragmentManager: FragmentManager, item: ContactSortEnum) {
        selecteditem = item
        recyclerView?.adapter?.notifyDataSetChanged()
        this.show(fragmentManager, TAG)
    }
}
