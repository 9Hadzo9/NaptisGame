package com.example.neptisgame.api

import DummyJsonApi
import RefreshTokenRequest
import android.content.SharedPreferences
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    private val sharedPreferences: SharedPreferences,
    private val apiService: DummyJsonApi
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {

        val refreshToken = sharedPreferences.getString("refreshToken", null) ?: return null

        val refreshResponse = apiService.refreshAuthToken(RefreshTokenRequest(refreshToken)).execute()

        return if (refreshResponse.isSuccessful) {
            val newToken = refreshResponse.body()?.token
            val newRefreshToken = refreshResponse.body()?.refreshToken

            sharedPreferences.edit()
                .putString("authToken", newToken)
                .putString("refreshToken", newRefreshToken)
                .apply()

            response.request.newBuilder()
                .header("Authorization", "Bearer $newToken")
                .build()
        } else {
            null
        }
    }
}
