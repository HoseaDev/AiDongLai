package com.hosea.aidonglai.service.detector

import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.compose.ui.geometry.Rect
import com.hosea.aidonglai.service.AutoClickService
import com.hosea.aidonglai.service.utils.AccessibilityUtil

class OrderDetector : IWindowDetector {
    lateinit var service: AutoClickService

    companion object {
        private const val TARGET_ACTIVITY =
            "com.bytedance.android.shopping.buynow.container.BuyNowFullscreenDialog"
    }

    var TAG = "OrderDetector"

    override fun matchWindow(event: AccessibilityEvent): Boolean {
        return event.className?.toString() == TARGET_ACTIVITY
    }

    override fun handleEvent(rootNode: AccessibilityNodeInfo, event: AccessibilityEvent) {
        // 立即支付
        val order = AccessibilityUtil.findNodeByAttributes(
            rootNode, attributes = mapOf(
                "class" to "android.widget.Button",
                "content-desc" to "立即支付",
                "clickable" to false,
                "enabled" to true,
                "focusable" to true,
                "package" to "com.ss.android.ugc.aweme"
            )
        )
        Log.i(TAG, "order：${order}")
        order?.let {
            Log.i(TAG, "点击order")
            val rect = android.graphics.Rect()
            order.getBoundsInScreen(rect)
            Log.i(TAG, "rect:${rect}")
            for (i in 0..3) {
                AccessibilityUtil.clickOnBounds(service, rect)
                Thread.sleep(100)
            }
        }
    }
}
