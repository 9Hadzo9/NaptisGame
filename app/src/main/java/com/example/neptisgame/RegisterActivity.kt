package com.example.neptisgame

import RegisterRequest
import RegisterResponse
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.neptisgame.MainActivity
import com.example.neptisgame.R
import com.example.neptisgame.api.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var ageEditText: EditText
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize views
        firstNameEditText = findViewById(R.id.firstNameEditText)
        lastNameEditText = findViewById(R.id.lastNameEditText)
        ageEditText = findViewById(R.id.ageEditText)
        usernameEditText = findViewById(R.id.usernameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        registerButton = findViewById(R.id.registerButton)
        sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)

        registerButton.setOnClickListener {
            val firstName = firstNameEditText.text.toString().trim()
            val lastName = lastNameEditText.text.toString().trim()
            val age = ageEditText.text.toString().trim().toIntOrNull() ?: 0
            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (firstName.isNotEmpty() && lastName.isNotEmpty() && age > 0 && username.isNotEmpty() && password.isNotEmpty()) {
                register(firstName, lastName, age, username, password)
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun register(firstName: String, lastName: String, age: Int, username: String, password: String) {
        val request = RegisterRequest(firstName, lastName, age, username, password)
        val apiService = RetrofitClient.getApiService(sharedPreferences)

        apiService.register(request).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val registerResponse = response.body()

                    val editor = sharedPreferences.edit()
                    editor.putString("username", username)
                    editor.putString("password", password) // Not recommended to store passwords
                    editor.putInt("userId", response.body()?.id ?: -1)
                    editor.putBoolean("isLoggedIn", true)
                    editor.apply()

                    Log.d("RegisterActivity", "Registering with username: $username, password: $password")

                    startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                    finish()

                } else {
                    when (response.code()) {
                        400 -> Toast.makeText(this@RegisterActivity, "Bad Request: Invalid data", Toast.LENGTH_SHORT).show()
                        409 -> Toast.makeText(this@RegisterActivity, "Username already exists", Toast.LENGTH_SHORT).show()
                        else -> Toast.makeText(this@RegisterActivity, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Toast.makeText(this@RegisterActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun updateUser(userId: Int, updatedFields: Map<String, Any>) {
        val apiService = RetrofitClient.getApiService(sharedPreferences)
        apiService.updateUser(userId, updatedFields).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful && response.body() != null) {
                    Log.d("MainActivity", "User updated: ${response.body()}")
                } else {
                    Log.e("MainActivity", "Failed to update user: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.e("MainActivity", "Error updating user: ${t.message}")
            }
        })
    }

    private fun deleteUser(userId: Int) {
        val apiService = RetrofitClient.getApiService(sharedPreferences)
        apiService.deleteUser(userId).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful && response.body() != null) {
                    Log.d("MainActivity", "User deleted: ${response.body()}")
                } else {
                    Log.e("MainActivity", "Failed to delete user: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.e("MainActivity", "Error deleting user: ${t.message}")
            }
        })
    }



}
