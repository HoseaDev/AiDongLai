package com.hosea.aidonglai

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
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
import com.hosea.aidonglai.service.detector.StoreActivityDetector
import com.hosea.aidonglai.ui.theme.AiDongLaiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AiDongLaiTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    var isAccessibilityEnabled by remember { 
        mutableStateOf(false)
    }
    var searchText by remember { 
        mutableStateOf(StoreActivityDetector.targetText)
    }
    val context = LocalContext.current
    
    LaunchedEffect(Unit) {
        isAccessibilityEnabled = AutoClickService.isServiceEnabled(context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 软件简介卡片
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "软件简介",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Text(text = "作者：Hosea")
                Text(text = "GitHub：https://github.com/HoseaDev")
                Text(text = "绿色：HoseaDev")
                Text(
                    text = "免责声明：本软件完全开源免费，仅供学习使用，请不要用于任何商业用途。" +
                    "使用本软件所产生的任何法律责任由使用者自行承担。",
                    color = Color.Red
                )
            }
        }

        // 使用说明卡片
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "使用说明：",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Text(text = "1. 打开无障碍权限，出现黑框表示开启成功")
                Text(text = "2. 进入某音 -> 我 -> 商城 -> 搜索")
                Text(text = "3. 输入（只卖真货的店名）")
                Text(text = "4. 切换到店铺栏")
                Text(text = "5. 进入店铺即可完成")
                Text(text = "6. 本软件设定只能抢{油}，且商家限定每人只能抢一次，所以你也只能抢一次。")
            }
        }

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
                    Button(
                        onClick = {
                            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                            context.startActivity(intent)
                        }
                    ) {
                        Text(text = if (isAccessibilityEnabled) "已开启" else "去开启")
                    }
                }
            }
        }
    }
}