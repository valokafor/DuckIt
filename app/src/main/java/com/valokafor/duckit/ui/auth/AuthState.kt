package com.valokafor.duckit.ui.auth

import com.valokafor.duckit.domain.UserAuth

// UI state models
sealed class AuthState {
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Authenticated(val user: UserAuth) : AuthState()
    data class Error(val message: String) : AuthState()
}