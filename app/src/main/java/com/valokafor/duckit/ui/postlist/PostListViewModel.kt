package com.valokafor.duckit.ui.postlist

import com.valokafor.duckit.domain.usecase.GetPostsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valokafor.duckit.api.common.ApiResult
import com.valokafor.duckit.api.common.AuthDataStore
import com.valokafor.duckit.domain.usecase.DownVotePostUseCase
import com.valokafor.duckit.domain.usecase.UpvotePostUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltViewModel
class PostListViewModel @Inject constructor(
    private val getPostsUseCase: GetPostsUseCase,
    private val upvotePostUseCase: UpvotePostUseCase,
    private val downVotePostUseCase: DownVotePostUseCase,
    private val authDataStore: AuthDataStore
) : ViewModel() {

    private val _postsState = MutableStateFlow<PostsState>(PostsState.Loading)
    val postsState: StateFlow<PostsState> = _postsState.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    init {
        fetchPosts()
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        viewModelScope.launch {
            authDataStore.authToken.collect { token ->
                _isLoggedIn.value = token.isNotEmpty()
            }
        }
    }

    fun fetchPosts() {
        viewModelScope.launch {
            _postsState.value = PostsState.Loading
            getPostsUseCase().collectLatest { result ->
                _postsState.value = when (result) {
                    is ApiResult.Success -> PostsState.Success(result.data)
                    is ApiResult.Error -> PostsState.Error(result.message)
                    is ApiResult.Exception -> PostsState.Error(result.e.message ?: "Unknown error")
                }
            }
        }
    }

    fun upVotePost(postId: String) {
        if (!_isLoggedIn.value) {
            // Could handle this by emitting a one-time event to show login dialog
            return
        }

        viewModelScope.launch {
            upvotePostUseCase(postId).collectLatest { result ->
                when (result) {
                    is ApiResult.Success -> updatePostVotes(postId, result.data)
                    is ApiResult.Error -> {}
                    is ApiResult.Exception -> {}
                }
            }
        }
    }

    fun downVotePost(postId: String) {
        if (!_isLoggedIn.value) {
            // Could handle this by emitting a one-time event to show login dialog
            return
        }

        viewModelScope.launch {
            downVotePostUseCase(postId).collectLatest { result ->
                when (result) {
                    is ApiResult.Success -> updatePostVotes(postId, result.data)
                    is ApiResult.Error -> {}
                    is ApiResult.Exception -> {}
                }
            }
        }
    }

    private fun updatePostVotes(postId: String, upvotes: Int) {
        val currentState = _postsState.value
        if (currentState is PostsState.Success) {
            val updatedPosts = currentState.posts.map { post ->
                if (post.id == postId) post.copy(upvotes = upvotes) else post
            }
            _postsState.value = PostsState.Success(updatedPosts)
        }
    }
}