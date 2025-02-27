package com.valokafor.duckit.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valokafor.duckit.api.common.ApiResult
import com.valokafor.duckit.domain.UserAuth
import com.valokafor.duckit.domain.usecase.SignUpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword.asStateFlow()

    fun updateEmail(value: String) {
        _email.value = value
    }

    fun updatePassword(value: String) {
        _password.value = value
    }

    fun updateConfirmPassword(value: String) {
        _confirmPassword.value = value
    }

    fun signUp() {
        val emailValue = email.value
        val passwordValue = password.value
        val confirmPasswordValue = confirmPassword.value

        // Validate inputs
        if (emailValue.isBlank() || passwordValue.isBlank()) {
            _authState.value = AuthState.Error("Email and password are required")
            return
        }

        if (passwordValue != confirmPasswordValue) {
            _authState.value = AuthState.Error("Passwords do not match")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading

            signUpUseCase(emailValue, passwordValue).collectLatest { result ->
                _authState.value = when (result) {
                    is ApiResult.Success -> {
                        val token = result.data.token
                        AuthState.Authenticated(UserAuth(emailValue, token))
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