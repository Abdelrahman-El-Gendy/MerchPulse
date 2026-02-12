package com.gndy.merchpulse

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform