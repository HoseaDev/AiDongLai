package com.hosea.aidonglai.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import androidx.core.app.NotificationCompat
import com.hosea.aidonglai.MainActivity
import com.hosea.aidonglai.R
import com.hosea.aidonglai.service.detector.IWindowDetector
import com.hosea.aidonglai.service.detector.ProductActivityDetector
import com.hosea.aidonglai.service.detector.SearchActivityDetector
import com.hosea.aidonglai.service.detector.StoreActivityDetector
import com.hosea.aidonglai.service.FloatingWindowManager
import com.hosea.aidonglai.service.detector.OrderDetector

class AutoClickService : AccessibilityService() {
    private val detectors = listOf<IWindowDetector>(
        SearchActivityDetector(),
        StoreActivityDetector().also {
            it.service = this
        },
        ProductActivityDetector(),
        OrderDetector().also {
            it.service = this
        }
    ).also {
        Log.i(
            "AutoClickService",
            "Initialized detectors: ${it.map { detector -> detector.javaClass.simpleName }}"
        )
    }

    companion object {
        private const val TAG = "AutoClickService"
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "AutoClickService"
        var instance: AutoClickService? = null
            private set

        fun isServiceEnabled(context: Context): Boolean {
            val accessibilityManager =
                context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
            val accessibilityServices = accessibilityManager
                .getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC)
            return accessibilityServices.any { it.id.contains(context.packageName) }
        }
    }

    private lateinit var floatingWindowManager: FloatingWindowManager

    override fun onCreate() {
        super.onCreate()
        instance = this
        Log.i(TAG, "Service created")
        floatingWindowManager = FloatingWindowManager(this)
        startForeground()
    }

    private fun startForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Auto Click Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Keep service running"
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Auto Click Service")
            .setContentText("Service is running")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.i(TAG, "Service connected, config loaded")
        try {
            serviceInfo = serviceInfo.also { info ->
                info?.apply {
                    // 监听所有事件类型
                    eventTypes = AccessibilityEvent.TYPES_ALL_MASK

                    flags = flags or AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS or
                            AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS or
                            AccessibilityServiceInfo.FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY or
                            AccessibilityServiceInfo.FLAG_REQUEST_FILTER_KEY_EVENTS

                    notificationTimeout = 100
                }
                Log.d(TAG, "Service info - eventTypes: ${info?.eventTypes}, flags: ${info?.flags}")
            }
            // 在服务连接后显示悬浮窗
            floatingWindowManager.show()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to configure service info", e)
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        val packageName = event.packageName?.toString() ?: "unknown"
        val className = event.className?.toString() ?: "unknown"
        val eventTime = System.currentTimeMillis()

        // 记录所有事件类型
        val eventTypeStr = when (event.eventType) {
            AccessibilityEvent.TYPE_VIEW_CLICKED -> "TYPE_VIEW_CLICKED"
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> "TYPE_WINDOW_STATE_CHANGED"
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> "TYPE_WINDOW_CONTENT_CHANGED"
            AccessibilityEvent.TYPE_VIEW_FOCUSED -> "TYPE_VIEW_FOCUSED"
            AccessibilityEvent.TYPE_VIEW_SELECTED -> "TYPE_VIEW_SELECTED"
            AccessibilityEvent.TYPE_VIEW_SCROLLED -> "TYPE_VIEW_SCROLLED"
            AccessibilityEvent.TYPE_WINDOWS_CHANGED -> "TYPE_WINDOWS_CHANGED"
            else -> "TYPE_UNKNOWN(${event.eventType})"
        }

        Log.d(
            TAG,
            "Raw event received - type: $eventTypeStr, class: $className, package: $packageName, time: $eventTime"
        )

        // 只处理我们感兴趣的包
        if (packageName == "unknown" || packageName == "android" || packageName == "com.android.systemui") {
            return
        }

        // 只在窗口状态改变或内容加载完成时处理
        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && 
            event.eventType != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            return
        }

        val rootNode = try {
            rootInActiveWindow
        } catch (e: Exception) {
            Log.e(TAG, "Error getting root node", e)
            null
        }

        if (rootNode == null) {
            Log.e(TAG, "Root node is null for package: $packageName")
            return
        }

        try {
            // 对所有事件都尝试使用检测器
            detectors.find { it.matchWindow(event) }?.let { detector ->
                Log.i(
                    TAG,
                    "Matched detector: ${detector.javaClass.simpleName} for package: $packageName"
                )
                try {
                    detector.handleEvent(rootNode, event)
                } catch (e: Exception) {
                    Log.e(
                        TAG,
                        "Error handling event with detector: ${detector.javaClass.simpleName}",
                        e
                    )
                }
            }

            event.packageName?.toString()?.let { packageName ->
                val activityName = event.className?.toString() ?: ""
                if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                    floatingWindowManager.updateInfo(packageName, activityName)
                }
            }
        } finally {
            rootNode.recycle()
        }
    }

    override fun onInterrupt() {
        Log.w(TAG, "Service interrupted")
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
        floatingWindowManager.hide()
        Log.i(TAG, "Service destroyed")
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.i(TAG, "Service unbound with intent: ${intent?.action}")
        return super.onUnbind(intent)
    }
}
