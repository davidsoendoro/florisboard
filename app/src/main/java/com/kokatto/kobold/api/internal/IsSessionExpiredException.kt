package com.kokatto.kobold.api.internal

import retrofit2.HttpException

interface IsSessionExpiredException {
    operator fun invoke(httpException: HttpException): Boolean
}
