package com.kokatto.kobold.dashboardcreatetransaction.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kokatto.kobold.R
import com.kokatto.kobold.constant.TransactionStatusConstant
import com.kokatto.kobold.extension.RoundedBottomSheet

class DialogAction : RoundedBottomSheet() {

    companion object {
        const val TAG = "DialogAction"
        const val STATUS = "status"
    }

    var onCancelClick: ((Boolean) -> Unit)? = null
    var onEditClick: ((Boolean) -> Unit)? = null
    var onSendClick: ((Boolean) -> Unit)? = null
    var onChatClick: ((Boolean) -> Unit)? = null
    var onUnpaidClick: ((Boolean) -> Unit)? = null
    var onUnsentClick: ((Boolean) -> Unit)? = null
    var onCompleteEditClick: ((Boolean) -> Unit)? = null

    private var unpaidLayout: LinearLayout? = null
    private var paidLayout: LinearLayout? = null
    private var unsentLayout: LinearLayout? = null
    private var completeLayout: LinearLayout? = null
    private var cancelLayout: LinearLayout? = null
    private var currentStatus: String? = ""

    fun newInstance(_status: String): DialogAction {
        return DialogAction().apply {
            arguments = Bundle().apply {
                putString(STATUS, _status)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_confirm_action, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val editBtn = view.findViewById<TextView>(R.id.action_edit)
        val cancelBtn = view.findViewById<TextView>(R.id.action_cancel)
        val notaBtn = view.findViewById<CardView>(R.id.action_nota)
        val chatBtn = view.findViewById<CardView>(R.id.action_chat)
        val unpaidBtn = view.findViewById<CardView>(R.id.action_unpaid)
        val unsentBtn = view.findViewById<CardView>(R.id.action_unsent)
        val completeEditBtn = view.findViewById<CardView>(R.id.action_edit_complete)
        val completeChatBtn = view.findViewById<CardView>(R.id.action_chat_complete)

        unpaidLayout = view.findViewById<LinearLayout>(R.id.layout_unpaid)
        paidLayout = view.findViewById<LinearLayout>(R.id.layout_paid)
        unsentLayout = view.findViewById<LinearLayout>(R.id.layout_unsent)
        completeLayout = view.findViewById<LinearLayout>(R.id.layout_complete)
        completeLayout = view.findViewById<LinearLayout>(R.id.layout_complete)
        cancelLayout = view.findViewById<LinearLayout>(R.id.layout_cancel)

        cancelBtn?.let { button ->
            button.setOnClickListener {
                onCancelClick?.invoke(true)
            }
        }
        editBtn?.let { button ->
            button.setOnClickListener {
                onEditClick?.invoke(true)
            }
        }
        notaBtn?.let { button ->
            button.setOnClickListener {
                onSendClick?.invoke(true)
            }
        }
        chatBtn?.let { button ->
            button.setOnClickListener {
                onChatClick?.invoke(true)
            }
        }
        unpaidBtn?.let { button ->
            button.setOnClickListener {
                onUnpaidClick?.invoke(true)
            }
        }
        unsentBtn?.let { button ->
            button.setOnClickListener {
                onUnsentClick?.invoke(true)
            }
        }
        completeEditBtn?.let { button ->
            button.setOnClickListener {
                onCompleteEditClick?.invoke(true)
            }
        }
        completeChatBtn?.let { button ->
            button.setOnClickListener {
                onChatClick?.invoke(true)
            }
        }

        when (currentStatus) {
            TransactionStatusConstant.PENDING -> {
                unpaidLayout?.visibility = View.VISIBLE
                paidLayout?.visibility = View.GONE
                unsentLayout?.visibility = View.GONE
                completeLayout?.visibility = View.GONE
                cancelLayout?.visibility = View.VISIBLE
            }
            TransactionStatusConstant.PAID -> {
                unpaidLayout?.visibility = View.GONE
                paidLayout?.visibility = View.VISIBLE
                unsentLayout?.visibility = View.GONE
                completeLayout?.visibility = View.GONE
                cancelLayout?.visibility = View.VISIBLE
            }
            TransactionStatusConstant.SENT -> {
                unpaidLayout?.visibility = View.GONE
                paidLayout?.visibility = View.VISIBLE
                unsentLayout?.visibility = View.VISIBLE
                completeLayout?.visibility = View.GONE
                cancelLayout?.visibility = View.VISIBLE
            }
            else -> {
                unpaidLayout?.visibility = View.GONE
                paidLayout?.visibility = View.GONE
                unsentLayout?.visibility = View.GONE
                completeLayout?.visibility = View.VISIBLE
                cancelLayout?.visibility = View.GONE
            }
        }
    }

    fun openDialog(fragmentManager: FragmentManager) {
        this.show(fragmentManager, TAG)
    }

    fun closeDialog() {
        dismiss()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        arguments?.getString(DialogAction.STATUS)?.let {
            currentStatus = it
        }
    }
}
