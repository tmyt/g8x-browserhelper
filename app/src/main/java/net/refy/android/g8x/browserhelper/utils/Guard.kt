package net.refy.android.g8x.browserhelper.utils

import java.lang.RuntimeException

inline fun <T> guard(value: T?, ifNull: () -> Unit): T{
    if(value != null) return value
    ifNull()
    throw RuntimeException("Value is Null")
}