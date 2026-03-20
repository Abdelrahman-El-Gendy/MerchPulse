package com.merchpulse.core.supabase.di

import com.merchpulse.core.supabase.client.SupabaseClientProvider
import com.merchpulse.core.supabase.datasource.*
import com.merchpulse.core.supabase.repository.*
import com.merchpulse.shared.domain.repository.AuditRepository
import com.merchpulse.shared.domain.repository.AuthRepository
import com.merchpulse.shared.domain.repository.EmployeeRepository
import com.merchpulse.shared.domain.repository.ProductRepository
import com.merchpulse.shared.domain.repository.PunchRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import org.koin.dsl.module

/**
 * Koin module for Supabase dependencies.
 *
 * Provides:
 * - SupabaseClient singleton
 * - Postgrest and GoTrue plugin accessors
 * - Remote data sources for each domain entity
 */
val supabaseModule = module {

    // ── Core Client ────────────────────────────────
    single<SupabaseClient> { SupabaseClientProvider.client }
    single<Postgrest> { get<SupabaseClient>().postgrest }
    single<Auth> { get<SupabaseClient>().auth }

    // ── Remote Data Sources ────────────────────────
    single<SupabaseProductDataSource> { SupabaseProductDataSourceImpl(get()) }
    single<SupabaseEmployeeDataSource> { SupabaseEmployeeDataSourceImpl(get()) }
    single<SupabasePunchDataSource> { SupabasePunchDataSourceImpl(get()) }
    single<SupabaseAuditDataSource> { SupabaseAuditDataSourceImpl(get()) }

    // ── Repositories ──────────────────────────────
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<ProductRepository> { SupabaseProductRepository(get()) }
    single<EmployeeRepository> { SupabaseEmployeeRepository(get()) }
    single<PunchRepository> { SupabasePunchRepository(get()) }
    single<AuditRepository> { SupabaseAuditRepository(get()) }
}
