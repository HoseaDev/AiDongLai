package com.hosea.aidonglai

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hosea.aidonglai.service.AutoClickService
import com.hosea.aidonglai.ui.theme.AiDongLaiTheme

class MainActivity : ComponentActivity() {
    private lateinit var accessibilityManager: AccessibilityManager
    private var accessibilityCallback: AccessibilityManager.AccessibilityStateChangeListener? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        accessibilityManager = getSystemService(ACCESSIBILITY_SERVICE) as AccessibilityManager
        accessibilityCallback = AccessibilityManager.AccessibilityStateChangeListener { 
            updateAccessibilityState()
        }
        accessibilityManager.addAccessibilityStateChangeListener(accessibilityCallback!!)
        
        setContent {
            AiDongLaiTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(AutoClickService.isServiceEnabled(this))
                }
            }
        }
    }
    
    private fun updateAccessibilityState() {
        setContent {
            AiDongLaiTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(AutoClickService.isServiceEnabled(this))
                }
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        accessibilityCallback?.let {
            accessibilityManager.removeAccessibilityStateChangeListener(it)
        }
    }
}

@Composable
fun MainScreen(initialAccessibilityState: Boolean) {
    val context = LocalContext.current
    var isAccessibilityEnabled by remember { 
        mutableStateOf(initialAccessibilityState)
    }
    
    // 监听无障碍服务状态变化
    DisposableEffect(Unit) {
        val accessibilityManager = context.getSystemService(ComponentActivity.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val callback = AccessibilityManager.AccessibilityStateChangeListener { 
            isAccessibilityEnabled = AutoClickService.isServiceEnabled(context)
        }
        
        accessibilityManager.addAccessibilityStateChangeListener(callback)
        
        onDispose {
            accessibilityManager.removeAccessibilityStateChangeListener(callback)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 无障碍服务状态卡片
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "无障碍服务状态",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = if (isAccessibilityEnabled) 
                                Icons.Default.CheckCircle 
                            else 
                                Icons.Default.Close,
                            contentDescription = "状态",
                            tint = if (isAccessibilityEnabled) Color.Green else Color.Red
                        )
                        Text(
                            text = if (isAccessibilityEnabled) "已开启" else "未开启",
                            color = if (isAccessibilityEnabled) Color.Green else Color.Red
                        )
                    }
                    
                    Button(
                        onClick = {
                            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                            context.startActivity(intent)
                        },
                        enabled = !isAccessibilityEnabled
                    ) {
                        Text(text = if (isAccessibilityEnabled) "已开启" else "去开启")
                    }
                }
            }
        }

        // 关于信息卡片
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "关于",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Text(text = "开发者：Hosea")
                Text(text = "本软件完全免费开源")
                Text(text = "GitHub: https://github.com/hosea")
            }
        }
    }
}