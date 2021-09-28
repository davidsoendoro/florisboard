package com.kokatto.kobold.extension

fun Int.addAffixToString(prefix: String = "", suffix: String = ""): String {
    return prefix + this.toString() + suffix
}
