package com.notewriterkmp.notiq.di

import org.koin.dsl.module
import com.notewriterkmp.notiq.data.local.database.NotesLocalDataSource
import com.notewriterkmp.notiq.data.repository.NotesRepositoryImpl
import com.notewriterkmp.notiq.domain.repository.NotesRepository
import com.notewriterkmp.notiq.domain.usecase.AddNoteUseCase
import com.notewriterkmp.notiq.domain.usecase.DeleteNoteUseCase
import com.notewriterkmp.notiq.domain.usecase.GetNotesUseCase
import com.notewriterkmp.notiq.domain.usecase.UpdateNoteUseCase
import com.notewriterkmp.notiq.notiq.presentation.NoteLIstScreen.NotesListViewModel
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

}

expect val platformModule: Module
