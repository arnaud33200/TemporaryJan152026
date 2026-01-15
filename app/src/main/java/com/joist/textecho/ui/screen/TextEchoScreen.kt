package com.joist.textecho.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.joist.textecho.R
import com.joist.textecho.analytics.AnalyticsEvents
import com.joist.textecho.analytics.AnalyticsTracker
import com.joist.textecho.ui.mapper.toErrorMessage
import com.joist.textecho.ui.state.TextEchoState


@Composable
fun TextEchoScreen(
    state: TextEchoState,
    analyticsTracker: AnalyticsTracker? = null,
    onClear: () -> Unit = {},
    onSubmit: () -> Unit = {},
    onTextChanged: (String) -> Unit = {}
) {
    val snackBarHostState = remember { SnackbarHostState() }

    val errorMessage = state.error?.toErrorMessage()

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackBarHostState.showSnackbar(it)
        }
    }

    // Track screen view
    LaunchedEffect(Unit) {
        analyticsTracker!!.trackScreenView(AnalyticsEvents.SCREEN_TEXT_ECHO)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.title),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.enter_a_text_to_validate),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(32.dp))

                InputSection(
                    inputText = state.inputText,
                    isLoading = state.isLoading,
                    hasError = state.error != null,
                    onTextChanged = onTextChanged,
                    onSubmit = onSubmit,
                    onClear = onClear
                )

                Spacer(modifier = Modifier.height(24.dp))

                AnimatedVisibility(
                    visible = state.outputText.isNotEmpty(),
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut() + slideOutVertically()
                ) {
                    OutputSection(outputText = state.outputText)
                }
            }

            if (state.isLoading) {
                LoadingOverlay()
            }
        }
    }
}

@Composable
private fun InputSection(
    inputText: String,
    isLoading: Boolean,
    hasError: Boolean,
    onTextChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onClear: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = inputText,
            onValueChange = onTextChanged,
            label = { Text(stringResource(R.string.enter_a_text_label)) },
            placeholder = { Text(stringResource(R.string.type_something_hint)) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            isError = hasError,
            singleLine = false,
            maxLines = 4,
            supportingText = {
                Text(stringResource(R.string.minimun_3_characters))
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onClear,
                modifier = Modifier.weight(1f),
                enabled = !isLoading && inputText.isNotEmpty()
            ) {
                Text(stringResource(R.string.clear))
            }

            Button(
                onClick = onSubmit,
                modifier = Modifier.weight(1f),
                enabled = !isLoading && inputText.isNotEmpty()
            ) {
                Text(stringResource(R.string.submit))
            }
        }
    }
}

@Composable
private fun OutputSection(outputText: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Validated Output",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = outputText,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun LoadingOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {
            Row(
                modifier = Modifier.padding(24.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = stringResource(R.string.validating),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Preview
@Composable
private fun TextEchoScreenPreview() {
    TextEchoScreen(
        state = TextEchoState()
    )
}
