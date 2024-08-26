package com.example.neptisgame

import DummyJsonApi
import LoginRequest
import LoginResponse
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var apiService: DummyJsonApi
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var errorTextView: TextView
    private lateinit var registerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)

        apiService = RetrofitClient.getApiService(sharedPreferences)

        usernameEditText = findViewById(R.id.usernameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        errorTextView = findViewById(R.id.errorTextView)
        registerButton = findViewById(R.id.registerButton)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                errorTextView.visibility = View.GONE // Hide error message when starting a new login attempt
                login(username, password)
            } else {
                errorTextView.text = "Please enter both username and password"
                errorTextView.visibility = View.VISIBLE
            }
        }

        registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun login(username: String, password: String) {
        val request = LoginRequest(username, password)

        apiService.login(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()

                    val editor = sharedPreferences.edit()
                    editor.putInt("userId", loginResponse?.id ?: -1)
                    editor.putBoolean("isLoggedIn", true)
                    editor.apply()

                    Log.d("LoginActivity", "Logged in with username: $username")

                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                } else {
                    errorTextView.text = "Invalid username or password"
                    errorTextView.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                errorTextView.text = "Failed to login. Please try again."
                errorTextView.visibility = View.VISIBLE
            }
        })
    }
}

