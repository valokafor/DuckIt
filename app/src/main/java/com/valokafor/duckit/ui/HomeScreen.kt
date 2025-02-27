package com.valokafor.duckit.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.valokafor.duckit.ui.navigation.NavigationGraph
import com.valokafor.duckit.ui.theme.DuckItTheme

@Composable
fun HomeScreen(
    navHostController: NavHostController,
    finishActivity: () -> Unit
) {
    DuckItTheme {
        NavigationGraph(navHostController, finishActivity)
    }
}