package pe.edu.upc.follmobileapp.core.di

import android.content.Context
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pe.edu.upc.follmobileapp.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {
    val BASE_URL: String = BuildConfig.BASE_URL
    /** SignalR hub: directo al backend, sin API gateway */
    val HUB_URL: String = BuildConfig.HUB_URL

    @Volatile
    private var retrofit: Retrofit? = null

    fun provideRetrofit(context: Context): Retrofit {
        return retrofit ?: synchronized(this) {
            retrofit ?: run {
                val logging = HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }

                val authInterceptor = Interceptor { chain ->
                    val sharedPrefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
                    val token = sharedPrefs.getString("auth_token", null)
                    
                    val requestBuilder = chain.request().newBuilder()
                    if (!token.isNullOrEmpty()) {
                        requestBuilder.header("Authorization", "Bearer $token")
                    }
                    
                    chain.proceed(requestBuilder.build())
                }

                val okHttpClient = OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .addInterceptor(authInterceptor)
                    .build()

                val instance = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                
                retrofit = instance
                instance
            }
        }
    }
}
