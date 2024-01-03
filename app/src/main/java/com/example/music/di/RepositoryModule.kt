package com.example.music.di

import com.example.music.data.repository.MusicPlayerRepoImpl
import com.example.music.domain.repository.MusicPlayerRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule { // RepositoryModule is used to provide the repository
    @Binds // Binds is used to bind the implementation to the interface
    @Singleton // Singleton is used because we want to have only one instance of this repository
    abstract fun bindMusicPlayerRepository(repository: MusicPlayerRepoImpl): MusicPlayerRepository
}