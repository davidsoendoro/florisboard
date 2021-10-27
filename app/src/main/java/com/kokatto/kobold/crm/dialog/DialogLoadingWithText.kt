package com.kokatto.kobold.crm.dialog

import android.app.Activity
import android.app.AlertDialog
import android.widget.TextView
import com.kokatto.kobold.R

class DialogLoadingWithText(val mActivity: Activity) {
    private lateinit var isdialog: AlertDialog
    private lateinit var progressText: TextView

    fun startLoading(){
        /**set View*/
        val infalter = mActivity.layoutInflater
        val dialogView = infalter.inflate(R.layout.dialog_loading_with_text,null)
        progressText = dialogView.findViewById(R.id.progress_text)

        /**set Dialog*/
        val bulider = AlertDialog.Builder(mActivity)
        bulider.setView(dialogView)
        bulider.setCancelable(false)
        isdialog = bulider.create()
        isdialog.window?.getDecorView()?.setBackgroundResource(android.R.color.transparent);
        isdialog.show()
    }

    fun isDismiss(){
        isdialog.dismiss()
    }

    fun setProgressText(text: String){
        progressText.setText(text)
    }
}
