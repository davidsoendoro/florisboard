package com.kokatto.kobold.api.model.basemodel

import android.os.Parcelable
import com.kokatto.kobold.utility.CurrencyUtility
import kotlinx.parcelize.Parcelize

@Parcelize
data class TransactionModel(
    val _id: String = "",
    var address: String = "",
    var buyer: String = "",
    var channel: String = "",
    var channelAsset: String = "",
    var deliveryFee: Double = 0.0,
    var latestStatus: String = "",
    var logistic: String = "",
    var logisticAsset: String = "",
    var notes: String = "",
    var payingMethod: String = "",
    var bankType: String? = "",
    var bankAccountNo: String = "",
    var bankAccountName: String = "",
    var bankAsset: String = "",
    var phone: String = "",
    var price: Double = 0.0,
    var createdAt: Long = 0,
    var updatedAt: Long = 0,
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
        "\n\n${model.payingMethod} - ${model.bankAccountNo} - ${model.bankAccountName}" +
        "\n\nTerima Kasih :-)"
}
