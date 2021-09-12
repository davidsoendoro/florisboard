package com.kokatto.kobold.api

import com.google.gson.Gson
import okhttp3.OkHttpClient

object Network {
    val clienApi: IdentityApi by lazy {
        createNetwork(OkHttpClient.Builder(), Gson())
    }
}
