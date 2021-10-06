package com.kokatto.kobold.login.dialog

import android.app.Activity
import android.app.AlertDialog
import com.kokatto.kobold.R

class DialogLoading(val mActivity: Activity) {
    private lateinit var isdialog: AlertDialog

    fun startLoading(){
        /**set View*/
        val infalter = mActivity.layoutInflater
        val dialogView = infalter.inflate(R.layout.dialog_loading,null)

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
}
