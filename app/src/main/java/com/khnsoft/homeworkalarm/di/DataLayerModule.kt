package com.khnsoft.homeworkalarm.di

import com.khnsoft.data.di.RepositoryModule
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module(includes = [RepositoryModule::class])
@InstallIn(SingletonComponent::class)
abstract class DataLayerModule