package com.khnsoft.homeworkalarm.di

import com.khnsoft.domain.di.UseCaseModule
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module(includes = [UseCaseModule::class])
@InstallIn(SingletonComponent::class)
abstract class DomainLayerModule