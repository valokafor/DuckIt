package com.valokafor.duckit.ui.addpost

sealed class NewPostState {
    object Idle : NewPostState()
    object Loading : NewPostState()
    object Success : NewPostState()
    data class Error(val message: String) : NewPostState()
}