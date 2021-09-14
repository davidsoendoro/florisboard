package com.kokatto.kobold.persistance

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.kokatto.kobold.extension.get
import com.kokatto.kobold.extension.set

object AppPersistence {
    private lateinit var thePrivate : SharedPreferences

    var test: String
        get() = thePrivate["test"] ?: ""
        set(value) {
            thePrivate["test"] = value
        }

    var token: String
        get() = thePrivate["applicationToken"] ?: ""
        set(value) {
            thePrivate["applicationToken"] = value
        }

    fun init(context: Application) {
        thePrivate = context.getSharedPreferences("private", Context.MODE_PRIVATE)
    }

    fun private() = thePrivate
}
