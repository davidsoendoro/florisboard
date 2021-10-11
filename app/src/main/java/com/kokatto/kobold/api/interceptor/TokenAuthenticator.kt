package com.kokatto.kobold.api.interceptor

import com.kokatto.kobold.api.Network
import com.kokatto.kobold.api.internal.DefaultIsSessionExpiredException
import com.kokatto.kobold.api.internal.INVALIDATION_AFTER_REFRESH_HEADER_NAME
import com.kokatto.kobold.api.internal.INVALIDATION_AFTER_REFRESH_HEADER_VALUE
import com.kokatto.kobold.api.internal.IsSessionExpiredException
import com.kokatto.kobold.api.internal.SetAuthorizationHeader
import com.kokatto.kobold.api.internal.authFinishedInvalidationException
import com.kokatto.kobold.api.model.request.PostTokenRefreshRequest
import com.kokatto.kobold.api.model.response.PostTokenRefreshResponse
import com.kokatto.kobold.persistance.AppPersistence
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.HttpException
import java.io.IOException

internal class TokenAuthenticator : Authenticator {

    private var isSessionExpiredException: IsSessionExpiredException = DefaultIsSessionExpiredException()
    private var setAuthorizationHeader: SetAuthorizationHeader = SetAuthorizationHeader()

    @Throws(IOException::class)
    override fun authenticate(route: Route?, response: Response): Request? {
        synchronized(this) {

            if (!setAuthorizationHeader.isSame(response.request)) {
                return setAuthorizationHeader(response.request)
            } else if (AppPersistence.refreshToken.isEmpty()) {
                return null
            }

            repeat(REFRESH_TOKEN_RETRY_COUNT) {
                try {
                    val refreshTokenResponse =
                        Network.refreshTokenApi.refreshToken(PostTokenRefreshRequest(AppPersistence.refreshToken))
                            .execute()
                    val tokenRefreshResponse: PostTokenRefreshResponse? = refreshTokenResponse.body()
                    if (refreshTokenResponse.isSuccessful && tokenRefreshResponse != null) {

                        AppPersistence.token = tokenRefreshResponse.data.token
                        AppPersistence.refreshToken = tokenRefreshResponse.data.refreshToken

                        // throw exception since the header is present
                        if (response.request.header(INVALIDATION_AFTER_REFRESH_HEADER_NAME) == INVALIDATION_AFTER_REFRESH_HEADER_VALUE) {
                            throw authFinishedInvalidationException
                        }

                        // retry request with the new tokens
                        return setAuthorizationHeader(response.request)

                    } else {
                        throw HttpException(refreshTokenResponse)
                    }
                } catch (throwable: Throwable) {
                    when (throwable) {
                        authFinishedInvalidationException -> throw throwable
                        is HttpException -> {
                            if (isSessionExpiredException(throwable)) {
                                onSessionExpiration()
                                return null
                            }
                        }
                    }
                    throwable.printStackTrace()
                }
            }

            // return the request with 401 error since the refresh token failed 3 times.
            return null
        }
    }

    /**
     * On SessionExpiration we clear the data and report the event.
     */
    private fun onSessionExpiration() {
        AppPersistence.clear()
    }

    companion object {
        private const val REFRESH_TOKEN_RETRY_COUNT = 3
    }

}
