package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.service.VoiceNarrationManager
import com.example.ui.navigation.AppNavHost
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
  private val viewModel: MainViewModel by viewModels()
  private lateinit var voiceNarrationManager: VoiceNarrationManager

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    voiceNarrationManager = VoiceNarrationManager(this)

    setContent {
      MyApplicationTheme {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = MaterialTheme.colorScheme.background
        ) {
          AppNavHost(
            viewModel = viewModel,
            voiceManager = voiceNarrationManager
          )
        }
      }
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    if (::voiceNarrationManager.isInitialized) {
      voiceNarrationManager.destroy()
    }
  }
}

