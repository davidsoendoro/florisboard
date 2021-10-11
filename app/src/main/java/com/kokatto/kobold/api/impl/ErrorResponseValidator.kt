package com.kokatto.kobold.api.impl

class ErrorResponseValidator {

    companion object {

    fun isSessionExpiredResponse(errorString: String): Boolean =
        errorString.contains("expired token", ignoreCase = true) ||
            errorString.contains("token is null",ignoreCase = true)

    }

}
