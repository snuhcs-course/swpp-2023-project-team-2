package com.goliath.emojihub.repositories

import com.goliath.emojihub.repositories.local.X3dRepositoryImpl
import com.goliath.emojihub.repositories.local.X3dRepository
import com.goliath.emojihub.repositories.remote.ClipRepository
import com.goliath.emojihub.repositories.remote.ClipRepositoryImpl
import com.goliath.emojihub.repositories.remote.EmojiRepository
import com.goliath.emojihub.repositories.remote.EmojiRepositoryImpl
import com.goliath.emojihub.repositories.remote.PostRepository
import com.goliath.emojihub.repositories.remote.PostRepositoryImpl
import com.goliath.emojihub.repositories.remote.ReactionRepository
import com.goliath.emojihub.repositories.remote.ReactionRepositoryImpl
import com.goliath.emojihub.repositories.remote.UserRepository
import com.goliath.emojihub.repositories.remote.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindsClipRepository(impl: ClipRepositoryImpl): ClipRepository
    @Binds
    abstract fun bindsEmojiRepository(impl: EmojiRepositoryImpl): EmojiRepository

    @Binds
    abstract fun bindsPostRepository(impl: PostRepositoryImpl): PostRepository

    @Binds
    abstract fun bindsReactionRepository(impl: ReactionRepositoryImpl): ReactionRepository

    @Binds
    abstract fun bindsUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    abstract fun bindsX3dRepository(impl: X3dRepositoryImpl): X3dRepository
}