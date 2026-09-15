package com.cricpro.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.cricpro.app.presentation.navigation.CricProNavGraph
import com.cricpro.app.presentation.theme.CricProTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CricProTheme {
                val navController = rememberNavController()
                CricProNavGraph(navController = navController)
            }
        }
    }
}
