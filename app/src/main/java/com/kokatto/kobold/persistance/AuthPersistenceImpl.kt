package com.kokatto.kobold.persistance

import com.kokatto.kobold.extension.get
import com.kokatto.kobold.extension.set
import com.kokatto.kobold.provider.SharedPreferencesProvider

class AuthPersistenceImpl(private val persistence: SharedPreferencesProvider) : AuthPersistence {

    companion object {
        private const val ACCESS_TOKEN = "accessToken"
        private const val EXPIRES_IN = "expiresIn"
        private const val FIREBASE_TOKEN = "firebaseToken"
    }

    override var accessToken: String?
        get() = persistence.private[ACCESS_TOKEN]
        set(value) {
            persistence.private[ACCESS_TOKEN] = value
        }

    override var expiresIn: Long?
        get() = persistence.private[EXPIRES_IN]
        set(value) {
            persistence.private[EXPIRES_IN] = value
        }
    override var firebaseToken: String?
        get() = persistence.private[FIREBASE_TOKEN]
        set(value) {
            persistence.private[FIREBASE_TOKEN] = value
        }

    override fun clear() {
        persistence.private[ACCESS_TOKEN] = null
    }
}
