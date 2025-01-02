package com.hosea.aidonglai.service.detector

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.hosea.aidonglai.service.AutoClickService
import com.hosea.aidonglai.service.utils.AccessibilityUtil

class StoreActivityDetector : IWindowDetector {
    companion object {
        private const val TARGET_ACTIVITY =
            "com.bytedance.android.shopping.store.tabkit.container.TabKitActivity"
        private const val TARGET_ID = "com.ss.android.ugc.aweme:id/ypb"
        var targetText: String = "油"
    }

    var TAG = "StoreActivityDetector"
    private val handler = Handler(Looper.getMainLooper())

    lateinit var service: AutoClickService

    override fun matchWindow(event: AccessibilityEvent): Boolean {
        return event.className?.toString() == TARGET_ACTIVITY
    }

    override fun handleEvent(rootNode: AccessibilityNodeInfo, event: AccessibilityEvent) {
        Log.i(TAG, "event:${event}")
        Log.i(TAG, "当前查找的商品关键词: $targetText")
        
        // 打印根节点信息
        Log.i(TAG, "根节点包名: ${rootNode.packageName}, 类名: ${rootNode.className}")
        
        // 先延时等待内容加载
        Thread.sleep(500)  // 500毫秒延时
        
        val targetNode = AccessibilityUtil.findNodeByIdAndContainText(rootNode, TARGET_ID, targetText)

        if (targetNode != null) {
            Log.i(TAG, "有想要的商品: ${targetNode.text}")
            AccessibilityUtil.performClick(targetNode)
        } else {
            // 如果第一次没找到，再等待500ms再试一次
            Thread.sleep(500)
            val secondTryNode = AccessibilityUtil.findNodeByIdAndContainText(rootNode, TARGET_ID, targetText)
            
            if (secondTryNode != null) {
                Log.i(TAG, "第二次尝试找到商品: ${secondTryNode.text}")
                AccessibilityUtil.performClick(secondTryNode)
                return
            }
            
            Log.i(TAG, "没有想要的商品")
            
            // 最后尝试直接通过文本查找
            val textNode = AccessibilityUtil.findNodeByText(rootNode, targetText)
            if (textNode != null) {
                Log.i(TAG, "通过文本找到商品: ${textNode.text}")
                AccessibilityUtil.performClick(textNode)
                return
            }

            handler.postDelayed({
                service.let { AccessibilityUtil.performBack(it) }
            }, 1000)
        }
    }
}
