package com.valokafor.duckit.api.common

import com.valokafor.duckit.api.request.NewPostRequestDto
import com.valokafor.duckit.api.request.SignInRequestDto
import com.valokafor.duckit.api.request.SignUpRequestDto
import com.valokafor.duckit.api.response.AuthResponseDto
import com.valokafor.duckit.api.response.PostsResponseDto
import com.valokafor.duckit.api.response.VoteResponseDto
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("signin")
    suspend fun signIn(@Body request: SignInRequestDto): AuthResponseDto

    @POST("signup")
    suspend fun signUp(@Body request: SignUpRequestDto): AuthResponseDto

    @GET("posts")
    suspend fun getPosts(@Header("Authorization") authToken: String? = null): PostsResponseDto

    @POST("posts/{id}/upvote")
    suspend fun upvotePost(
        @Path("id") postId: String,
    ): VoteResponseDto

    @POST("posts/{id}/downvote")
    suspend fun downvotePost(
        @Path("id") postId: String,
    ): VoteResponseDto

    @POST("posts")
    @Headers("Content-Type: application/json")
    suspend fun createPost(
        @Body request: NewPostRequestDto,
    ): Response<Unit>
}