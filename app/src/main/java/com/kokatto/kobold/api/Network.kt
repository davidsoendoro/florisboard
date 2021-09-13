package com.kokatto.kobold.api

import com.google.gson.Gson
import okhttp3.OkHttpClient

object Network {
    val identityApi: IdentityApi by lazy {
        createNetwork(OkHttpClient.Builder(), Gson())
    }

    val chatTemplateApi: ChatTemplateApi by lazy {
        createNetwork(OkHttpClient.Builder(), Gson())
    }
}
