import com.dicoding.noviantisafitri.storyapp.data.remote.request.LoginRequest
import com.dicoding.noviantisafitri.storyapp.data.remote.request.RegisterRequest
import com.dicoding.noviantisafitri.storyapp.data.remote.response.LoginResponse
import com.dicoding.noviantisafitri.storyapp.data.remote.response.RegisterResponse
import com.dicoding.noviantisafitri.storyapp.data.remote.response.StoriesResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {

    @POST("login")
    suspend fun loginUser(
        @Body loginRequest: LoginRequest
    ): LoginResponse

    @POST("register")
    suspend fun registerUser(
        @Body registerRequest: RegisterRequest
    ): RegisterResponse

    @Multipart
    @POST("stories")
    suspend fun postStoryAuth(
        @Header("Authorization") token: String,
        @Part("description") description: RequestBody,
        @Part photo: MultipartBody.Part,
        @Part("lat") latitude: RequestBody? = null,
        @Part("lon") longitude: RequestBody? = null
    ): StoriesResponse

    @GET("stories")
    suspend fun getStoriesAuth(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("location") location: Int
    ): StoriesResponse
}
