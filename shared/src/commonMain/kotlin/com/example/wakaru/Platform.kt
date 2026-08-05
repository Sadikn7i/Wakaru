package com.example.wakaru

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform