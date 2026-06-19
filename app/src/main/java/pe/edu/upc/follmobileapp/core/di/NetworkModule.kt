package pe.edu.upc.follmobileapp.core.di

import android.content.Context
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {
    // ─── Configuración del servidor ───────────────────────────────────────────
    // Para cambiar el entorno, modifica únicamente esta constante:
    //   • Emulador Android  → "http://10.0.2.2:5237/"
    //   • Dispositivo físico → "http://<IP-LAN-de-tu-PC>:5237/"  (ej. "http://192.168.1.100:5237/")
    //   • Producción (Azure) → "https://foll-backend-iot-h5hkb3czhwedhph0.brazilsouth-01.azurewebsites.net/"
    //
    // Esta misma URL la reutiliza el WebSocket (SignalR) en tiempo real, así que
    // SOLO se cambia aquí para TODA la app (HTTP + WebSockets).
    const val BASE_URL = "http://10.0.2.2:5237/"

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
