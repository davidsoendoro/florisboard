package com.kokatto.kobold.component

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kokatto.kobold.R

open class DashboardThemeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.DashboardThemeBase)
    }

}
