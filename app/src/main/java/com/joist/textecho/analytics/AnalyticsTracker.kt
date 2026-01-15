package com.joist.textecho.analytics

interface AnalyticsTracker {
    fun trackEvent(eventName: String, properties: Map<String, Any> = emptyMap())
    fun trackScreenView(screenName: String)
    fun setUserId(userId: String)
    fun setUserProperty(key: String, value: String)
}
