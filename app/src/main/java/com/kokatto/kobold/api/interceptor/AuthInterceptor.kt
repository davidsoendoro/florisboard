package com.kokatto.kobold.api.interceptor

import com.kokatto.kobold.api.annotation.RequiredAuth
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Invocation

internal class AuthInterceptor :
    Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val invocation = request.tag(Invocation::class.java)
        val requiredAuth = invocation?.method()?.getAnnotation(RequiredAuth::class.java)
//        val token = AppPersistence.token
        val token =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhY2NvdW50IjoiNjE2MDJjY2IwOWEyNDcwMDA4ZGNjMWRkIiwiaWF0IjoxNjMzOTMwOTkxLCJleHAiOjE2OTQ0MTA5OTF9.LcV7_KzNOZs8dBpn4siV7te04BwFpdGhqlCmQ-mSdbs"
        return when {
            requiredAuth != null -> when {
                token.isNotEmpty() -> chain.proceed(
                    request.newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                )

                else -> Response.Builder()
                    .code(404)
                    .protocol(Protocol.HTTP_2)
                    .body("".toResponseBody())
                    .message("Client token is null")
                    .request(request)
                    .build()
            }
            else -> chain.proceed(request)
        }
    }
}
