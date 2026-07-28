package com.notiq.notiq.data.local.database

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import androidx.sqlite.db.SupportSQLiteDatabase
import com.notiq.db.NotesDatabase

class DatabaseDriverFactory(
    private val context: Context
) {
    fun createDriver(): SqlDriver {

        return AndroidSqliteDriver(
            schema = NotesDatabase.Schema,
            context = context,
            name = "notes.db",
            callback = object : AndroidSqliteDriver.Callback(NotesDatabase.Schema) {
                override fun onOpen(db: SupportSQLiteDatabase) {
                    super.onOpen(db)
                    db.execSQL("PRAGMA foreign_keys=ON;")
                }
            }
        )
    }
}
