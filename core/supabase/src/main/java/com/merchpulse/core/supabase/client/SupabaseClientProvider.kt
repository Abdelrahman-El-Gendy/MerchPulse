package com.merchpulse.core.supabase.client

import com.merchpulse.core.supabase.config.SupabaseConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.FlowType
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime

/**
 * Creates and configures the Supabase client singleton.
 *
 * Includes:
 * - **Postgrest**: For direct CRUD operations against the database
 * - **GoTrue**: For authentication (email/password, OAuth, magic links)
 * - **Realtime**: For live data subscriptions (future use for sync)
 */
object SupabaseClientProvider {

    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = SupabaseConfig.SUPABASE_URL,
            supabaseKey = SupabaseConfig.SUPABASE_ANON_KEY
        ) {
            install(Postgrest) {
                // Use SERIAL_NAME to match our @SerialName annotations on DTOs
                // This overrides the default camelCase→snake_case conversion
            }

            install(Auth) {
                flowType = FlowType.PKCE
                scheme = SupabaseConfig.DEEPLINK_SCHEME
                host = SupabaseConfig.DEEPLINK_HOST
            }

            install(Realtime)
        }
    }
}
