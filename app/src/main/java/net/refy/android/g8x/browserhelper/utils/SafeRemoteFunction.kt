package net.refy.android.g8x.browserhelper.utils

import android.os.RemoteException

inline fun <reified T> call(default: T, callRemote: () -> T?): T {
    val a: Unit
    return try {
        callRemote() ?: default
    } catch (e: RemoteException) {
        default
    }
}
