package com.kokatto.kobold.persistance

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

object AppPersistence {
    private lateinit var thePrivate : SharedPreferences

    fun init(context: Context) {
        thePrivate = context.getSharedPreferences("private", Context.MODE_PRIVATE)
    }

    fun private() = thePrivate
}
