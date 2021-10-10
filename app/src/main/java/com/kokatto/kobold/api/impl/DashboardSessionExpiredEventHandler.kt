package com.kokatto.kobold.api.impl

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.kokatto.kobold.api.internal.SessionExpiredEventHandler
import com.kokatto.kobold.login.LoginActivity

class DashboardSessionExpiredEventHandler(private val context: Context) : SessionExpiredEventHandler {
    override fun onSessionExpired() {
        Handler(Looper.getMainLooper()).post {
            // todo navigate to sign in
            val intent = Intent(context, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }
}
