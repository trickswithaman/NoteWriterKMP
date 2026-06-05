package com.notewriterkmp.notiq.di

import org.koin.dsl.module
import com.notewriterkmp.notiq.data.local.database.NotesLocalDataSource
import com.notewriterkmp.notiq.data.repository.NotesRepositoryImpl
import com.notewriterkmp.notiq.domain.repository.NotesRepository
import org.koin.core.module.Module

val appModule = module {

    // 🔹 Local DataSource
    single {
        NotesLocalDataSource(get())
    }

    // 🔹 Repository
    single<NotesRepository> {
        NotesRepositoryImpl(get())
    }

}

expect val platformModule: Module
