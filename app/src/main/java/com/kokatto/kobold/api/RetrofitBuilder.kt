package com.kokatto.kobold.api

import com.google.gson.Gson
import com.skydoves.sandwich.coroutines.CoroutinesResponseCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val baseUrl: String = "https://xnuhystuwl.execute-api.ap-southeast-1.amazonaws.com/dev/"
private const val baseUrlIdentity: String = "https://084n8vblr6.execute-api.ap-southeast-1.amazonaws.com/dev/"
val logging = HttpLoggingInterceptor()

internal inline fun <reified T> createNetwork(
    okHttpClient: OkHttpClient.Builder,
    gson: Gson
): T {
    logging.level = HttpLoggingInterceptor.Level.BODY

    okHttpClient.addInterceptor(logging)

    val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(CoroutinesResponseCallAdapterFactory.create())

    return retrofit.client(okHttpClient.build()).build().create(T::class.java)
}

internal inline fun <reified T> callNetworkIdentity(
    okHttpClient: OkHttpClient.Builder,
    gson: Gson
): T {
    logging.level = HttpLoggingInterceptor.Level.BODY

    okHttpClient.addInterceptor(logging)

    val retrofit = Retrofit.Builder()
        .baseUrl(baseUrlIdentity)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(CoroutinesResponseCallAdapterFactory.create())

    return retrofit.client(okHttpClient.build()).build().create(T::class.java)
}
