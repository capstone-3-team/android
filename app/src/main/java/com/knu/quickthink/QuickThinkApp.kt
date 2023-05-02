package com.knu.quickthink

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.knu.quickthink.navigation.MainDestination
import com.knu.quickthink.screens.LoginScreen
import com.knu.quickthink.screens.FeedScreen
import com.knu.quickthink.screens.SplashScreen
import com.knu.quickthink.ui.theme.QuickThinkTheme

@Composable
fun QuickThinkApp(appState: QuickThinkAppState = rememberQuickThinkAppState()) {
    QuickThinkTheme {
        Scaffold(

        ){ innerPaddingModifier ->
            NavHost( /* Root NavHost */
                navController = appState.navController,
                startDestination = MainDestination.SPlASH_ROUTE,
                modifier = Modifier
                    .padding(innerPaddingModifier)
            ) {
                composable(route = MainDestination.SPlASH_ROUTE) {
                    SplashScreen(navController = appState.navController)
                }
                composable(route = MainDestination.LOGIN_ROUTE){
                    LoginScreen(
                        onLoginClicked = {appState.navController.navigate(MainDestination.MAIN_ROUTE)},
                        onSignUpClicked = {appState.navController.navigate(MainDestination.MAIN_ROUTE)}
                    )
                }

                navigation(
                    route = MainDestination.MAIN_ROUTE,
                    startDestination = MainDestination.FEED_ROUTE
                ){
                    composable(route = MainDestination.FEED_ROUTE){
                        FeedScreen()
                    }
                }
            }

        }

    }
}
