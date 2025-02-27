package com.valokafor.duckit.api.response

import com.valokafor.duckit.domain.Post

data class PostsResponseDto(
    val posts: List<Post>? = emptyList()
)