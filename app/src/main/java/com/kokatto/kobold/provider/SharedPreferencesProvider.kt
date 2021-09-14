package com.kokatto.kobold.provider

interface SharedPreferencesProvider {
    val private: android.content.SharedPreferences
    val public: android.content.SharedPreferences
}
