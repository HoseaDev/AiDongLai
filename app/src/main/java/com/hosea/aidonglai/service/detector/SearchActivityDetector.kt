package com.hosea.aidonglai.service.detector

import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.hosea.aidonglai.service.utils.AccessibilityUtil

class SearchActivityDetector : IWindowDetector {
    companion object {
        private const val TARGET_PACKAGE = "com.ss.android.ugc.aweme"
        private const val TARGET_ACTIVITY = "com.ss.android.ugc.aweme.search.common.activity.ECSearchActivity"
        private const val TARGET_ID = "com.ss.android.ugc.aweme:id/xpp"
        private const val TARGET_TEXT = "胖东来专营店"
    }

    override fun matchWindow(event: AccessibilityEvent): Boolean {
        val packageName = event.packageName?.toString()
        val className = event.className?.toString()
        
        // 首先匹配包名
        if (packageName != TARGET_PACKAGE) {
            return false
        }
        
        // 如果是目标Activity，直接返回true
        if (className == TARGET_ACTIVITY) {
            return true
        }
        
        // 如果不是Activity类型，也返回true以便处理其他界面元素
        return !className.isNullOrEmpty() && !className.contains("Activity")
    }

    override fun handleEvent(rootNode: AccessibilityNodeInfo, event: AccessibilityEvent) {
        val targetNode = AccessibilityUtil.findNodeByIdAndText(rootNode, TARGET_ID, TARGET_TEXT)
        targetNode?.let {
            AccessibilityUtil.performClick(it)
        }
    }
}
