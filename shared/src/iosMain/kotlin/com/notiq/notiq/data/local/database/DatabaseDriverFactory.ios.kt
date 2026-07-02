package com.notiq.notiq.data.local.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.notiq.db.NotesDatabase

class DatabaseDriverFactory {
    fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            NotesDatabase.Schema,
            "notes.db"
        )
    }
}