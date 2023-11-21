package com.goliath.emojihub.data_sources

import com.goliath.emojihub.data_sources.local.X3dDataSource
import com.goliath.emojihub.data_sources.local.X3dDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {
    @Binds
    abstract fun bindsX3dDataSource(impl: X3dDataSourceImpl): X3dDataSource

    @Binds
    abstract fun bindsApiErrorController(impl: ApiErrorControllerImpl): ApiErrorController
}