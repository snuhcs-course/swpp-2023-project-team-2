package com.goliath.emojihub.data_sources

import com.goliath.emojihub.EmojiHubApplication
import com.goliath.emojihub.data_sources.api.ClipApi
import com.goliath.emojihub.data_sources.api.EmojiApi
import com.goliath.emojihub.data_sources.api.PostApi
import com.goliath.emojihub.data_sources.api.ReactionApi
import com.goliath.emojihub.data_sources.api.UserApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val baseURL = API_BASE_URL
    private const val clipBaseURL = CLIP_BASE_URL

    @Provides
    @Singleton
    @Named("ApiRetrofit")
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseURL)
            .client(provideOkHttpClient())
            .addConverterFactory(nullOnEmptyConverterFactory)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    @Provides
    @Singleton
    @Named("ClipRetrofit")
    fun provideClipRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(clipBaseURL)
            .client(provideClipOkHttpClient())
            .addConverterFactory(nullOnEmptyConverterFactory)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
            .build()

    @Provides
    @Singleton
    fun provideClipOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(CLIPAuthInterceptor())
            .build()

    @Provides
    @Singleton
    fun providesUserRestApi(@Named("ApiRetrofit") retrofit: Retrofit): UserApi =
        retrofit.create(UserApi::class.java)

    @Provides
    @Singleton
    fun providesEmojiRestApi(@Named("ApiRetrofit") retrofit: Retrofit): EmojiApi =
        retrofit.create(EmojiApi::class.java)

    @Provides
    @Singleton
    fun providesPostRestApi(@Named("ApiRetrofit") retrofit: Retrofit): PostApi =
        retrofit.create(PostApi::class.java)

    @Provides
    @Singleton
    fun providesReactionRestApi(@Named("ApiRetrofit") retrofit: Retrofit): ReactionApi =
        retrofit.create(ReactionApi::class.java)

    @Provides
    @Singleton
    fun providesCLIPRestApi(@Named("ClipRetrofit") retrofit: Retrofit): ClipApi =
        retrofit.create(ClipApi::class.java)

    // empty responses should be handled `success`
    private val nullOnEmptyConverterFactory = object : Converter.Factory() {
        fun converterFactory() = this
        override fun responseBodyConverter(type: Type,
            annotations: Array<out Annotation>,
            retrofit: Retrofit
        ) = object : Converter<ResponseBody, Any?> {
            val nextResponseBodyConverter =
                retrofit.nextResponseBodyConverter<Any?>(converterFactory(), type, annotations)
            override fun convert(value: ResponseBody) =
                if (value.contentLength() != 0L) nextResponseBodyConverter.convert(value) else null
        }
    }
}

class AuthInterceptor @Inject constructor(): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
        val accessToken = EmojiHubApplication.preferences.accessToken
        if (accessToken.isNullOrEmpty()) {
            request.removeHeader("Authorization")
        } else {
            request.header("Authorization", "Bearer $accessToken")
        }
        return chain.proceed(request.build())
    }
}

class CLIPAuthInterceptor @Inject constructor(): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val clipApiKey = CLIP_API_KEY
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $clipApiKey")
            .build()
        return chain.proceed(request)
    }
}