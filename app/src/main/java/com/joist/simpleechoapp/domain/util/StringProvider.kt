package com.joist.simpleechoapp.domain.util

/**
 * Interface for providing string resources.
 * Abstracts the Android framework dependency from the domain layer,
 * maintaining Clean Architecture principles.
 */
interface StringProvider {
    fun getString(stringId: StringResource): String
    fun getString(stringId: StringResource, vararg formatArgs: Any): String
}

/**
 * Enum representing all string resources used in the domain and data layers.
 * This keeps the domain layer independent of Android framework.
 */
enum class StringResource {
    ERROR_TEXT_EMPTY,
    ERROR_UNKNOWN_VALIDATION,
    ERROR_UNEXPECTED,
    ERROR_SERVER_INVALID_CONTENT,
    ERROR_NETWORK,
    ERROR_SERVER_UNAVAILABLE,
    ERROR_VALIDATION_FAILED
}
