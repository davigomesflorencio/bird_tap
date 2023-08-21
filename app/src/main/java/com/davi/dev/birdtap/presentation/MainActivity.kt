package com.davi.dev.birdtap.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.davi.dev.birdtap.presentation.navigation.SetupNavGraph
import com.davi.dev.birdtap.presentation.screen.flappy.FlappyBirdTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlappyBirdTheme {
                val navController = rememberNavController()
                SetupNavGraph(navController = navController)
            }
        }
    }
}
