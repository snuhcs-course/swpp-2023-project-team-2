package com.goliath.emojihub.usecases

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn

@Module
@InstallIn(ViewModelComponent::class)
abstract class UseCaseModule {
    @Binds
    abstract fun bindsUserUseCase(impl: UserUseCaseImpl): UserUseCase

    @Binds
    abstract fun bindsEmojiUseCase(impl: EmojiUseCaseImpl): EmojiUseCase

    @Binds
    abstract fun bindsPostUseCase(impl: PostUseCaseImpl): PostUseCase
}

@Module
@TestInstallIn(
    components = [ViewModelComponent::class],
    replaces = [UseCaseModule::class]
)
abstract class FakeUseCaseModule {
    @Binds
    abstract fun bindsUserUseCase(impl: FakeUserUseCaseImpl): FakeUserUseCase

    @Binds
    abstract fun bindsEmojiUseCase(impl: FakeEmojiUseCaseImpl): FakeEmojiUseCase
}