package com.savelyev.MobileApp.Api

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.savelyev.MobileApp.Api.Service.ApiService
import com.savelyev.MobileApp.Utils.TokenManager
import com.squareup.picasso.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

@SuppressLint("StaticFieldLeak")
object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8080/"
    private lateinit var okHttpClient: OkHttpClient
    private const val CONNECT_TIMEOUT = 15L
    private const val READ_TIMEOUT = 15L

    private var context: Context? = null
    private var authToken: String? = null

    fun initialize(context: Context, initialToken: String? = null) {
        TokenManager.init(context)
        this.context = context.applicationContext
        this.authToken = initialToken

        okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            //.addInterceptor(unauthorizedInterceptor)
            .build()
    }


    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    private val authInterceptor = Interceptor{ chain ->
        val request = chain.request()

        if (request.url.encodedPath.startsWith("/api/v1/auth/")) {
            return@Interceptor chain.proceed(request)
        }

        try {
            val token = TokenManager.getInstance().getToken()
                ?: throw IOException("Authentication token missing")

            chain.proceed(
                request.newBuilder()
                    //.header("Accept", "application/json")
                    //.header("Content-Type", "application/json")
                    .header("Authorization", "Bearer $token")
                    .build()
            )
        } catch (e: Exception) {
            Log.e("Retrofit", "Auth error: ${e.message}")
            throw e
        }
    }


    private val unauthorizedInterceptor = { chain: okhttp3.Interceptor.Chain ->
        val response = chain.proceed(chain.request())
        when (response.code) {
            401, 403 -> {
                context?.let { ctx ->
                    TokenManager(ctx).clearToken()
                }
                throw IOException("HTTP ${response.code} - ${response.message}")
            }
        }
        response
    }

//    private val client = OkHttpClient.Builder()
//        .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
//        .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
//        .addInterceptor(loggingInterceptor)
//        .addInterceptor(authInterceptor)
//        .addInterceptor(unauthorizedInterceptor)
//        .build()

    private val mapper: ObjectMapper = ObjectMapper()
                    .registerModule(KotlinModule())
                    .registerModule(JavaTimeModule())

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(JacksonConverterFactory.create(mapper))
            .build()
            .create(ApiService::class.java)
    }
}


