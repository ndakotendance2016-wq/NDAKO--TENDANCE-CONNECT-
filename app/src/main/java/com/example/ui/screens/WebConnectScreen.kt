package com.example.ui.screens

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.NoirProfond

@Composable
fun WebConnectScreen() {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(NoirProfond)
  ) {
    AndroidView(
      modifier = Modifier.fillMaxSize(),
      factory = { context ->
        WebView(context).apply {
          // Standard basic configurations to run local static single-page webapps securely
          webViewClient = WebViewClient()
          settings.javaScriptEnabled = true
          settings.domStorageEnabled = true
          settings.useWideViewPort = true
          settings.loadWithOverviewMode = true
          
          // Loads the bundled offline asset page
          loadUrl("file:///android_asset/local_ndako.html")
        }
      }
    )
  }
}
