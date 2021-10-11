package com.kokatto.kobold.api.internal

import retrofit2.HttpException
import java.net.HttpURLConnection

/**
 * Default implementation of [IsSessionExpiredException].
 *
 * Checks the [HttpException] against to most common session expiration responses.
 */
class DefaultIsSessionExpiredException : IsSessionExpiredException {
    override fun invoke(httpException: HttpException): Boolean =
        httpException.isInvalidTokenException() || httpException.isExpiredTokenException()

    companion object {
        fun HttpException.isInvalidTokenException() =
            code() == HttpURLConnection.HTTP_BAD_REQUEST &&
                errorBodyAsString().contains("\"invalid token")

        fun HttpException.isExpiredTokenException() =
            code() == HttpURLConnection.HTTP_UNAUTHORIZED &&
                errorBodyAsString().contains("\"expired token")

        fun HttpException.errorBodyAsString() = response()?.errorBody()?.string().orEmpty()
    }
}
