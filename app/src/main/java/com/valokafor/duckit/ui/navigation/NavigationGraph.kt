package com.valokafor.duckit.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.valokafor.duckit.ui.addpost.AddNewPostScreen
import com.valokafor.duckit.ui.auth.SignInScreen
import com.valokafor.duckit.ui.auth.SignUpScreen
import com.valokafor.duckit.ui.postlist.PostListScreen

@Composable
fun NavigationGraph(
    navController: NavHostController,
    onFinish: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = PostListScreenInfo
    ) {
        composable<PostListScreenInfo> {
            PostListScreen(
                onNavigateBack = onFinish,
                onNavigateToAddPost = {
                    navController.navigate(AddNewPostScreenInfo)
                },
                onNavigateToLoginScreen = {
                    navController.navigate(SignInScreenInfo)
                }
            )
        }

        composable<AddNewPostScreenInfo> {
            AddNewPostScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToLoginScreen = {
                    navController.navigate(SignInScreenInfo)
                }
            )
        }

        composable<SignInScreenInfo> {
            SignInScreen(
                onSignInComplete = {
                    navController.popBackStack()
                },
                onNavigateToSignUp = {
                    navController.navigate(SignUpScreenInfo)
                }
            )
        }

        composable<SignUpScreenInfo> {
            SignUpScreen(
                onSignUpComplete = { email ->
                    navController.popBackStack()
                },
                onNavigateToLoginScreen = {
                    navController.navigate(SignInScreenInfo)
                }
            )
        }
    }
}