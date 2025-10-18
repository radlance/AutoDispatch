package com.github.radlance.autodispatch.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
expect fun NavGraph(navController: NavHostController)