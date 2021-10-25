package com.kokatto.kobold.persistance

import com.kokatto.kobold.extension.get
import com.kokatto.kobold.extension.set
import com.kokatto.kobold.provider.SharedPreferencesProvider

class AuthPersistenceImpl(private val persistence: SharedPreferencesProvider) : AuthPersistence {

    companion object {
        private const val TOKEN = "token"
        private const val REFRESH_TOKEN = "refreshToken"
        private const val EXPIRES_IN = "expiresIn"
        private const val HIDE_CONTACT_UPDATE_MESSAGE = "hideContactUpdateMessage"
    }

    override var token: String?
        get() = persistence.private[TOKEN]
        set(value) {
            persistence.private[TOKEN] = value
        }

    override var refreshToken: String?
        get() = persistence.private[REFRESH_TOKEN]
        set(value) {
            persistence.private[REFRESH_TOKEN] = value
        }

    override var expiresIn: Long?
        get() = persistence.private[EXPIRES_IN]
        set(value) {
            persistence.private[EXPIRES_IN] = value
        }

    override var hideContactUpdateMessage: Boolean
        get() = persistence.private[HIDE_CONTACT_UPDATE_MESSAGE] ?: false
        set(value) {
            persistence.private[HIDE_CONTACT_UPDATE_MESSAGE] = value
        }

    override fun clear() {
        persistence.private[TOKEN] = null
        persistence.private[REFRESH_TOKEN] = null
        persistence.private[EXPIRES_IN] = null
        persistence.private[HIDE_CONTACT_UPDATE_MESSAGE] = false
    }
}
