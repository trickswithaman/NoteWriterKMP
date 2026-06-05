package com.notewriterkmp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform