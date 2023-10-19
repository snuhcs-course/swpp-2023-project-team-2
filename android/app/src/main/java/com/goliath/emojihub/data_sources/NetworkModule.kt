package com.goliath.emojihub.data_sources

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val baseURL = API_BASE_URL

    @Provides
    fun provideRetrofit(

    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    fun providesUserRestApi(retrofit: Retrofit): UserApi {
        return retrofit.create(UserApi::class.java)
    }
}