package com.example.autoclick.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.Path
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import androidx.core.app.NotificationCompat
import com.example.autoclick.R
import com.example.autoclick.view.home.HomeActivity
import com.example.autoclick.bean.Event
import com.example.autoclick.service.manager.AppNotificationManager
import com.example.autoclick.service.manager.AppNotificationManager.Companion.NOTIFICATION_ID


class AutoClickService : AccessibilityService() {
    private val TAG = "#####" + this::class.java.simpleName
    private val events = mutableListOf<Event>()

    override fun onInterrupt() {
        Log.d(TAG, "onInterrupt: ")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        Log.d(TAG, "onAccessibilityEvent: $event")
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
        Log.d(TAG, "onServiceConnected: ")
        startActivity(
            Intent(this, HomeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
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
                super.onCompleted(gestureDescription)
            }

            override fun onCancelled(gestureDescription: GestureDescription?) {
                super.onCancelled(gestureDescription)
            }
        }, Handler(Looper.getMainLooper()))
    }

    private fun run(newEvents: MutableList<Event>) {
        events.clear()
        events.addAll(newEvents)
        val builder = GestureDescription.Builder()
        events.forEach { builder.addStroke(it.onEvent()) }
        dispatchGesture(builder.build(), null, null)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }


    companion object {
        const val CLICK = "CLICK"
        const val MOVE = "MOVE"
        const val STOP = "STOP"
    }
}