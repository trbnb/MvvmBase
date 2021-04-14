package de.trbnb.apptemplate.app

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import de.trbnb.apptemplate.list.ListScreen
import de.trbnb.apptemplate.main.MainScreen
import de.trbnb.apptemplate.second.SecondScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "main") {
        composable("main") { MainScreen(navController) }
        composable("second") { SecondScreen() }
        composable("list") { ListScreen() }
    }
}