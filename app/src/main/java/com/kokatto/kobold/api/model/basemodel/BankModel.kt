package com.kokatto.kobold.api.model.basemodel
import android.os.Parcelable
import com.kokatto.kobold.constant.ActivityConstantCode.Companion.BANK_TYPE_OTHER
import kotlinx.parcelize.Parcelize

@Parcelize
data class BankModel(
    val _id: String,
    val bankType: String = BANK_TYPE_OTHER,
    val bank: String,
    val accountNo: String,
    val accountHolder: String,
    val asset: String
) : Parcelable

fun getBankInfoStringFormat(bank: BankModel): String {
    return "(${bank.bank}) ${bank.accountNo} • ${bank.accountHolder}"
}
