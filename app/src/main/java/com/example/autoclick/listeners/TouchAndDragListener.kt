package com.example.autoclick.listeners

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import com.example.autoclick.utils.getBottomNavigationHeight
import com.example.autoclick.utils.getDisplaySize
import com.example.autoclick.utils.getStatusBarHeight

class TouchAndDragListener(
    private val context: Context,
    private val params: WindowManager.LayoutParams,
    private val startDragDistance: Int = 10,
    private val onClick: (() -> Unit)?,
    private val onDrag: (() -> Unit)?,
) : View.OnTouchListener {
    private var initialX: Int = 0
    private var initialY: Int = 0
    private var initialTouchX: Float = 0.toFloat()
    private var initialTouchY: Float = 0.toFloat()
    private var isDrag = false
    private val ss = context.getDisplaySize()
    private val topBar = context.getStatusBarHeight()
    private val bottomBar = context.getBottomNavigationHeight()

    private fun isDragging(event: MotionEvent): Boolean =
        ((Math.pow((event.rawX - initialTouchX).toDouble(), 2.0)
                + Math.pow((event.rawY - initialTouchY).toDouble(), 2.0))
                > startDragDistance * startDragDistance)

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                isDrag = false
                initialX = params.x
                initialY = params.y
                initialTouchX = event.rawX
                initialTouchY = event.rawY
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                if (!isDrag && isDragging(event)) {
                    isDrag = true
                }
                if (!isDrag) return true
                params.x = event.rawX.toInt() - ss.first / 2
                params.y = event.rawY.toInt() - topBar - ss.second / 2
                onDrag?.invoke()
                return true
            }

            MotionEvent.ACTION_UP -> {
                if (!isDrag) {
                    onClick?.invoke()
                    return true
                }
            }
        }
        return false
    }
}