package com.hosea.aidonglai.service.utils

import android.view.accessibility.AccessibilityNodeInfo

object AccessibilityUtil {
    fun findNodeByIdAndText(rootNode: AccessibilityNodeInfo?, id: String, text: String): AccessibilityNodeInfo? {
        rootNode ?: return null
        val nodes = rootNode.findAccessibilityNodeInfosByViewId(id)
        return nodes.find { it.text?.toString() == text }
    }

    fun findNodeByText(rootNode: AccessibilityNodeInfo?, text: String): AccessibilityNodeInfo? {
        rootNode ?: return null
        val nodes = rootNode.findAccessibilityNodeInfosByText(text)
        return nodes.firstOrNull()
    }

    fun performClick(node: AccessibilityNodeInfo) {
        if (node.isClickable) {
            node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        } else {
            var parent = node.parent
            while (parent != null) {
                if (parent.isClickable) {
                    parent.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    break
                }
                parent = parent.parent
            }
        }
    }


}
