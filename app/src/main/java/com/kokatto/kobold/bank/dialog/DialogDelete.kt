package com.kokatto.kobold.bank.dialog

import com.kokatto.kobold.extension.RoundedBottomSheet

class DialogDelete: RoundedBottomSheet() {

    val TAG = "DialogSent"

    fun newInstance(): DialogDelete? {
        return DialogDelete()
    }

    var onConfirmClick: ((Boolean) -> Unit)? = null


}
