package com.hosea.aidonglai.service.detector

import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.hosea.aidonglai.service.utils.AccessibilityUtil

class SearchActivityDetector : IWindowDetector {
    companion object {
        private const val TARGET_ACTIVITY = "com.ss.android.ugc.aweme.search.common.activity.ECSearchActivity"
        private const val TARGET_ID = "com.ss.android.ugc.aweme:id/xpp"
        private const val TARGET_TEXT = "胖东来专营店"
    }

    override fun matchWindow(event: AccessibilityEvent): Boolean {
        return event.className?.toString() == TARGET_ACTIVITY
    }

    override fun handleEvent(rootNode: AccessibilityNodeInfo, event: AccessibilityEvent) {
        val targetNode = AccessibilityUtil.findNodeByIdAndText(rootNode, TARGET_ID, TARGET_TEXT)
        targetNode?.let {
            AccessibilityUtil.performClick(it)
        }
    }
}
