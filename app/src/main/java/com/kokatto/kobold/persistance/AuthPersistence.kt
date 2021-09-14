package com.kokatto.kobold.persistance

interface AuthPersistence {
    var accessToken: String?
    var expiresIn: Long?
    var firebaseToken: String?
    fun clear()
}
