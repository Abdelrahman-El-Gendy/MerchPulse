package com.merchpulse.core.common

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

class StandardDispatchers : DispatcherProvider {
    override val main: CoroutineContext = Dispatchers.Main
    override val io: CoroutineContext = Dispatchers.IO
    override val default: CoroutineContext = Dispatchers.Default
}
