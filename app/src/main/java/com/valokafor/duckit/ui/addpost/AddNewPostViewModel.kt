package com.valokafor.duckit.ui.addpost

import androidx.lifecycle.ViewModel
import com.valokafor.duckit.domain.usecase.CreatePostUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import com.valokafor.duckit.api.common.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltViewModel
class AddNewPostViewModel @Inject constructor(
    private val createPostUseCase: CreatePostUseCase
): ViewModel() {

    private val _newPostState = MutableStateFlow<NewPostState>(NewPostState.Idle)
    val newPostState: StateFlow<NewPostState> = _newPostState.asStateFlow()

    private val _headline = MutableStateFlow("")
    val headline: StateFlow<String> = _headline.asStateFlow()

    private val _imageUrl = MutableStateFlow("")
    val imageUrl: StateFlow<String> = _imageUrl.asStateFlow()

    fun updateHeadline(text: String) {
        _headline.value = text
    }

    fun updateImageUrl(url: String) {
        _imageUrl.value = url
    }

    fun createPost() {
        val currentHeadline = headline.value
        val currentImageUrl = imageUrl.value

        if (currentHeadline.isBlank() || currentImageUrl.isBlank()) {
            _newPostState.value = NewPostState.Error("Headline and image URL are required")
            return
        }

        viewModelScope.launch {
            _newPostState.value = NewPostState.Loading

            createPostUseCase(currentHeadline, currentImageUrl).collectLatest { result ->
                _newPostState.value = when (result) {
                    is ApiResult.Success -> {
                        _headline.value = ""
                        _imageUrl.value = ""
                        NewPostState.Success
                    }
                    is ApiResult.Error -> NewPostState.Error(result.message)
                    is ApiResult.Exception -> NewPostState.Error(result.e.message ?: "Unknown error")
                }
            }
        }
    }

    fun resetState() {
        _newPostState.value = NewPostState.Idle
    }
}