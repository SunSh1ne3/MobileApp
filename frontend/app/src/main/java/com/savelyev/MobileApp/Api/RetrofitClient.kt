package com.savelyev.MobileApp.Api

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.savelyev.MobileApp.Activity.ContentActivity
import com.savelyev.MobileApp.Fragment.LoginFragment
import com.savelyev.MobileApp.Api.Service.ApiService
import com.savelyev.MobileApp.Utils.TokenManager
import com.squareup.picasso.BuildConfig
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

@SuppressLint("StaticFieldLeak")
object RetrofitClient {
    //private const val BASE_URL = "http://10.0.2.2:8080/"
    private const val BASE_URL = "https://placidly-scholarly-bluejay.cloudpub.ru/"


    private lateinit var okHttpClient: OkHttpClient
    private const val CONNECT_TIMEOUT = 15L
    private const val READ_TIMEOUT = 15L
    private const val WRITE_TIMEOUT = 15L

    private var context: Context? = null

    fun initialize(context: Context) {
        TokenManager.init(context)
        this.context = context.applicationContext

        okHttpClient = OkHttpClient.Builder()
            .addInterceptor(contentTypeInterceptor)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .addInterceptor(unAuthorizedInterceptor)
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }

    private val contentTypeInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val newRequest = originalRequest.newBuilder()
            .apply {
                if (originalRequest.method == "GET") {
                    removeHeader("Content-Type")
                } else {
                    header("Content-Type", "application/json")
                }
                header("Accept", "application/json")
            }
            .build()
        Log.d("ContentTypeInterceptor", "Setting Content-Type and Accept headers")
        chain.proceed(newRequest)
    }


    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }.also {
        it.redactHeader("Authorization")
    }

    private val authInterceptor = Interceptor { chain ->
        val request = chain.request()
        val tokenManager = TokenManager.getInstance()
        val path = request.url.encodedPath

        // Пропускаем аутентификационные запросы
        if (path.startsWith("/api/v1/auth/")) {
            Log.d("AuthInterceptor", "Skipping auth for authentication endpoint")
            return@Interceptor chain.proceed(request)
        }

        Log.d("AuthInterceptor", "Processing request to: ${request.url.toString()}")
        Log.d("AuthInterceptor", "Request method: ${request.method}")
        Log.d("AuthInterceptor", "Request path: $path")

        val token = tokenManager.getToken()
        if (token == null) {
            // 1. Перенаправляем на экран логина
            context?.startActivity(Intent(context, LoginFragment::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }, return@Interceptor Response.Builder()
                .request(chain.request())
                .protocol(Protocol.HTTP_1_1)
                .code(401)
                .message("Unauthorized")
                .body("{ \"error\": \"No token\" }".toResponseBody("application/json".toMediaType()))
                .build())
        }

        try {
            // Добавляем логирование перед отправкой
            if (token != null) {
                Log.d("AuthInterceptor", "Sending request to $path with token: ${token.take(10)}...")
            }
            val newRequest = request.newBuilder()
                .header("Authorization", "Bearer $token")
                .header("Accept", "application/json")
                .build()

            chain.proceed(newRequest)
        } catch (e: HttpException) {
            if (e.code() == 401) {
                // 3. При 401 ошибке очищаем токен и перенаправляем
                TokenManager.getInstance().clearToken()
                context?.startActivity(Intent(context, LoginFragment::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
            }
            throw e
        }
    }

    private val unAuthorizedInterceptor = Interceptor { chain ->
        val request = chain.request()
        val tokenManager = TokenManager.getInstance()
        val path = request.url.encodedPath
        // Пропускаем аутентификационные запросы
        if (path.startsWith("/api/v1/auth/")) {
            Log.d("UnauthInterceptor", "Skipping for authentication endpoint")
            return@Interceptor chain.proceed(request)
        }

        Log.d("UnauthInterceptor", "Processing request to: ${request.url}")
        Log.d("UnauthInterceptor", "Request path: $path")

        // Первый запрос с текущим токеном
        val initialRequest = request.newBuilder()
            .apply {
                tokenManager.getToken()?.let {
                    header("Authorization", "Bearer $it")
                }
            }
            .build()

        val initialResponse = try {
            chain.proceed(initialRequest) // Важно: сохраняем response для дальнейшей обработки
        } catch (e: IOException) {
            // Обработка сетевых ошибок
            throw e
        }

        // Если не 401 - возвращаем response как есть
        if (initialResponse.code != 401) {
            return@Interceptor initialResponse
        }

        // Закрываем предыдущий response перед новым запросом
        initialResponse.close()

        // Очищаем токен и перенаправляем на логин
        context?.let { ctx ->
            val intent = Intent(ctx, ContentActivity::class.java).apply {
                putExtra("SHOW_FRAGMENT", "LOGIN") // Флаг для показа LoginFragment
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            ctx.startActivity(intent)
        }

        // Создаем новый response с 401 кодом
        Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(401)
            .message("Unauthorized")
            .body("{ \"error\": \"Session expired\" }".toResponseBody("application/json".toMediaType()))
            .build()
    }

    private val mapper: ObjectMapper = ObjectMapper()
        .registerModule(KotlinModule.Builder().build())
        .registerModule(JavaTimeModule())
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(JacksonConverterFactory.create(mapper))
            .build()
            .create(ApiService::class.java)
    }
}


