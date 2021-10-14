package com.kokatto.kobold.extension

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.view.View

fun Context.isConnectedToInternet(): Boolean {

    val connectivity = this.applicationContext
        .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (connectivity != null) {
        val info = connectivity.allNetworkInfo
        if (info != null) for (i in info.indices) if (info[i].state == NetworkInfo.State.CONNECTED) {
            return true
        }
    }
    return false
}

fun View.isConnectedToInternet(): Boolean {

    val connectivity = this.context
        .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (connectivity != null) {
        val info = connectivity.allNetworkInfo
        if (info != null) for (i in info.indices) if (info[i].state == NetworkInfo.State.CONNECTED) {
            return true
        }
    }
    return false
}
