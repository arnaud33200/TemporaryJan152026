package com.joist.textecho.data.service

import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlin.random.Random

class ValidationService @Inject constructor() {
    suspend fun validateWithServer(text: String): String {
        delay(SIMULATED_NETWORK_DELAY_MS)
        
        if (Random.nextDouble() < ERROR_PROBABILITY) {
            throw ValidationException("Server validation failed: Invalid input detected")
        }
        
        return text
    }

    class ValidationException(message: String) : Exception(message)
    
    companion object {
        private const val SIMULATED_NETWORK_DELAY_MS = 1500L
        private const val ERROR_PROBABILITY = 0.2
    }
}
