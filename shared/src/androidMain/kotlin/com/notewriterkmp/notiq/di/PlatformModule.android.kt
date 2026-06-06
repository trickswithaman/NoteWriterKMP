package com.notewriterkmp.notiq.di

import com.notewriterkmp.db.NotesDatabase
import com.notewriterkmp.notiq.data.local.database.DatabaseDriverFactory
import com.notewriterkmp.notiq.presentation.NotesListViewModel
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