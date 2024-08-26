import android.content.SharedPreferences
import api.AuthInterceptor

import com.example.neptisgame.api.TokenAuthenticator
import com.example.neptisgame.api.User
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private lateinit var apiService: DummyJsonApi

    fun getApiService(sharedPreferences: SharedPreferences): DummyJsonApi {
        if (!::apiService.isInitialized) {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://dummyjson.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(provideOkHttpClient(sharedPreferences))
                .build()

            apiService = retrofit.create(DummyJsonApi::class.java)
        }
        return apiService
    }

    private fun provideOkHttpClient(sharedPreferences: SharedPreferences): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(sharedPreferences))
            .authenticator(TokenAuthenticator(sharedPreferences, apiService))
            .build()
    }

}

