package com.hosea.aidonglai.service.detector

import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.hosea.aidonglai.service.utils.AccessibilityUtil

class ProductActivityDetector : IWindowDetector {
    companion object {
        private const val TARGET_ACTIVITY = "com.bytedance.android.shopping.anchorv4.containers.AnchorV4Activity"
    }

    override fun matchWindow(event: AccessibilityEvent): Boolean {
        return event.className?.toString() == TARGET_ACTIVITY
    }

    override fun handleEvent(rootNode: AccessibilityNodeInfo, event: AccessibilityEvent) {
        // 先点击立即购买
        val buyNode = AccessibilityUtil.findNodeByText(rootNode, "立即购买")
        buyNode?.let {
            AccessibilityUtil.performClick(it)
            // 等待一下确保弹窗出现
            Thread.sleep(500)
            // 再点击立即支付
            val payNode = AccessibilityUtil.findNodeByText(rootNode, "立即支付")
            payNode?.let { node ->
                AccessibilityUtil.performClick(node)
            }
        }
    }
}
