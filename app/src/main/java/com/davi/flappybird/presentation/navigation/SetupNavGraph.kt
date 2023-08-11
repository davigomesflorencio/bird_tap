package com.davi.flappybird.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.davi.flappybird.presentation.screen.flappy.FlappyBirdScreen
import com.davi.flappybird.presentation.screen.splash.AnimatedSplashScreen

@Composable
fun SetupNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH_SCREEN
    ) {
        composable(route = Routes.SPLASH_SCREEN) {
            AnimatedSplashScreen(navController = navController)
        }
        composable(route = Routes.GAME_SCREEN) {
            FlappyBirdScreen()
        }
    }
}