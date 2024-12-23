package com.iilhamwd.libhc

import android.app.Activity
import android.os.Handler
import android.util.Log
import android.view.ActionMode
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.SearchEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import com.dawn.decoderapijni.ServiceTools
import com.dawn.decoderapijni.SoftEngine
import com.xlzn.hcpda.uhf.UHFReader
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result


/** LibhcPlugin */
class LibhcPlugin : FlutterPlugin, MethodCallHandler, ActivityAware {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private lateinit var eventChannel: EventChannel
    private lateinit var channel: MethodChannel
    private lateinit var context: Activity

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "libhc")
        eventChannel = EventChannel(flutterPluginBinding.binaryMessenger, "libhc_stream")
        channel.setMethodCallHandler(this)
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        when (call.method) {
            "init" -> {
                ServiceTools.getInstance()
                    .startInit(context.cacheDir.path, false, Handler() { _ -> true })
                val init = UHFReader.getInstance().connect(context)

                if (init.resultCode != 0) {
                    result.error(init.resultCode.toString(), init.message, init.data)
                }

                result.success(0)
            }

            "startInventoryReader" -> {
                result.success(UHFReader.getInstance().startInventory().data)
            }

            "stopInventoryReader" -> {
                result.success(UHFReader.getInstance().stopInventory().data)
            }

            "startQrScanner" -> {
                ServiceTools.getInstance().startScan()
                result.success(0)
            }

            "stopQrScanner" -> {
                ServiceTools.getInstance().stopScan()
                result.success(0)
            }
        }
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
        UHFReader.getInstance().disConnect()
    }

    companion object {
        const val TAG = "LibHC"
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        context = binding.activity

        eventChannel.setStreamHandler(object : EventChannel.StreamHandler {
            override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
                if (events == null) return

                SoftEngine.getInstance().setScanningCallback(object : SoftEngine.ScanningCallback {
                    override fun onScanningCallback(
                        i: Int,
                        i2: Int,
                        bytes: ByteArray?,
                        length: Int
                    ): Int {
                        if (bytes == null) return 0

                        context.runOnUiThread {
                            events.success(
                                mapOf(
                                    "type" to "qr_code_result",
                                    "data" to String(bytes, 128, length - 128)
                                )
                            )
                        }

                        return 0
                    }
                })

                context.window.callback = object : Window.Callback by context.window.callback {
                    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
                        if (event?.keyCode != 290) return true

                        events.success(
                            mapOf(
                                "type" to "physical_button_state",
                                "data" to event.action
                            )
                        )

                        return true
                    }
                }

                UHFReader.getInstance().setOnInventoryDataListener {
                    context.runOnUiThread {

                        events.success(
                            mapOf(
                                "type" to "rfid_results",
                                "data" to it.map {
                                    mapOf(
                                        "ecp" to it.ecpHex, "rssi" to it.rssi
                                    )
                                }.toList()
                            )
                        )
                    }
                }
            }

            override fun onCancel(arguments: Any?) {
                UHFReader.getInstance().stopInventory()
            }
        })
    }

    override fun onDetachedFromActivityForConfigChanges() {
        Log.d(TAG, "Detached from activity")
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        context = binding.activity
    }

    override fun onDetachedFromActivity() {
        Log.d(TAG, "Detached from activity")
    }
}
