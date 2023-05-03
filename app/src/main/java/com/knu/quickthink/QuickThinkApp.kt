package com.knu.quickthink

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.knu.quickthink.components.QuickThinkTopAppBar
import com.knu.quickthink.navigation.MainDestination
import com.knu.quickthink.screens.LoginScreen
import com.knu.quickthink.screens.main.FeedScreen
import com.knu.quickthink.screens.SplashScreen
import com.knu.quickthink.screens.main.AccountScreen
import com.knu.quickthink.screens.main.ChatGPTScreen
import com.knu.quickthink.ui.theme.QuickThinkTheme

@Composable
fun QuickThinkApp(appState: QuickThinkAppState = rememberQuickThinkAppState()) {
    QuickThinkTheme {
        Scaffold(
            topBar ={
                if (appState.isMainRoute.value){
                    QuickThinkTopAppBar(
                        onSearchClicked = {/*TODO */},
                        onChatGPTClicked = {appState.navController.navigate(MainDestination.CHATGPT_ROUTE)},
                        onSignOutClicked = {appState.navController.navigate(MainDestination.LOGIN_ROUTE)},
                        onAccountClicked = {appState.navController.navigate(MainDestination.ACCOUNT_ROUTE)}
                    )
                }
            }

        ){ innerPaddingModifier ->
            LaunchedEffect(appState.navController){
                appState.addDestinationChangedListener()
            }
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
                    composable(route = MainDestination.ACCOUNT_ROUTE){
                        AccountScreen()
                    }
                    composable(route = MainDestination.CHATGPT_ROUTE){
                        ChatGPTScreen()
                    }
                    composable(route = MainDestination.CHATGPT_ROUTE){
                        ChatGPTScreen()
                    }
                }
            }

        }

    }
}
