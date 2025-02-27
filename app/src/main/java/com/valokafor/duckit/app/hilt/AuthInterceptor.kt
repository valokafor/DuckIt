package com.valokafor.duckit.app.hilt

import com.valokafor.duckit.api.common.AuthDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val authDataStore: AuthDataStore
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()

        val authToken = runBlocking { authDataStore.authToken.first() }

        if (authToken.isNotEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $authToken")
        }

        return chain.proceed(requestBuilder.build())
    }
}