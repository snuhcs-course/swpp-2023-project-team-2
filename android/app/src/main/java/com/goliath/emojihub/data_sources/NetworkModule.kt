package com.goliath.emojihub.data_sources

import android.util.Log
import com.goliath.emojihub.EmojiHubApplication
import com.goliath.emojihub.data_sources.api.EmojiApi
import com.goliath.emojihub.data_sources.api.PostApi
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
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val baseURL = API_BASE_URL

    @Provides
    @Singleton
    fun provideRetrofit(

    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseURL)
            .client(provideOkHttpClient())
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
    fun providesUserRestApi(retrofit: Retrofit): UserApi =
        retrofit.create(UserApi::class.java)

    @Provides
    @Singleton
    fun providesEmojiRestApi(retrofit: Retrofit): EmojiApi =
        retrofit.create(EmojiApi::class.java)

    @Provides
    @Singleton
    fun providesPostRestApi(retrofit: Retrofit): PostApi =
        retrofit.create(PostApi::class.java)

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
        Log.d("header", accessToken ?: "")
        return chain.proceed(request.build())
    }
}