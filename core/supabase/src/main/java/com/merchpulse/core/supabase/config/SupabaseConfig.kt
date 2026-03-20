package com.merchpulse.core.supabase.config

/**
 * Supabase connection configuration.
 *
 * In production, inject these values via BuildConfig or local.properties.
 * Never commit real keys to version control.
 */
object SupabaseConfig {
    const val SUPABASE_URL = "https://oetrzphsxvxwlkgyqphf.supabase.co"
    const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im9ldHJ6cGhzeHZ4d2xrZ3lxcGhmIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTg1NDg4NzksImV4cCI6MjA3NDEyNDg3OX0.oUvu4-QCK0AgBWDVYRb1pEfF8LIyTJVZcQtDVyMTRec"

    // Deep link configuration for OAuth callbacks
    const val DEEPLINK_SCHEME = "merchpulse"
    const val DEEPLINK_HOST = "auth"
}
