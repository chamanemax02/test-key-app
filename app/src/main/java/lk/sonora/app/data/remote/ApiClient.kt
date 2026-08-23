package lk.sonora.app.data.remote

import lk.sonora.app.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    private val DEFAULT_BASE_URL = BuildConfig.DEFAULT_API_BASE_URL
    var apiKey: String = BuildConfig.DEFAULT_API_KEY
    var customBaseUrl: String = DEFAULT_BASE_URL

    private val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    private var retrofit: Retrofit? = null
    private var service: SonoraApiService? = null

    fun getService(baseUrl: String = customBaseUrl): SonoraApiService {
        val normalizedUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        if (service == null || customBaseUrl != normalizedUrl) {
            customBaseUrl = normalizedUrl
            retrofit = Retrofit.Builder()
                .baseUrl(customBaseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            service = retrofit!!.create(SonoraApiService::class.java)
        }
        return service!!
    }
}
