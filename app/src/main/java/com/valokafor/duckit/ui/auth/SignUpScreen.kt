package com.valokafor.duckit.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.valokafor.duckit.R

@Composable
fun SignUpScreen(
    onSignUpComplete: (String) -> Unit,
    onNavigateToLoginScreen: () -> Unit
) {
    val viewModel: SignUpViewModel = hiltViewModel()
    val authState by viewModel.authState.collectAsState()
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val confirmPassword by viewModel.confirmPassword.collectAsState()

    SignUpScreenContent(
        uiState = authState,
        email = email,
        password = password,
        confirmPassword = confirmPassword,
        onEmailChanged = viewModel::updateEmail,
        onPasswordChanged = viewModel::updatePassword,
        onConfirmPasswordChanged = viewModel::updateConfirmPassword,
        onSignUpClicked = viewModel::signUp,
        onSignInClicked = onNavigateToLoginScreen
    )

    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated) {
            val user = (authState as AuthState.Authenticated).user
            viewModel.resetState()
            onSignUpComplete(user.email)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreenContent(
    uiState: AuthState,
    email: String = "",
    password: String = "",
    confirmPassword: String = "",
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onConfirmPasswordChanged: (String) -> Unit,
    onSignUpClicked: () -> Unit,
    onSignInClicked: () -> Unit
) {
    val screenTitle = stringResource(id = R.string.sign_up)
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = screenTitle) }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logo or app name
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .padding(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "DuckIt",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Create an account",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Email field
                OutlinedTextField(
                    value = email,
                    onValueChange = onEmailChanged,
                    label = { Text("Email") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password field
                OutlinedTextField(
                    value = password,
                    onValueChange = onPasswordChanged,
                    label = { Text("Password") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Confirm Password field
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = onConfirmPasswordChanged,
                    label = { Text("Confirm Password") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                            onSignUpClicked()
                        }
                    ),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Error message
                if (uiState is AuthState.Error) {
                    Text(
                        text = uiState.message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Sign up button
                Button(
                    onClick = onSignUpClicked,
                    enabled = email.isNotBlank() && password.isNotBlank() &&
                            confirmPassword.isNotBlank() && uiState !is AuthState.Loading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (uiState is AuthState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Sign Up")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Already have an account?")
                    TextButton(onClick = onSignInClicked) {
                        Text("Sign In")
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun SignUpScreenContentPreview() {
    SignUpScreenContent(
        uiState = AuthState.Unauthenticated,
        email = "",
        password = "",
        confirmPassword = "",
        onEmailChanged = {},
        onPasswordChanged = {},
        onConfirmPasswordChanged = {},
        onSignUpClicked = {},
        onSignInClicked = {}
    )
}