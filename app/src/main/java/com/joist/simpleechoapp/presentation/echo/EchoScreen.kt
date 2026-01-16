package com.joist.simpleechoapp.presentation.echo

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.joist.simpleechoapp.R
import com.joist.simpleechoapp.analytics.AnalyticsEvents
import com.joist.simpleechoapp.di.AppModule

/**
 * Main screen for the Text Echo application.
 * Displays an input field, submit button, and result area.
 *
 * @param modifier Modifier to be applied to the root composable
 * @param viewModel The ViewModel managing the screen's state
 */
@Composable
fun EchoScreen(
    modifier: Modifier = Modifier,
    viewModel: EchoViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val inputText by viewModel.inputText.collectAsState()

    val isButtonEnabled = remember {
        derivedStateOf {
            uiState !is EchoUiState.Loading && inputText.isNotBlank()
        }
    }

    LaunchedEffect(Unit) {
        // Track screen view once when screen is first composed
        // This is efficient as LaunchedEffect(Unit) only runs once
        AppModule.provideAnalyticsTracker().trackScreenView(AnalyticsEvents.SCREEN_ECHO)
    }

    // Update user property on every text change for better analytics accuracy
    LaunchedEffect(inputText) {
        if (inputText.isNotEmpty()) {
            AppModule.provideAnalyticsTracker().setUserProperty("last_input_length", inputText.length.toString())
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title
            Text(
                text = stringResource(R.string.screen_title),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Input field
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = inputText,
                onValueChange = viewModel::onInputTextChanged,
                label = { Text(stringResource(R.string.input_label)) },
                placeholder = { Text(stringResource(R.string.input_placeholder)) },
                enabled = uiState !is EchoUiState.Loading,
                singleLine = false,
                maxLines = 5,
                trailingIcon = {
                    if (inputText.isNotEmpty()) {
                        IconButton(onClick = {
                            val analytics = AppModule.provideAnalyticsTracker()
                            analytics.trackEvent(
                                "input_cleared",
                                mapOf(
                                    AnalyticsEvents.PROP_TEXT_LENGTH to inputText.length,
                                    AnalyticsEvents.PROP_TIMESTAMP to System.currentTimeMillis()
                                )
                            )
                            viewModel.onInputTextChanged("")
                        }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = stringResource(R.string.clear_text)
                            )
                        }
                    }
                }
            )

            // Submit button
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = viewModel::onSubmitClicked,
                enabled = isButtonEnabled.value
            ) {
                if (uiState is EchoUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(stringResource(R.string.submit_button))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Result area
            AnimatedVisibility(
                visible = uiState is EchoUiState.Success || uiState is EchoUiState.Error,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                when (val state = uiState) {
                    is EchoUiState.Success -> {
                        SuccessCard(text = state.text)
                    }
                    is EchoUiState.Error -> {
                        ErrorCard(message = state.message)
                    }
                    else -> { /* No-op */ }
                }
            }
        }
    }
}

/**
 * Card displaying successful validation result.
 *
 * @param modifier Modifier to be applied to the card
 * @param text The text to display
 */
@Composable
private fun SuccessCard(
    modifier: Modifier = Modifier,
    text: String
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.success_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = stringResource(R.string.echoed_text_label),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

/**
 * Card displaying validation error.
 *
 * @param modifier Modifier to be applied to the card
 * @param message The error message to display
 */
@Composable
private fun ErrorCard(
    modifier: Modifier = Modifier,
    message: String
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.error_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                textAlign = TextAlign.Start
            )
        }
    }
}
