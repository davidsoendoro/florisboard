package com.kokatto.kobold.api.model

import Hours

data class GetRestaurantResponse(
    val address: String,
    val description: String,
    val hours: Hours,
    val id: Int,
    val logo: String,
    val name: String,
    val phone_number: String,
    val review: String,
    val type: String,
    val uid: String
)
