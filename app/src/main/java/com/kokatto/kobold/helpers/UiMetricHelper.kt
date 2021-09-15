package com.kokatto.kobold.helpers

import android.content.Context
import kotlin.math.roundToInt

object UiMetricHelper {
    fun dpToPx(context: Context, dp: Int): Int {
        val density: Float = context.resources
            .displayMetrics.density
        return (dp.toFloat() * density).roundToInt()
    }
}
