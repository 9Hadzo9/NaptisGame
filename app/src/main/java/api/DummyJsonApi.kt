import com.example.neptisgame.api.User
import retrofit2.Call
import retrofit2.http.*

data class LoginRequest(val username: String, val password: String)
data class LoginResponse(val token: String, val id: Int, val username: String)

data class RegisterRequest(
    val firstName: String,
    val lastName: String,
    val age: Int,
    val username: String,
    val password: String
)

data class RegisterResponse(val id: Int, val firstName: String, val lastName: String, val username: String)

data class RefreshTokenRequest(
    val refreshToken: String,
    val expiresInMins: Int = 60
)

data class RefreshTokenResponse(
    val token: String,
    val refreshToken: String
)


interface DummyJsonApi {

    @POST("/auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("/users/add")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>

    @PUT("/users/{id}")
    fun updateUser(@Path("id") id: Int, @Body request: Map<String, Any>): Call<User>

    @DELETE("/users/{id}")
    fun deleteUser(@Path("id") id: Int): Call<User>

    @GET("/users/{id}")
    fun getUser(@Path("id") id: Int): Call<User>

    @GET("/auth/me")
    fun getCurrentUser(@Header("Authorization") token: String): Call<User>


    @POST("/auth/refresh")
    fun refreshAuthToken(@Body request: RefreshTokenRequest): Call<RefreshTokenResponse>


}
