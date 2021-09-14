package com.kokatto.kobold.provider

import android.content.SharedPreferences

class SharedPreferencesProviderImpl(
    override val private: SharedPreferences,
    override val public: SharedPreferences
) : SharedPreferencesProvider
