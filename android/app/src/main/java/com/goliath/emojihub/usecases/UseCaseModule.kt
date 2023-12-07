package com.goliath.emojihub.usecases

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class UseCaseModule {
    @Binds
    abstract fun bindsUserUseCase(impl: UserUseCaseImpl): UserUseCase

    @Binds
    abstract fun bindsEmojiUseCase(impl: EmojiUseCaseImpl): EmojiUseCase

    @Binds
    abstract fun bindsPostUseCase(impl: PostUseCaseImpl): PostUseCase

    @Binds
    abstract fun bindsReactionUseCase(impl: ReactionUseCaseImpl): ReactionUseCase
}