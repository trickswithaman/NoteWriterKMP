package com.notewriterkmp.notiq.di

import com.notewriterkmp.db.NotesDatabase
import com.notewriterkmp.notiq.data.local.database.DatabaseDriverFactory
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single { DatabaseDriverFactory() }
    single {
        NotesDatabase(
            driver = get<DatabaseDriverFactory>().createDriver()
        )
    }

}
