package net.refy.android.g8x.browserhelper.utils

import android.content.Context
import tech.onsen.reflect.Reflect

class DisplayHelperUtils(context: Context) : Reflect("com.lge.display.DisplayManagerHelper") {
    override val value = ctor(Context::class.java)(context)
    val getCoverDisplayId by virtual<Int>()
}