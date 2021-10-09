package com.kokatto.kobold.api.internal

import androidx.annotation.CheckResult
import okhttp3.Request

internal class SetAuthorizationHeader(private val token: String) {

    @CheckResult
    operator fun invoke(request: Request): Request =
        request.newBuilder()
            .header(AUTHORIZATION_KEY, "Bearer $token")
            .build()

    fun isSame(request: Request): Boolean =
        invoke(request).header(AUTHORIZATION_KEY) == request.header(AUTHORIZATION_KEY)

    companion object {
        private const val AUTHORIZATION_KEY = "Authorization"
    }
}
