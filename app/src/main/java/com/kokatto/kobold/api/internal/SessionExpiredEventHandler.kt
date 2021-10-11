package com.kokatto.kobold.api.internal

import androidx.annotation.WorkerThread

/**
 * Callback to handle SessionExpiration which means the authentication token expired and the refreshToken can't be used to get a new one.
 * The user has to log in again.
 *
 * When this method is called the [AuthenticationLocalStorage] is already cleared.
 */
interface SessionExpiredEventHandler {

    @WorkerThread
    fun onSessionExpired()
}
