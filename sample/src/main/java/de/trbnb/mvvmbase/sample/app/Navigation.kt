package de.trbnb.mvvmbase.sample.app

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import de.trbnb.mvvmbase.sample.list.ListScreen
import de.trbnb.mvvmbase.sample.main.MainScreen
import de.trbnb.mvvmbase.sample.second.SecondScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "main") {
        composable("main") { MainScreen(navController) }
        composable("second") { SecondScreen() }
        composable("list") { ListScreen() }
    }
}