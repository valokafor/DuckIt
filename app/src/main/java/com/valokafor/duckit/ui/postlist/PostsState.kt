package com.valokafor.duckit.ui.postlist

import com.valokafor.duckit.domain.Post

sealed class PostsState {
    object Loading : PostsState()
    data class Success(val posts: List<Post>) : PostsState()
    data class Error(val message: String) : PostsState()
}