package com.notiq.notiq.di

import com.notiq.notiq.data.local.database.NotesLocalDataSource
import com.notiq.notiq.data.repository.NotesRepositoryImpl
import com.notiq.notiq.domain.repository.NotesRepository
import com.notiq.notiq.domain.repository.SettingsRepository
import com.notiq.notiq.domain.usecase.AddNoteUseCase
import com.notiq.notiq.domain.usecase.DeleteNoteUseCase
import com.notiq.notiq.domain.usecase.GetNotesUseCase
import com.notiq.notiq.domain.usecase.UpdateNoteUseCase
import com.notiq.notiq.notiq.presentation.NoteLIstScreen.NotesListViewModel
import com.notiq.notiq.notiq.presentation.SettingScreen.SettingsViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {

    // 🔹 Local DataSource
    single {
        NotesLocalDataSource(get())
    }

    // 🔹 Repository
    single<NotesRepository> {
        NotesRepositoryImpl(get())
    }
    singleOf(::SettingsRepository)

    // 🔹 Use Cases
    factory { GetNotesUseCase(get()) }
    factory { AddNoteUseCase(get()) }
    factory { DeleteNoteUseCase(get()) }
    factory { UpdateNoteUseCase(get()) }

    factory {
        NotesListViewModel(
            getNotes = get(),
            addNoteUseCase = get(),
            deleteNoteUseCase = get(),
            updateNoteUseCase = get()
        )
    }

    factoryOf(::SettingsViewModel)
}

expect val platformModule: Module
