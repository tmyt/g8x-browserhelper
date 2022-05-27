package net.refy.android.g8x.browserhelper.services

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.*
import android.support.customtabs.ICustomTabsCallback
import android.support.customtabs.ICustomTabsService
import android.util.Log
import androidx.browser.customtabs.CustomTabsCallback
import androidx.browser.customtabs.CustomTabsService
import androidx.browser.customtabs.CustomTabsSessionToken
import net.refy.android.g8x.browserhelper.utils.*

class CustomTabsConnectionService : CustomTabsService(), ServiceConnection {
    companion object {
        const val TAG = "G8X.BrowserHelper"
    }

    private var binder: ICustomTabsService? = null

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "Service.onCreate")
        // bind to target browser
        val targetPackageName = guard(lookupPackage(getPreferredPackageName())) { return }
        val serviceClass = guard(lookupServiceClass(targetPackageName)) { return }
        bindTargetService(targetPackageName, serviceClass)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "Service.onDestroy")
        unbindService(this)
    }

    override fun warmup(flags: Long) = call(false) { binder?.warmup(flags) }

    override fun requestPostMessageChannel(
        sessionToken: CustomTabsSessionToken,
        postMessageOrigin: Uri
    ) =
        call(false) { binder?.requestPostMessageChannel(sessionToken.wrap(), postMessageOrigin) }

    override fun newSession(sessionToken: CustomTabsSessionToken) =
        call(false) { binder?.newSession(sessionToken.wrap()) }

    override fun extraCommand(commandName: String, args: Bundle?) =
        call(null) { binder?.extraCommand(commandName, args) }

    override fun receiveFile(
        sessionToken: CustomTabsSessionToken,
        uri: Uri,
        purpose: Int,
        extras: Bundle?
    ) =
        call(false) { binder?.receiveFile(sessionToken.wrap(), uri, purpose, extras) }

    override fun mayLaunchUrl(
        sessionToken: CustomTabsSessionToken,
        url: Uri?,
        extras: Bundle?,
        otherLikelyBundles: MutableList<Bundle>?
    ) = call(false) { binder?.mayLaunchUrl(sessionToken.wrap(), url, extras, otherLikelyBundles) }

    override fun postMessage(
        sessionToken: CustomTabsSessionToken,
        message: String,
        extras: Bundle?
    ) =
        call(0) { binder?.postMessage(sessionToken.wrap(), message, extras) }

    override fun validateRelationship(
        sessionToken: CustomTabsSessionToken,
        relation: Int,
        origin: Uri,
        extras: Bundle?
    ) = call(false) { binder?.validateRelationship(sessionToken.wrap(), relation, origin, extras) }

    override fun updateVisuals(sessionToken: CustomTabsSessionToken, bundle: Bundle?) =
        call(false) { binder?.updateVisuals(sessionToken.wrap(), bundle) }

    override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
        Log.i(TAG, "Service.onServiceConnected($name)")
        this.binder = ICustomTabsService.Stub.asInterface(binder)
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        Log.i(TAG, "Service.onServiceDisconnected($name)")
        this.stopSelf()
    }

    private fun bindTargetService(packageName: String, serviceClass: String) {
        Log.i(TAG, "bindTo: $packageName/$serviceClass")
        val serviceIntent = Intent(CustomIntent.ACTION_CUSTOM_TABS_CONNECTION)
        serviceIntent.setClassName(packageName, serviceClass)
        bindService(serviceIntent, this, Service.BIND_AUTO_CREATE)
    }

    //
    class CustomTabsCallbackWrap(private val callback: CustomTabsCallback) :
        ICustomTabsCallback.Stub() {
        override fun onRelationshipValidationResult(
            relation: Int,
            requestedOrigin: Uri,
            result: Boolean,
            extras: Bundle?
        ) {
            callback.onRelationshipValidationResult(relation, requestedOrigin, result, extras)
        }

        override fun onNavigationEvent(navigationEvent: Int, extras: Bundle?) {
            callback.onNavigationEvent(navigationEvent, extras)
        }

        override fun extraCallback(callbackName: String, args: Bundle?) {
            callback.extraCallback(callbackName, args)
        }

        override fun onPostMessage(message: String, extras: Bundle?) {
            callback.onPostMessage(message, extras)
        }

        override fun extraCallbackWithResult(callbackName: String, args: Bundle?): Bundle {
            return callback.extraCallbackWithResult(callbackName, args)!!
        }

        override fun onMessageChannelReady(extras: Bundle?) {
            callback.onMessageChannelReady(extras)
        }
    }

    // wrap utility
    private fun CustomTabsSessionToken.wrap() =
        sessionCache[this] ?: CustomTabsCallbackWrap(this.callback!!).also {
            sessionCache[this] = it
        }

    private val sessionCache: HashMap<CustomTabsSessionToken, CustomTabsCallbackWrap> = HashMap()
}