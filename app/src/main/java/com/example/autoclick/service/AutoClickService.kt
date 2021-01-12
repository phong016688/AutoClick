package com.example.autoclick.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.app.Service
import android.content.Intent
import android.graphics.Path
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent
import com.example.autoclick.MainActivity
import com.example.autoclick.bean.Event
import com.example.autoclick.logd


class AutoClickService : AccessibilityService() {

    private val events = mutableListOf<Event>()

    override fun onInterrupt() {
        // NO-OP
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        // NO-OP
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                CLICK -> {
                    val extras = it.extras ?: return@let
                    click(extras.getInt("x"), extras.getInt("y"))
                }
                MOVE -> {
                    val extras = it.extras ?: return@let
                    move(extras.getInt("x"), extras.getInt("y"))
                }
                STOP -> {
                }
            }
        }
        return Service.START_STICKY
    }


    override fun onServiceConnected() {
        super.onServiceConnected()
        "onServiceConnected".logd()
        startActivity(
            Intent(this, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    private fun move(x: Int, y: Int) {
        val path = Path()
        path.moveTo(x.toFloat(), y.toFloat())
        path.lineTo(x.toFloat(), y - 300f)
        val gestureDescription = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 10, 1000))
            .build()
        dispatchGesture(gestureDescription)
    }

    private fun click(x: Int, y: Int) {
        "click $x $y".logd("#### x/y ")
        val path = Path()
        path.moveTo(x.toFloat(), y.toFloat())
        val builder = GestureDescription.Builder()
        val gestureDescription = builder
            .addStroke(GestureDescription.StrokeDescription(path, 10, 10))
            .build()
        dispatchGesture(gestureDescription)
    }

    private fun dispatchGesture(gestureDescription: GestureDescription) {
        dispatchGesture(gestureDescription, object : AccessibilityService.GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription?) {
                gestureDescription?.logd("############### complete: ")
                super.onCompleted(gestureDescription)
            }

            override fun onCancelled(gestureDescription: GestureDescription?) {
                gestureDescription?.logd("############### onCancelled: ")
                super.onCancelled(gestureDescription)
            }
        }, Handler(Looper.getMainLooper()))
    }

    private fun run(newEvents: MutableList<Event>) {
        events.clear()
        events.addAll(newEvents)
        events.toString().logd()
        val builder = GestureDescription.Builder()
        events.forEach { builder.addStroke(it.onEvent()) }
        dispatchGesture(builder.build(), null, null)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        "AutoClickService onUnbind".logd()
        return super.onUnbind(intent)
    }


    override fun onDestroy() {
        "AutoClickService onDestroy".logd()
        super.onDestroy()
    }

    companion object {
        const val CLICK = "CLICK"
        const val MOVE = "MOVE"
        const val STOP = "STOP"
    }
}