package com.goliath.emojihub.repositories

import com.goliath.emojihub.repositories.local.X3dRepositoryImpl
import com.goliath.emojihub.repositories.local.X3dRepository
import com.goliath.emojihub.repositories.remote.EmojiRepository
import com.goliath.emojihub.repositories.remote.EmojiRepositoryImpl
import com.goliath.emojihub.repositories.remote.FakeEmojiRepository
import com.goliath.emojihub.repositories.remote.FakeEmojiRepositoryImpl
import com.goliath.emojihub.repositories.remote.FakeUserRepository
import com.goliath.emojihub.repositories.remote.FakeUserRepositoryImpl
import com.goliath.emojihub.repositories.remote.PostRepository
import com.goliath.emojihub.repositories.remote.PostRepositoryImpl
import com.goliath.emojihub.repositories.remote.UserRepository
import com.goliath.emojihub.repositories.remote.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindsUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    abstract fun bindsEmojiRepository(impl: EmojiRepositoryImpl): EmojiRepository

    @Binds
    abstract fun bindsPostRepository(impl: PostRepositoryImpl): PostRepository

    @Binds
    abstract fun bindsX3dRepository(impl: X3dRepositoryImpl): X3dRepository
}

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RepositoryModule::class]
)
abstract class FakeRepositoryModule {
    @Binds
    abstract fun bindsUserRepository(impl: FakeUserRepositoryImpl): FakeUserRepository

    @Binds
    abstract fun bindsEmojiRepository(impl: FakeEmojiRepositoryImpl): FakeEmojiRepository
}