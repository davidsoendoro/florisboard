package com.kokatto.kobold.crm

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import com.kokatto.kobold.R
import com.kokatto.kobold.databinding.ActivityAddContactBinding
import com.kokatto.kobold.extension.createBottomSheetDialog
import com.kokatto.kobold.login.LoginActivity
import com.kokatto.kobold.persistance.AppPersistence
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kokatto.kobold.crm.adapter.AddContactRecyclerAdapter
import com.kokatto.kobold.extension.showToast


class AddContactActivity : AppCompatActivity(), AddContactRecyclerAdapter.OnItemClickListener {
    lateinit var uiBinding: ActivityAddContactBinding
    private val dataList = ArrayList<DataItem>()
    private val adapter = AddContactRecyclerAdapter(dataList,this)

    var index = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contact)
        uiBinding = ActivityAddContactBinding.inflate(layoutInflater).apply {
            setContentView(root)
        }

        val recyclerView: RecyclerView = findViewById(R.id.add_contact_recycler_view)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(false)

//        val del: Button = findViewById(R.id.btn_del)

        uiBinding.koboltAddContactAddChannelText.setOnClickListener(View.OnClickListener {
            Toast.makeText(this, "Add $index", Toast.LENGTH_SHORT).show()

            val newItem = DataItem()
            dataList.add(newItem)
            adapter.notifyItemInserted(index)
            adapter.notifyItemChanged(index)
            index++
        })
//
//        del.setOnClickListener(View.OnClickListener {
//            Toast.makeText(this, "Del", Toast.LENGTH_SHORT).show()
//
//            exampleList.removeAt(index)
//            adapter.notifyItemRemoved(index)
//        })


        uiBinding.backButton.setOnClickListener {
            createConfirmationDialog()
        }
    }

    override fun onItemClick(position: Int) {
        Toast.makeText(this, "Item $position clicked", Toast.LENGTH_SHORT).show()
        val clickedItem: DataItem = dataList[position]
        dataList.removeAt(position)
        //adapter.notifyItemChanged(position)

        adapter.notifyItemRemoved(position)

        if (index>0) index--

    }

    private fun createConfirmationDialog() {
        val bottomDialog = createBottomSheetDialog(
            layoutInflater.inflate(
                R.layout.dialog_confirm_back,
                null
            )
        )

        val acceptButton = bottomDialog.findViewById<MaterialCardView>(R.id.kobold_add_contact_back_btn_yes)
        val discardButton = bottomDialog.findViewById<MaterialCardView>(R.id.kobold_add_contact_back_btn_no)

        acceptButton?.setOnClickListener {
            bottomDialog.dismiss()

//            val i = Intent(this@AddContactActivity, LoginActivity ::class.java)        // Specify any activity here e.g. home or splash or login etc
//            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
//            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            i.putExtra("EXIT", true)
//            startActivity(i)
//            finish()

        }

        discardButton?.setOnClickListener {
            bottomDialog.dismiss()
        }

        bottomDialog.show()
    }
}
