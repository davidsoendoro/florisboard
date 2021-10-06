package com.kokatto.kobold.persistance

interface AuthPersistence {
    var token: String?
    var refreshToken: String?
    var expiresIn: Long?
    fun clear()
}
