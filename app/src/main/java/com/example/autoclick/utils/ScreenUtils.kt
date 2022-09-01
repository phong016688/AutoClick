package com.example.autoclick.utils

import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.view.Display
import android.view.WindowManager
import androidx.core.hardware.display.DisplayManagerCompat


fun Context.getDisplaySize(): Pair<Int, Int> {
    val width: Int
    val height: Int
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val displayManager = DisplayManagerCompat.getInstance(this)
        val defaultDisplay = displayManager.getDisplay(Display.DEFAULT_DISPLAY)
        val displayContext = this.createDisplayContext(defaultDisplay!!)
        width = displayContext.resources.displayMetrics.widthPixels
        height = displayContext.resources.displayMetrics.heightPixels
    } else {
        val displayMetrics = DisplayMetrics()
        val windowManager = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        @Suppress("DEPRECATION")
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        height = displayMetrics.heightPixels
        width = displayMetrics.widthPixels
    }
    return width to height
}

fun Context.getBottomNavigationHeight(): Int {
    val resources = this.resources
    val resourceId: Int = resources.getIdentifier("navigation_bar_height", "dimen", "android")
    return if (resourceId > 0) {
        resources.getDimensionPixelSize(resourceId)
    } else 0
}

fun Context.getStatusBarHeight(): Int {
    val resourceId: Int = resources.getIdentifier("status_bar_height", "dimen", "android")
    return if (resourceId > 0) {
        resources.getDimensionPixelSize(resourceId)
    } else 0
}