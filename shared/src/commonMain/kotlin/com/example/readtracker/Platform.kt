package com.example.readtracker

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform