package api


import okhttp3.Interceptor
import okhttp3.Response
import android.content.SharedPreferences

class AuthInterceptor(private val sharedPreferences: SharedPreferences) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val token = sharedPreferences.getString("authToken", null)

        val modifiedRequest = if (token.isNullOrEmpty()) {
            originalRequest
        } else {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        }

        return chain.proceed(modifiedRequest)
    }

}
