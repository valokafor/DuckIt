package com.valokafor.duckit.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valokafor.duckit.api.common.ApiResult
import com.valokafor.duckit.domain.UserAuth
import com.valokafor.duckit.domain.usecase.SignInUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    fun updateEmail(value: String) {
        _email.value = value
    }

    fun updatePassword(value: String) {
        _password.value = value
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            signInUseCase(email, password).collectLatest { result ->
                _authState.value = when (result) {
                    is ApiResult.Success -> {
                        val token = result.data.token
                        AuthState.Authenticated(UserAuth(email, token))
                    }
                    is ApiResult.Error -> AuthState.Error(result.message)
                    is ApiResult.Exception -> AuthState.Error(
                        result.e.message ?: "Unknown error occurred"
                    )
                }
            }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Unauthenticated
    }
}