package com.valokafor.duckit.api.request

// Network request/response models
data class SignInRequestDto(
    val email: String,
    val password: String
)