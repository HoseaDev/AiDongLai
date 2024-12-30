package com.hosea.aidonglai.service

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.hosea.aidonglai.service.detector.IWindowDetector
import com.hosea.aidonglai.service.detector.ProductActivityDetector
import com.hosea.aidonglai.service.detector.SearchActivityDetector
import com.hosea.aidonglai.service.detector.StoreActivityDetector

class AutoClickService : AccessibilityService() {
    companion object {
        private const val TAG = "AutoClickService"
    }

    private val detectors = listOf<IWindowDetector>(
        SearchActivityDetector(),
        StoreActivityDetector(),
        ProductActivityDetector()
    )

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.i(TAG, "Service connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        Log.d(TAG, "Received event: ${event.eventType}, class: ${event.className}")
        
        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            return
        }

        val rootNode = rootInActiveWindow
        if (rootNode == null) {
            Log.w(TAG, "Root node is null")
            return
        }
        
        detectors.find { it.matchWindow(event) }?.let { detector ->
            Log.i(TAG, "Matched detector: ${detector.javaClass.simpleName}")
            detector.handleEvent(rootNode, event)
        }
    }

    override fun onInterrupt() {
        Log.w(TAG, "Service interrupted")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "Service destroyed")
    }
}
