package com.kokatto.kobold.constant

enum class TransactionFilterEnum(val code: String, val desc: String) {
    ALL("", "Semua"),
    PENDING("PENDING", "Belum diproses"),
    PAID("PAID", "Dibayar"),
    SENT("SENT", "Dikirim"),
    CANCEL("CANCEL", "Dibatalkan"),
    COMPLETE("COMPLETE", "Selesai"),

}
