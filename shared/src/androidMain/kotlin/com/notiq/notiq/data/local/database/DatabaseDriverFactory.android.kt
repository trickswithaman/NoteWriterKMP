package com.notiq.notiq.data.local.database

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.notiq.db.NotesDatabase

class DatabaseDriverFactory(
    private val context: Context
) {
    fun createDriver(): SqlDriver {

        return AndroidSqliteDriver(
            NotesDatabase.Schema,
            context,
            "notes.db"
        )
    }
}
