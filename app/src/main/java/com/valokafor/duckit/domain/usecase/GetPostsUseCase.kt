package com.valokafor.duckit.domain.usecase
import com.valokafor.duckit.api.common.ApiService
import com.valokafor.duckit.api.common.ApiResult
import com.valokafor.duckit.domain.Post
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetPostsUseCase @Inject constructor(
    private val apiService: ApiService
) {
    operator fun invoke(): Flow<ApiResult<List<Post>>> = flow {
        try {
            val response = apiService.getPosts()
            val posts = response.posts ?: emptyList()
            emit(ApiResult.Success(posts))
        } catch (e: HttpException) {
            emit(ApiResult.Error(e.code(), "Failed to load posts: ${e.message()}"))
        } catch (e: IOException) {
            emit(ApiResult.Exception(e))
        } catch (e: Exception) {
            emit(ApiResult.Exception(e))
        }
    }
}