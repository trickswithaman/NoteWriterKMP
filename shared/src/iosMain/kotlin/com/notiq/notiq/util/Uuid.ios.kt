package com.notiq.notiq.notiq.util


import platform.Foundation.NSUUID

actual fun randomUUID(): String = NSUUID().UUIDString()
