package net.refy.android.g8x.browserhelper.utils

import android.os.RemoteException

inline fun <reified T> call(callRemote: ()->T?): T{
    return try{
        callRemote() ?: T::class.java.newInstance()
    }catch(e: RemoteException){
        T::class.java.newInstance()
    }
}