package com.example.autoclick.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.PixelFormat.OPAQUE
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
import android.widget.TextView
import com.example.autoclick.R
import com.example.autoclick.listeners.TouchAndDragListener
import com.example.autoclick.service.manager.AppNotificationManager
import com.example.autoclick.utils.getDisplaySize
import java.util.*
import kotlin.concurrent.fixedRateTimer


class FloatingClickService : Service() {
    private lateinit var manager: WindowManager
    private lateinit var view: View
    private lateinit var params: WindowManager.LayoutParams
    private var xForRecord = 0
    private var yForRecord = 0
    private val location = IntArray(2)
    private var startDragDistance: Int = 0
    private var timer: Timer? = null
    private val appNotificationManager by lazy { AppNotificationManager(this.applicationContext) }

    override fun onCreate() {
        super.onCreate()
        startDragDistance = 100
        view = LayoutInflater.from(this).inflate(R.layout.widget, null)

        //setting the layout parameters
        val overlayParam = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }
        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            overlayParam,
            FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )


        //getting windows services and adding the floating view to it
        manager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        manager.addView(view, params)

        //adding an touchlistener to make drag movement of the floating widget
        view.setOnTouchListener(
            TouchAndDragListener(
                this,
                params,
                startDragDistance,
                { viewOnClick() },
                { manager.updateViewLayout(view, params) }
            )
        )
        Handler(Looper.getMainLooper()).postDelayed({
            startEmail()
        }, 5000)
    }

    private fun startEmail() {
        val intentClick = Intent(this, AutoClickService::class.java).apply {
            action = AutoClickService.CLICK
            putExtra("x", 750)
            putExtra("y", 1750)
        }
        startService(intentClick)
    }

    private var isOn = false
    private fun viewOnClick() {
        if (isOn) {
            timer?.cancel()
        } else {
            timer = fixedRateTimer(initialDelay = 0, period = 1000) {
                view.getLocationOnScreen(location)
                val intentClick = Intent(applicationContext, AutoClickService::class.java).apply {
                    action = AutoClickService.MOVE
                    putExtra("x", location[0] + view.right + 10)
                    putExtra("y", location[1] + view.bottom + 10)
                }
                startService(intentClick)
            }
        }
        isOn = !isOn
        view.findViewById<TextView>(R.id.onOffTextView).text = if (isOn) "ON" else "OFF"

    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
        manager.removeView(view)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val x = params.x
        val y = params.y
        params.x = xForRecord
        params.y = yForRecord
        xForRecord = x
        yForRecord = y
        manager.updateViewLayout(view, params)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForeground() {
        val notification = appNotificationManager.createNotification("...")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                AppNotificationManager.NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            startForeground(
                AppNotificationManager.NOTIFICATION_ID,
                notification
            )
        }
    }

    private fun stopForegroundAndStopSelf() {
        stopForeground(true)
        stopSelf()
    }
}