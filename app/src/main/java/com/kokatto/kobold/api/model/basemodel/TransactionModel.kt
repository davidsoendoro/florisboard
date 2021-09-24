package com.kokatto.kobold.api.model.basemodel

import android.os.Parcelable
import com.kokatto.kobold.utility.CurrencyUtility
import kotlinx.parcelize.Parcelize

@Parcelize
data class TransactionModel(
    val _id: String = "",
    val address: String,
    val buyer: String,
    val channel: String,
    val channelAsset: String = "",
    val deliveryFee: Int,
    val latestStatus: String = "",
    val logistic: String,
    val logisticAsset: String = "",
    val notes: String,
    val payingMethod: String,
    val bankType: String = "",
    val bankAccountNo: String = "",
    val bankAccountName: String = "",
    val bankAsset: String = "",
    val phone: String,
    val price: Int,
    val createdAt: Long = 0,
    val updatedAt: Long = 0,
) : Parcelable

fun createTransactionChat(model: TransactionModel): String {
    return "Halo ini detail transaksi nya ya :" +
        "\nPembeli: ${model.buyer}" +
        "\nNomor Telp: ${model.phone}" +
        "\nAlamat: ${model.address}" +
        "\n\n===" +
        "\n\nUntuk Pembayaran: ${model.notes}" +
        "\nHarga: ${CurrencyUtility.currencyFormatter(model.price)}" +
        "\nMetode Bayar: ${model.payingMethod}" +
        "\nOngkir: ${CurrencyUtility.currencyFormatter(model.deliveryFee)}" +
        "\nKurir: ${model.logistic}" +
        "\n\nSilahkan, proses pembayaran bisa via:" +
        "\n\n${model.bankAsset} - ${model.bankAccountNo} - ${model.bankAccountName}" +
        "\n\nTerima Kasih :-)"
}
