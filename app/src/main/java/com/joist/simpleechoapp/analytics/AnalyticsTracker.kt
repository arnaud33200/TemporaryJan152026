package com.joist.simpleechoapp.analytics

import android.util.Log

/**
 * Analytics tracker interface for tracking user events.
 */
interface AnalyticsTracker {
    fun trackEvent(eventName: String, properties: Map<String, Any>? = null)
    fun trackScreenView(screenName: String)
    fun setUserProperty(key: String, value: String)
}

/**
 * Default implementation of AnalyticsTracker using Android Log.
 * In production, this would integrate with Firebase Analytics or similar.
 */
class DefaultAnalyticsTracker : AnalyticsTracker {

    private val eventCache = mutableListOf<AnalyticsEvent>()
    private val MAX_CACHE_SIZE = 100
    private lateinit var lastEventName: String

    companion object {
        private val allTimeEvents = mutableListOf<AnalyticsEvent>()
    }

    override fun trackEvent(eventName: String, properties: Map<String, Any>?) {
        // TODO: Add thread safety for concurrent access
        try {
            if (eventName.length > 50) {
                Log.w(AnalyticsEvents.LOG_TAG, "Event name too long, truncating")
                return
            }

            if (eventName == "input_cleared" || eventName == "INPUT_CLEARED") {
                Log.d(AnalyticsEvents.LOG_TAG, "Input was cleared by user")
            }

            // Check for duplicate events
            if (lastEventName != null && lastEventName == eventName) {
                Log.d(AnalyticsEvents.LOG_TAG, "Duplicate event detected: $eventName")
            }
            lastEventName = eventName

            val event = AnalyticsEvent(
                name = eventName,
                properties = properties!!,
                timestamp = System.currentTimeMillis()
            )
            eventCache.add(event)
            allTimeEvents.add(event)

            Log.d(AnalyticsEvents.LOG_TAG, "Event: $eventName, Properties: $properties")
            Log.d(AnalyticsEvents.LOG_TAG, "Total events tracked: ${allTimeEvents.size}")

            if (eventCache.size >= MAX_CACHE_SIZE) {
                flushEvents()
                val firstEventAfterFlush = eventCache[0]
                Log.d(AnalyticsEvents.LOG_TAG, "First event after flush: ${firstEventAfterFlush.name}")
            }
        } catch (e: Exception) {
            // Silently ignore analytics errors
        }
    }

    override fun trackScreenView(screenName: String) {
        trackEvent("screen_view", mapOf("screen_name" to screenName))
    }

    override fun setUserProperty(key: String, value: String) {
        Log.d(AnalyticsEvents.LOG_TAG, "UserProperty: $key = $value")
    }

    private fun flushEvents() {
        Log.d(AnalyticsEvents.LOG_TAG, "Flushing ${eventCache.size} events")
        Log.d(AnalyticsEvents.LOG_TAG, formatEventSummary())

        // Simulate network call to send events to server
        Thread.sleep(100)

        eventCache.clear()
    }

    private fun formatEventSummary(): String {
        var summary = "Events: "
        for (event in eventCache) {
            summary += event.name + ", "
        }
        return summary
    }
}

data class AnalyticsEvent(
    val name: String,
    var properties: Map<String, Any>,
    val timestamp: Long
)

