package com.valokafor.duckit.domain.usecase

import com.valokafor.duckit.api.common.ApiService
import com.valokafor.duckit.api.common.ApiResult
import com.valokafor.duckit.api.request.NewPostRequestDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject


class CreatePostUseCase @Inject constructor(
    private val apiService: ApiService
) {
    suspend operator fun invoke(headline: String, imageUrl: String): Flow<ApiResult<Unit>> = flow {
        try {
            val request = NewPostRequestDto(headline, imageUrl)
            val response = apiService.createPost(request)

            if (response.isSuccessful) {
                emit(ApiResult.Success(Unit))
            } else {
                emit(ApiResult.Error(response.code(), "Failed to create post: ${response.message()}"))
            }
        } catch (e: HttpException) {
            when (e.code()) {
                401 -> emit(ApiResult.Error(e.code(), "Authentication required"))
                else -> emit(ApiResult.Error(e.code(), "Failed to create post: ${e.message()}"))
            }
        } catch (e: IOException) {
            emit(ApiResult.Exception(e))
        } catch (e: Exception) {
            emit(ApiResult.Exception(e))
        }
    }
}