package com.hosea.aidonglai.service.detector

import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.hosea.aidonglai.service.utils.AccessibilityUtil

class ProductActivityDetector : IWindowDetector {
    companion object {
        private const val TARGET_ACTIVITY =
            "com.bytedance.android.shopping.anchorv4.containers.AnchorV4Activity"
    }

    var TAG = "ProductActivityDetector"

    override fun matchWindow(event: AccessibilityEvent): Boolean {
        return event.className?.toString() == TARGET_ACTIVITY
    }

    override fun handleEvent(rootNode: AccessibilityNodeInfo, event: AccessibilityEvent) {
        // 先点击立即购买
        Thread.sleep(100)
        val buyNode = AccessibilityUtil.findNodeByText(rootNode, "立即购买")
        Log.i(TAG, "buyNode：${buyNode}")
        buyNode?.let {
            Log.i(TAG, "点击立即购买")
            AccessibilityUtil.performClick(it)

        }
    }
}
