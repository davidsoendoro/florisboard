package com.kokatto.kobold.persistance

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.kokatto.kobold.extension.get
import com.kokatto.kobold.extension.set

object AppPersistence {
    private lateinit var _private: SharedPreferences
    var test: String
        get() = _private["test"] ?: ""
        set(value) {
            _private["test"] = value
        }

    var token: String
        get() = _private["applicationToken"] ?: ""
        set(value) {
            _private["applicationToken"] = value
        }

    fun init(context: Application) {
        _private = context.getSharedPreferences("private", Context.MODE_PRIVATE)
    }

    fun private() = _private
}
