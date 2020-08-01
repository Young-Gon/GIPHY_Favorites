package com.gondev.giphyfavorites.di.module

import android.app.Application
import com.gondev.giphyfavorites.BuildConfig
import com.gondev.giphyfavorites.model.network.api.GiphyAPI
import com.google.gson.GsonBuilder
import com.orhanobut.logger.Logger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

private const val CONNECT_TIMEOUT = 15L
private const val WRITE_TIMEOUT = 15L
private const val READ_TIMEOUT = 15L

@Module
@InstallIn(ApplicationComponent::class)
object NetworkModule{

    @Singleton
    @Provides
    fun provideTrandingAPIService(application: Application) =
        Retrofit.Builder()
        .baseUrl("https://api.giphy.com/v1/gifs/")
        .addConverterFactory(GsonConverterFactory.create(
            GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create()
        ))
        .client(okhttpClient())
        .build()
        .create(GiphyAPI::class.java)

    private fun okhttpClient()=
        OkHttpClient.Builder().apply {
            connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            retryOnConnectionFailure(true)
            addInterceptor{ chain ->
                chain.proceed(chain.request().newBuilder().apply {
                    url(chain.request().url()
                        .newBuilder()
                        .addQueryParameter("api_key", BuildConfig.apikey)
                        .build())
                }.build())
            }
            addInterceptor(HttpLoggingInterceptor(object :HttpLoggingInterceptor.Logger{
                override fun log(message: String) {
                    Logger.t("PRETTY_LOGGER-OKHTTP").i(message)
                }
            }).apply {
                if (BuildConfig.DEBUG) {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            })
        }.build()
}