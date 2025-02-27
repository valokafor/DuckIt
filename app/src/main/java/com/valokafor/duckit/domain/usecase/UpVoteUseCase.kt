package com.valokafor.duckit.domain.usecase

import com.valokafor.duckit.api.common.ApiService
import com.valokafor.duckit.api.common.ApiResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class UpvotePostUseCase @Inject constructor(
    private val apiService: ApiService
) {
    suspend operator fun invoke(postId: String): Flow<ApiResult<Int>> = flow {
        try {
            val response = apiService.upvotePost(postId)
            emit(ApiResult.Success(response.upvotes))
        } catch (e: HttpException) {
            when (e.code()) {
                401 -> emit(ApiResult.Error(e.code(), "Authentication required"))
                else -> emit(ApiResult.Error(e.code(), "Failed to upvote: ${e.message()}"))
            }
        } catch (e: IOException) {
            emit(ApiResult.Exception(e))
        } catch (e: Exception) {
            emit(ApiResult.Exception(e))
        }
    }
}