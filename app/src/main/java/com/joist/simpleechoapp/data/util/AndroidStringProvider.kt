package com.joist.simpleechoapp.data.util

import android.content.Context
import com.joist.simpleechoapp.R
import com.joist.simpleechoapp.domain.util.StringProvider
import com.joist.simpleechoapp.domain.util.StringResource

/**
 * Android implementation of [StringProvider] that uses Android string resources.
 *
 * @property context Application context for accessing string resources
 */
class AndroidStringProvider(private val context: Context) : StringProvider {

    override fun getString(stringId: StringResource): String {
        return context.getString(mapToAndroidResource(stringId))
    }

    override fun getString(stringId: StringResource, vararg formatArgs: Any): String {
        return context.getString(mapToAndroidResource(stringId), *formatArgs)
    }

    /**
     * Maps domain string resources to Android string resource IDs.
     */
    private fun mapToAndroidResource(stringId: StringResource): Int {
        return when (stringId) {
            StringResource.ERROR_TEXT_EMPTY -> R.string.error_text_empty
            StringResource.ERROR_UNKNOWN_VALIDATION -> R.string.error_unknown_validation
            StringResource.ERROR_UNEXPECTED -> R.string.error_unexpected
            StringResource.ERROR_SERVER_INVALID_CONTENT -> R.string.error_server_invalid_content
            StringResource.ERROR_NETWORK -> R.string.error_network
            StringResource.ERROR_SERVER_UNAVAILABLE -> R.string.error_server_unavailable
            StringResource.ERROR_VALIDATION_FAILED -> R.string.error_validation_failed
        }
    }
}
