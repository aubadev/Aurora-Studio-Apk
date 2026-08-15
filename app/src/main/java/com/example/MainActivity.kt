package com.example

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val systemDark = isSystemInDarkTheme()
            MyApplicationTheme(darkTheme = systemDark) {
                AppScreen(isDarkMode = systemDark)
            }
        }
    }
}

enum class Tab(val title: String, val icon: ImageVector) {
    Home("Home", Icons.Default.Home),
    Info("Info", Icons.Default.Info)
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun AppScreen(isDarkMode: Boolean) {
    var currentTab by remember { mutableStateOf(Tab.Home) }
    var pageLoaded by remember { mutableStateOf(false) }
    var minSplashTimePassed by remember { mutableStateOf(false) }
    var webView: WebView? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        delay(2000)
        minSplashTimePassed = true
    }

    LaunchedEffect(isDarkMode) {
        val mode = if (isDarkMode) "dark" else "light"
        val js = """
            try {
                document.documentElement.setAttribute('data-theme', '$mode');
                document.documentElement.style.colorScheme = '$mode';
            } catch(e) {}
        """.trimIndent()
        webView?.evaluateJavascript(js, null)
    }

    val showSplash = !minSplashTimePassed || !pageLoaded

    BackHandler(enabled = currentTab == Tab.Home && webView?.canGoBack() == true) {
        if (currentTab == Tab.Home) {
            webView?.goBack()
        }
    }

    BackHandler(enabled = currentTab != Tab.Home) {
        currentTab = Tab.Home
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                Tab.values().forEach { tab ->
                    NavigationBarItem(
                        icon = { Icon(tab.icon, contentDescription = tab.title) },
                        label = { Text(tab.title) },
                        selected = currentTab == tab,
                        onClick = { currentTab = tab }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    WebView(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.useWideViewPort = true
                        settings.loadWithOverviewMode = true
                        setBackgroundColor(android.graphics.Color.TRANSPARENT)

                        webViewClient = object : WebViewClient() {
                            override fun shouldOverrideUrlLoading(
                                view: WebView?,
                                request: WebResourceRequest?
                            ): Boolean {
                                return false
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                pageLoaded = true
                                val mode = if (isDarkMode) "dark" else "light"
                                val js = "try { document.documentElement.setAttribute('data-theme', '$mode'); document.documentElement.style.colorScheme = '$mode'; } catch(e) {}"
                                view?.evaluateJavascript(js, null)
                            }
                        }
                        webChromeClient = WebChromeClient()

                        loadUrl("https://aubadev.github.io/Aurora-Studio/")
                        webView = this
                    }
                },
                update = {
                    webView = it
                }
            )

            if (currentTab == Tab.Info) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = R.drawable.aurora_custom_logo),
                            contentDescription = "Aurora Studio Logo",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Aurora Studio", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Version 1.0.0", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Xiaomi MTZ Theme Editor", textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onBackground)
                        Spacer(modifier = Modifier.height(32.dp))
                        Text("created by AuBa.DeV with ❤️", textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            AnimatedVisibility(
                visible = showSplash,
                exit = fadeOut(animationSpec = tween(800))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF121212)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.aurora_custom_logo),
                        contentDescription = "Aurora Studio Logo",
                        modifier = Modifier
                            .size(160.dp)
                            .clip(CircleShape)
                    )
                }
            }
        }
    }
}
