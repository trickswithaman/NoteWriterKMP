package com.notiq.notiq.di

import com.notiq.db.NotesDatabase
import com.notiq.notiq.data.local.database.DatabaseDriverFactory
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single { DatabaseDriverFactory(get()) }
    single {
        NotesDatabase(
            driver = get<DatabaseDriverFactory>().createDriver()
        )
    }
}
val androidModule = module {
    single { DatabaseDriverFactory(get()) }
    single { get<DatabaseDriverFactory>().createDriver() }
}