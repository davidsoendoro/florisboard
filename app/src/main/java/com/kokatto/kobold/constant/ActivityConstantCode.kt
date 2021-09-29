package com.kokatto.kobold.constant

class ActivityConstantCode {

    companion object {
        // Extra
        val EXTRA_DATA = "EXTRA_DATA"
        val EXTRA_CREATE = -1
        val EXTRA_EDIT = 1
        val EXTRA_MODE = "MODE"

        // Activity result code
        val STATUS_TO_PAID = 100
        val STATUS_TO_SENT = 101
        val STATUS_TO_COMPLETE = 102
        val STATUS_TO_CANCEL = 103
        val RESULT_OK_DELETED = 200
        val RESULT_OK_CREATED = 201
        val RESULT_OK_UPDATED = 202

        // Kirim Nota
        val BELUM_ADA = "Belum ada"
        val WHATSAPP = "WhatsApp"
        val WHATSAPP_BUSINESS = "WhatsApp Business"
        val LINE = "Line"
        val FACEBOOK_MESSENGER = "Facebook Messenger"
        val INSTAGRAM = "Instagram"
        val BUKALAPAK_CHAT = "Bukalapak Chat"
        val TOKOPEDIA_CHAT = "Tokopedia Chat"
        val SHOPEE_CHAT = "Shopee Chat"

        val WHATSAPP_PKG = "com.whatsapp"
        val WHATSAPP_BUSINESS_PKG = "com.whatsapp.w4b"
        val LINE_PKG = "jp.naver.line.android"
        val FACEBOOK_MESSENGER_PKG = "com.facebook.orca"
        val INSTAGRAM_PKG = "com.instagram.android"
        val BUKALAPAK_CHAT_PKG = "com.bukalapak.android"
        val TOKOPEDIA_CHAT_PKG = "com.tokopedia.tkpd"
        val SHOPEE_CHAT_PKG = "com.shopee.id"

        // VALUE
        val CASH = "CASH"
        val BANK_TYPE_OTHER = "OTHER"
        val BANK_TYPE_BANK = "BANK"

    }
}