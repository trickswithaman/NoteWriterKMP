package com.notewriterkmp.notiq.util

import java.util.UUID

actual fun randomUUID(): String = UUID.randomUUID().toString()
