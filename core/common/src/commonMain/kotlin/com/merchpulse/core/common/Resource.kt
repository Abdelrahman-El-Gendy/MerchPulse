package com.merchpulse.core.common

sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val exception: Throwable? = null, val message: String? = null) : Resource<Nothing>()
    data object Loading : Resource<Nothing>()
}

interface DispatcherProvider {
    val main: kotlin.coroutines.CoroutineContext
    val io: kotlin.coroutines.CoroutineContext
    val default: kotlin.coroutines.CoroutineContext
}
