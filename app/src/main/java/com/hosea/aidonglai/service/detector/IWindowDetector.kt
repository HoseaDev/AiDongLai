package com.hosea.aidonglai.service.detector

import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

interface IWindowDetector {
    fun matchWindow(event: AccessibilityEvent): Boolean
    fun handleEvent(rootNode: AccessibilityNodeInfo, event: AccessibilityEvent)
}
