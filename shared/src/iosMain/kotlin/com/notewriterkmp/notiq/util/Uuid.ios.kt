package com.notewriterkmp.notiq.notiq.util


import platform.Foundation.NSUUID

actual fun randomUUID(): String = NSUUID().UUIDString()
