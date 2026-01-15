package com.joist.textecho

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.joist.textecho.ui.screen.TextEchoScreen
import com.joist.textecho.ui.theme.TextEchoTheme
import com.joist.textecho.ui.viewmodel.TextEchoViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            val viewModel: TextEchoViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            TextEchoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TextEchoScreen(
                        state = state,
                        onClear = viewModel::onClear,
                        onSubmit = viewModel::onSubmit,
                        onTextChanged = viewModel::onTextChanged
                    )
                }
            }
        }
    }
}
