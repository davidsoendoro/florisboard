package com.kokatto.kobold.extension

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Parcelable
import java.io.Serializable


inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
    val editor = this.edit()
    operation(editor)
    editor.apply()
}

operator fun SharedPreferences.set(key: String, value: Any?) {
    when (value) {
        is String? -> edit { it.putString(key, value) }
        is Int -> edit { it.putInt(key, value) }
        is Boolean -> edit { it.putBoolean(key, value) }
        is Float -> edit { it.putFloat(key, value) }
        is Long -> edit { it.putLong(key, value) }
        else -> throw UnsupportedOperationException("Not yet implemented")
    }
}

inline operator fun <reified T : Any> SharedPreferences.get(
    key: String,
    defaultValue: T? = null
): T? {
    return when (T::class) {
        String::class -> getString(key, defaultValue as? String) as T?
        Int::class -> getInt(key, defaultValue as? Int ?: -1) as T?
        Boolean::class -> getBoolean(key, defaultValue as? Boolean ?: false) as T?
        Float::class -> getFloat(key, defaultValue as? Float ?: -1f) as T?
        Long::class -> getLong(key, defaultValue as? Long ?: -1) as T?
        CharSequence::class -> getString(key,  defaultValue as? String) as T?
        else -> throw UnsupportedOperationException("Not yet implemented")
    }
}

operator fun Bundle.set(key: String, value: Any?) {
    if (this.get(key) == null && value == null) {
        return
    }

    when (value) {
        is String -> this.putString(key, value)
        is String? -> this.putString(key, value)
        is Char -> this.putChar(key, value)
        is CharSequence -> this.putCharSequence(key, value)
        is CharSequence? -> this.putCharSequence(key, value)

        is Byte -> this.putByte(key, value)
        is Short -> this.putShort(key, value)
        is Int -> this.putInt(key, value)

        is Boolean -> this.putBoolean(key, value)
        is Double -> this.putDouble(key, value)

        is Float -> this.putFloat(key, value)

        is Long -> this.putLong(key, value)

        is Bundle -> this.putBundle(key, value)
        is Bundle? -> this.putBundle(key, value)

        is Parcelable -> this.putParcelable(key, value)
        is Parcelable? -> this.putParcelable(key, value)

        is Serializable -> this.putSerializable(key, value)
        is Serializable? -> this.putSerializable(key, value)

        else -> throw UnsupportedOperationException("Class outside boxed primitive types are not supported, Please implement Parcelable or Serializable to this class ${value?.javaClass?.name}")
    }
}

inline operator fun <reified T : Any> Bundle.get(
    key: String,
    defaultValue: T? = null
): T? {
    return when (T::class) {
        String::class -> getString(key, defaultValue as? String) as T?
        Int::class -> getInt(key, defaultValue as? Int ?: -1) as T?
        Boolean::class -> getBoolean(key, defaultValue as? Boolean ?: false) as T?
        Float::class -> getFloat(key, defaultValue as? Float ?: -1f) as T?
        Long::class -> getLong(key, defaultValue as? Long ?: -1) as T?
        Byte::class -> getByte(key, defaultValue as? Byte ?: 0) as T?
        Bundle::class -> getBundle(key) as T?
        else -> throw UnsupportedOperationException("Not yet implemented")
    }
}
