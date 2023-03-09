package com.example.composechatkiwi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.composechatkiwi.ui.theme.ComposeChatKiwiTheme
import com.example.composekiwi.ChatScreen
import com.example.composekiwi.LoginScreen
import com.example.composekiwi.RegistrationScreen
import com.example.composekiwi.Users

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ComposeChatKiwiTheme {
                val navController = rememberNavController()
                NavigationAppHost(navController = navController)

            }
        }
    }
}

@Composable
fun NavigationAppHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Destination.Register.route) {

        composable(Destination.Main.route) { Users(navHostController = navController) }
        composable(Destination.Register.route) { RegistrationScreen(navController) }
        composable(Destination.Login.route) { LoginScreen(navHostController = navController) }
        composable(
            "${Destination.Chat.route}/{userID}/{email}",
            arguments = listOf(
                navArgument("userID") { type = NavType.StringType },
                navArgument("email") { type = NavType.StringType }
            )
        ) {
            val userId = it.arguments?.getString("userID")!!
            val email = it.arguments?.getString("email")!!
            ChatScreen(userId, email)
        }
    }
}


sealed class Destination(val route: String) {
    object Main : Destination("main")
    object Register : Destination("register")
    object Chat : Destination("chat")
    object Login : Destination("login")
}

