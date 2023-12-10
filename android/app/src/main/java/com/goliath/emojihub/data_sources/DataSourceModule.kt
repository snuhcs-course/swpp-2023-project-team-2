package com.goliath.emojihub.data_sources

import com.goliath.emojihub.data_sources.local.MediaDataSource
import com.goliath.emojihub.data_sources.local.MediaDataSourceImpl
import com.goliath.emojihub.data_sources.local.X3dDataSource
import com.goliath.emojihub.data_sources.local.X3dDataSourceImpl
import com.goliath.emojihub.data_sources.remote.EmojiDataSource
import com.goliath.emojihub.data_sources.remote.EmojiDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {
    @Binds
    abstract fun bindsX3dDataSource(impl: X3dDataSourceImpl): X3dDataSource

    @Binds
    abstract fun bindsEmojiDataSource(impl: EmojiDataSourceImpl): EmojiDataSource

    @Binds
    abstract fun bindsMediaDataSource(impl: MediaDataSourceImpl): MediaDataSource

    @Binds
    abstract fun bindsApiErrorController(impl: ApiErrorControllerImpl): ApiErrorController
}