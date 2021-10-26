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

    var refreshToken: String
        get() = thePrivate["refreshToken"] ?: ""
        set(value) {
            thePrivate["refreshToken"] = value
        }
    var showContactUpdateMessage: Boolean
        get() = thePrivate["hideContactUpdateMessage"] ?: true
        set(value) {
            thePrivate["hideContactUpdateMessage"] = value
        }

    fun init(context: Application) {
        thePrivate = context.getSharedPreferences("private", Context.MODE_PRIVATE)
    }

    fun private() = thePrivate

    fun clear() {
        thePrivate["refreshToken"] = null
        thePrivate["applicationToken"] = null
    }
}
