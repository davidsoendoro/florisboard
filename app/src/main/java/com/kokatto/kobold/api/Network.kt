package com.kokatto.kobold.api

import com.google.gson.Gson
import com.kokatto.kobold.api.interceptor.AuthInterceptor
import com.kokatto.kobold.api.interceptor.TokenAuthenticator
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object Network {
    private val okHttpClient = OkHttpClient.Builder()
        .authenticator(TokenAuthenticator())
        .addInterceptor(AuthInterceptor())
        .connectTimeout(1, TimeUnit.MINUTES)
        .readTimeout(1, TimeUnit.MINUTES)
        .writeTimeout(1, TimeUnit.MINUTES)

    val authenticationApi: AuthenticationApi by lazy {
        callNetworkIdentity(okHttpClient, Gson())
    }

    val chatTemplateApi: ChatTemplateApi by lazy {
        createNetwork(okHttpClient, Gson())
    }

    val transactionApi: TransactionApi by lazy {
        createNetwork(okHttpClient, Gson())
    }

    val bankApi: BankApi by lazy {
        createNetwork(okHttpClient, Gson())
    }

    val deliveryApi: DeliveryApi by lazy {
        createNetwork(okHttpClient, Gson())
    }

    val merchantApi: MerchantApi by lazy {
        createNetwork(okHttpClient, Gson())
    }

    val refreshTokenApi: RefreshTokenApi by lazy {
        callNetworkRefreshToken()
    }
}
