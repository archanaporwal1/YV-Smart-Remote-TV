package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.MainRemoteScreen
import com.example.ui.theme.TvRemoteTheme
import com.example.viewmodel.TvRemoteViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      TvRemoteTheme {
        val viewModel: TvRemoteViewModel = viewModel()
        MainRemoteScreen(viewModel = viewModel)
      }
    }
  }
}
