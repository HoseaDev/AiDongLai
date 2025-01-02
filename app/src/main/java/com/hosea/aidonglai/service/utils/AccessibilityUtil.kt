package com.hosea.aidonglai.service.utils

import android.view.accessibility.AccessibilityNodeInfo
import android.accessibilityservice.AccessibilityService
import android.graphics.Path
import android.graphics.Rect

object AccessibilityUtil {
    const val GLOBAL_ACTION_BACK =
        android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_BACK

    fun findNodeByIdAndText(
        rootNode: AccessibilityNodeInfo?,
        id: String,
        text: String
    ): AccessibilityNodeInfo? {
        rootNode ?: return null
        val nodes = rootNode.findAccessibilityNodeInfosByViewId(id)
        return nodes.find { it.text?.toString() == text }
    }

    fun findNodeByIdAndContainText(
        rootNode: AccessibilityNodeInfo?,
        id: String,
        text: String
    ): AccessibilityNodeInfo? {
        rootNode ?: return null
        val nodes = rootNode.findAccessibilityNodeInfosByViewId(id)
        android.util.Log.d("AccessibilityUtil", "找到 $id 的节点数量: ${nodes.size}")
        
        nodes.forEach { node ->
            val nodeText = node.text?.toString()
            val nodeDesc = node.contentDescription?.toString()
            android.util.Log.d("AccessibilityUtil", "节点文本: $nodeText, 描述: $nodeDesc")
        }
        
        return nodes.find { node -> 
            val nodeText = node.text?.toString()
            val nodeDesc = node.contentDescription?.toString()
            nodeText?.contains(text) == true || nodeDesc?.contains(text) == true
        }
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

    fun performBack(service: AccessibilityService) {
        service.performGlobalAction(GLOBAL_ACTION_BACK)
    }

    fun findNodeByAttributes(
        rootNode: AccessibilityNodeInfo?,
        attributes: Map<String, Any>
    ): AccessibilityNodeInfo? {
        rootNode ?: return null
        
        // 使用DFS遍历所有节点
        fun searchNode(node: AccessibilityNodeInfo?): AccessibilityNodeInfo? {
            node ?: return null
            
            // 检查当前节点是否匹配所有条件
            val matches = attributes.all { (key, value) ->
                when (key) {
                    "resource-id" -> node.viewIdResourceName == value
                    "content-desc" -> node.contentDescription?.toString() == value
                    "text" -> node.text?.toString() == value
                    "class" -> node.className?.toString() == value
                    "package" -> node.packageName?.toString() == value
                    "checkable" -> node.isCheckable == value
                    "checked" -> node.isChecked == value
                    "clickable" -> node.isClickable == value
                    "enabled" -> node.isEnabled == value
                    "focusable" -> node.isFocusable == value
                    "focused" -> node.isFocused == value
                    "scrollable" -> node.isScrollable == value
                    "long-clickable" -> node.isLongClickable == value
                    "password" -> node.isPassword == value
                    "selected" -> node.isSelected == value
                    else -> true // 忽略未知属性
                }
            }

            if (matches) {
                return node
            }

            // 递归搜索子节点
            for (i in 0 until node.childCount) {
                val childNode = node.getChild(i)
                val result = searchNode(childNode)
                if (result != null) {
                    return result
                }
            }
            
            return null
        }
        
        return searchNode(rootNode)
    }

    fun clickOnScreen(service: AccessibilityService, x: Int, y: Int, duration: Long = 100L) {
        val path = Path()
        path.moveTo(x.toFloat(), y.toFloat())
        
        val builder = android.accessibilityservice.GestureDescription.Builder()
        val gestureStroke = android.accessibilityservice.GestureDescription.StrokeDescription(
            path,
            0,
            duration
        )
        builder.addStroke(gestureStroke)
        
        service.dispatchGesture(builder.build(), null, null)
    }

    fun clickOnBounds(service: AccessibilityService, bounds: Rect) {
        if (bounds.left == bounds.right && bounds.top == bounds.bottom) {
            // 如果是一个点，直接点击该点
            clickOnScreen(service, bounds.left, bounds.top)
        } else {
            // 如果是矩形，点击中心点
            val centerX = bounds.centerX()
            val centerY = bounds.centerY()
            clickOnScreen(service, centerX, centerY)
        }
    }
}
