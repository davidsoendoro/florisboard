package com.kokatto.kobold.api

import android.app.Application
import com.google.gson.Gson
import com.skydoves.sandwich.coroutines.CoroutinesResponseCallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

internal inline fun <reified T> createNetwork(
    okHttpClient: OkHttpClient.Builder,
    gson: Gson
): T {
    val retrofit = Retrofit.Builder()
        .baseUrl(Test)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(CoroutinesResponseCallAdapterFactory.create())

    return retrofit.client(okHttpClient.build()).build().create(T::class.java)
}
