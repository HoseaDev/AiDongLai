package com.hosea.aidonglai.service.detector

import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.hosea.aidonglai.service.utils.AccessibilityUtil

class StoreActivityDetector : IWindowDetector {
    companion object {
        private const val TARGET_ACTIVITY = "com.bytedance.android.shopping.store.tabkit.container.TabKitActivity"
        private const val TARGET_ID = "com.ss.android.ugc.aweme:id/ypb"
        private const val TARGET_TEXT = "花生油"
    }

    override fun matchWindow(event: AccessibilityEvent): Boolean {
        return event.className?.toString() == TARGET_ACTIVITY
    }

    override fun handleEvent(rootNode: AccessibilityNodeInfo, event: AccessibilityEvent) {
        val targetNode = AccessibilityUtil.findNodeByIdAndText(rootNode, TARGET_ID, TARGET_TEXT)
        if (targetNode==null){}
        targetNode?.let {
            AccessibilityUtil.performClick(it)
        }
    }
}
