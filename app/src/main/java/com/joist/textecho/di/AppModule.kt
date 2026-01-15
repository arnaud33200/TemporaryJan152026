package com.joist.textecho.di

import com.joist.textecho.data.repository.TextValidationRepositoryImpl
import com.joist.textecho.domain.repository.TextValidationRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(ActivityRetainedComponent::class)
object AppModule {

    @Provides
    @ActivityRetainedScoped
    fun bindTextValidationRepository(
        impl: TextValidationRepositoryImpl
    ): TextValidationRepository = impl
}
