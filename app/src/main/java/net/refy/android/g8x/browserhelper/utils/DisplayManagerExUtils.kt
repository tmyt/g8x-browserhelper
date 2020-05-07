package net.refy.android.g8x.browserhelper.utils

import android.os.IBinder
import tech.onsen.reflect.Reflect

class DisplayManagerExUtils : Reflect() {
    companion object{
        const val STATE_DISABLED = 2
        const val STATE_ENABLED = 3
        const val STATE_UNMOUNT = 1
    }

    private class ServiceManager : Reflect("android.os.ServiceManager") {
        override val value = null
        val getService by static<Any>(String::class.java)
    }

    private class IDisplayManager_Stub : Reflect("android.hardware.display.IDisplayManagerEx\$Stub") {
        override val value = null
        val asInterface by static<Any>(IBinder::class.java)
    }

    private val serviceManager = ServiceManager()
    private val displayManagerStub = IDisplayManager_Stub()

    override val type: Class<*> by lazy { value.javaClass }
    override val value by lazy { displayManagerStub.asInterface(serviceManager.getService("display")) }

    val getCoverDisplayState by virtual<Int>()

    fun isCoverEnabled() = getCoverDisplayState() == STATE_ENABLED
}