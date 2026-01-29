package com.github.radlance.autodispatch

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.github.radlance.autodispatch.core.App
import com.github.radlance.autodispatch.navigation.core.DeepLinkManager
import org.koin.core.context.GlobalContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        intent?.dataString?.let { url ->
            handleDeepLink(url)
        }

        setContent {
            App()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.dataString?.let { url ->
            handleDeepLink(url)
        }
    }

    private fun handleDeepLink(url: String) {
        val deepLinkManager = GlobalContext.get().get<DeepLinkManager>()
        deepLinkManager.handleRawUrl(url)
    }
}