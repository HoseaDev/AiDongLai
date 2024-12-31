package com.hosea.aidonglai.service

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.hosea.aidonglai.R

class FloatingWindowManager(private val context: Context) {
    private var windowManager: WindowManager? = null
    private var floatingView: View? = null
    private var isShowing = false

    private fun createFloatingWindow() {
        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        floatingView = LayoutInflater.from(context).inflate(R.layout.layout_floating_info, null)

        val windowType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            windowType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.END
            x = 16
            y = 100
        }

        try {
            windowManager?.addView(floatingView, params)
            isShowing = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun show() {
        if (!isShowing) {
            createFloatingWindow()
        }
    }

    fun updateInfo(packageName: String, activityName: String) {
        floatingView?.let {
            it.findViewById<TextView>(R.id.tvPackageName).text = "包名: $packageName"
            it.findViewById<TextView>(R.id.tvActivityName).text = "界面: $activityName"
        }
    }

    fun hide() {
        if (isShowing) {
            try {
                windowManager?.removeView(floatingView)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isShowing = false
                floatingView = null
                windowManager = null
            }
        }
    }
}
