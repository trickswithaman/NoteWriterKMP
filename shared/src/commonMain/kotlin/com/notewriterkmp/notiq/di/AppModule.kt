package com.notewriterkmp.notiq.di

import com.notewriterkmp.notiq.data.local.database.NotesLocalDataSource
import com.notewriterkmp.notiq.data.repository.NotesRepositoryImpl
import com.notewriterkmp.notiq.domain.repository.NotesRepository
import com.notewriterkmp.notiq.domain.repository.SettingsRepository
import com.notewriterkmp.notiq.domain.usecase.AddNoteUseCase
import com.notewriterkmp.notiq.domain.usecase.DeleteNoteUseCase
import com.notewriterkmp.notiq.domain.usecase.GetNotesUseCase
import com.notewriterkmp.notiq.domain.usecase.UpdateNoteUseCase
import com.notewriterkmp.notiq.notiq.presentation.NoteLIstScreen.NotesListViewModel
import com.notewriterkmp.notiq.notiq.presentation.SettingScreen.SettingsViewModel
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
