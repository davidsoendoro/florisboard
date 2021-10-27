package com.kokatto.kobold.api.model.request

data class PageVisitTrackerRequest(
    val pageName:PageName
)

enum class PageName {
    DASHBOARD_OPEN,
    KEYBOARD_OPEN
}
