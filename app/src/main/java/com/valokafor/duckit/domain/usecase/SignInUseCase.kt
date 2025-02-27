package com.valokafor.duckit.domain.usecase

import com.valokafor.duckit.api.common.ApiService
import com.valokafor.duckit.api.common.ApiResult
import com.valokafor.duckit.api.common.AuthDataStore
import com.valokafor.duckit.api.request.SignInRequestDto
import com.valokafor.duckit.api.response.AuthResponseDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val apiService: ApiService,
    private val authDataStore: AuthDataStore
) {
    operator fun invoke(email: String, password: String): Flow<ApiResult<AuthResponseDto>> = flow {
        try {
            val request = SignInRequestDto(email, password)
            val response = apiService.signIn(request)

            authDataStore.saveAuthToken(response.token)

            emit(ApiResult.Success(response))
        } catch (e: HttpException) {
            val errorMessage = when (e.code()) {
                403 -> "Incorrect password"
                404 -> "Account not found"
                else -> "Sign in failed: ${e.message()}"
            }
            emit(ApiResult.Error(e.code(), errorMessage))
        } catch (e: IOException) {
            emit(ApiResult.Exception(e))
        } catch (e: Exception) {
            emit(ApiResult.Exception(e))
        }
    }
}