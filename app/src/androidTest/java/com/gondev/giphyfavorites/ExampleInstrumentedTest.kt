package com.gondev.giphyfavorites

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.gondev.giphyfavorites.model.network.api.GiphyAPI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {


    private val gipyAPI=getService("https://api.giphy.com/v1/gifs/", GiphyAPI::class.java, Interceptor { chain ->
        chain.proceed(chain.request().newBuilder().build())
    })

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.gondev.giphyfavorites", appContext.packageName)

        CoroutineScope(Dispatchers.IO).launch {
            val result = gipyAPI.getGifList()
            assertEquals(20, result.size)
        }
    }


    private fun <T> getService(baseURL: String, service: Class<T>, interceptor: Interceptor) = Retrofit.Builder()
        //.baseUrl(BuildConfig.url_base)
        .baseUrl(baseURL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(OkHttpClient.Builder().apply {
            connectTimeout(15, TimeUnit.SECONDS)
            writeTimeout(15, TimeUnit.SECONDS)
            readTimeout(15, TimeUnit.SECONDS)
            retryOnConnectionFailure(true)
            addInterceptor(interceptor)
            addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
        }.build())
        .callbackExecutor(Executors.newSingleThreadExecutor())
        .build().create(service)
}