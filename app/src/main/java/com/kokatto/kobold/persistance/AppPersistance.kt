package com.kokatto.kobold.persistance

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

object AppPersistence {
    private lateinit var _private : SharedPreferences

    fun init(context: Context) {
        _private = context.getSharedPreferences("private", Context.MODE_PRIVATE)
    }

    fun private() = _private
}
