package com.kokatto.kobold

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kokatto.kobold.persistance.AppPersistence
import dev.patrickgold.florisboard.R

class MainKoboldActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_kobold)
    }
}
