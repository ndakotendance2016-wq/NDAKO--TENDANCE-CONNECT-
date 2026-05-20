package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.screens.MainFrame
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.NdakoViewModel

class MainActivity : ComponentActivity() {
  private val viewModel: NdakoViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        MainFrame(viewModel = viewModel)
      }
    }
  }
}
