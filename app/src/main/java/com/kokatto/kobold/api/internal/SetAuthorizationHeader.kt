package com.kokatto.kobold.api.internal

import androidx.annotation.CheckResult
import com.kokatto.kobold.persistance.AppPersistence
import okhttp3.Request

internal class SetAuthorizationHeader() {

    @CheckResult
    operator fun invoke(request: Request): Request {
        val token = AppPersistence.token
        return request.newBuilder()
            .header(AUTHORIZATION_KEY, "Bearer $token")
            .build()
    }


    fun isSame(request: Request): Boolean =
        invoke(request).header(AUTHORIZATION_KEY) == request.header(AUTHORIZATION_KEY)

    companion object {
        private const val AUTHORIZATION_KEY = "Authorization"
    }
}
