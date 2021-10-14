package com.kokatto.kobold.api.model.basemodel
import android.os.Parcelable
import com.kokatto.kobold.constant.ActivityConstantCode.Companion.BANK_TYPE_OTHER
import kotlinx.parcelize.Parcelize

@Parcelize
data class BankModel(
    val _id: String,
    val bankType: String = BANK_TYPE_OTHER,
    val bank: String = "",
    val accountNo: String = "",
    val accountHolder: String = "",
    val asset: String = ""
) : Parcelable

fun getBankInfoStringFormat(bank: BankModel): String {
    return "(${bank.bank}) ${bank.accountNo} • ${bank.accountHolder}"
}

fun getBankInfoFormatToString(text: String): BankModel {
    val textListTemp = text.split(" ")
    var accountNameTemp = ""
    lateinit var bankModel: BankModel

    for (i in 3..textListTemp.size - 1) {
        accountNameTemp += textListTemp[i] + " "
    }

    try {
        bankModel = BankModel(
            _id = "",
            bankType = textListTemp[0].substring(1, textListTemp[0].length - 1),
            accountNo = textListTemp[1],
            accountHolder = accountNameTemp
        )
    } catch (e: IndexOutOfBoundsException) {
        bankModel = BankModel(
            _id = "",
            bankType = textListTemp[0],
            accountNo = "",
            accountHolder = ""
        )
    }

    return bankModel
}
