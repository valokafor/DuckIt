package com.valokafor.duckit.ui.postlist

sealed class VoteState {
    object Loading : VoteState()
    data class Success(val postId: String, val upvotes: Int) : VoteState()
    data class Error(val message: String, val postId: String) : VoteState()
}