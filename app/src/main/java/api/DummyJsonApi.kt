import com.example.neptisgame.api.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

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

interface DummyJsonApi {

    @POST("/auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("/users/add")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>

    @GET("/users/{id}")
    fun getUser(@Path("id") id: Int): Call<User>
}
