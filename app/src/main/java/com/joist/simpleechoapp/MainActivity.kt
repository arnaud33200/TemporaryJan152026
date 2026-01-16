package com.joist.simpleechoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.joist.simpleechoapp.di.ViewModelFactory
import com.joist.simpleechoapp.presentation.echo.EchoScreen
import com.joist.simpleechoapp.presentation.echo.EchoViewModel
import com.joist.simpleechoapp.ui.theme.SimpleEchoAppTheme

/**
 * Main activity for the Simple Echo App.
 * Sets up the UI with Jetpack Compose and initializes the ViewModel.
 */
class MainActivity : ComponentActivity() {

    private val viewModel: EchoViewModel by viewModels { ViewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize dependency injection container
        // Analytics tracker will be initialized lazily when first accessed
        com.joist.simpleechoapp.di.AppModule.init(applicationContext)

        enableEdgeToEdge()
        setContent {
            SimpleEchoAppTheme {
                EchoScreen(
                    modifier = Modifier.fillMaxSize(),
                    viewModel = viewModel
                )
            }
        }
    }
}