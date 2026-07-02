package com.notiq

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform