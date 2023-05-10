package com.knu.quickthink

import android.graphics.drawable.shapes.Shape
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Animatable
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
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
import androidx.navigation.plusAssign
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.knu.quickthink.components.QuickThinkTopAppBar
import com.knu.quickthink.navigation.MainDestination
import com.knu.quickthink.screens.LoginScreen
import com.knu.quickthink.screens.SplashScreen
import com.knu.quickthink.screens.main.*
import com.knu.quickthink.ui.theme.QuickThinkTheme
import com.knu.quickthink.ui.theme.Shapes
import kotlinx.coroutines.launch
import timber.log.Timber
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.bottomSheet

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterialNavigationApi::class)
@Composable
fun QuickThinkApp(appState: QuickThinkAppState = rememberQuickThinkAppState()) {
    QuickThinkTheme {
        appState.navController.navigatorProvider += appState.bottomSheetNavigator

        ModalBottomSheetLayout(
            bottomSheetNavigator = appState.bottomSheetNavigator,
            sheetShape = RoundedCornerShape(20.dp)
        ) {
            Scaffold(
                topBar = {
                    if (appState.isMainRoute.value) {
                        QuickThinkTopAppBar(
                            onSearchClicked = {
                                appState.navController.navigate(MainDestination.SERACH_ROUTE)
                                appState.coroutineScope.launch {
                                }
                            },
                            onChatGPTClicked = { appState.navController.navigate(MainDestination.CHATGPT_ROUTE) },
                            onSignOutClicked = { appState.navController.navigate(MainDestination.LOGIN_ROUTE) },
                            onAccountClicked = { appState.navController.navigate(MainDestination.ACCOUNT_ROUTE) }
                        )
                    }
                }
            ) { innerPaddingModifier ->
                LaunchedEffect(appState.navController) {
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
                    composable(route = MainDestination.LOGIN_ROUTE) {
                        LoginScreen(
                            onLoginClicked = { appState.navController.navigate(MainDestination.MAIN_ROUTE) },
                            onSignUpClicked = { appState.navController.navigate(MainDestination.MAIN_ROUTE) }
                        )
                    }
                    navigation(
                        route = MainDestination.MAIN_ROUTE,
                        startDestination = MainDestination.FEED_ROUTE
                    ) {
                        composable(route = MainDestination.FEED_ROUTE) {
                            FeedScreen()
                        }
                        composable(route = MainDestination.ACCOUNT_ROUTE) {
                            AccountScreen()
                        }
                        composable(route = MainDestination.CHATGPT_ROUTE) {
                            ChatGPTScreen()
                        }
                        bottomSheet(route = MainDestination.SERACH_ROUTE) {
                            BoxWithConstraints() {
                                val sheetHeight = this.constraints.maxHeight * 0.8f
                                Box(modifier = Modifier.height(with(LocalDensity.current) { sheetHeight.toDp() })) {
                                    SearchScreen()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


//        if(appState.sheetState.isVisible){
//            Timber.tag("BottomSheet").d("sheetState.isVisible true")
//        }
//        BackHandler(enabled = appState.sheetState.isVisible) {
//            Timber.tag("BottomSheet").d("BottomSheet BackHandler 작동")
//            appState.coroutineScope.launch { appState.sheetState.hide() }
//        }
//        BoxWithConstraints {
//            val sheetHeight = this.constraints.maxHeight * 0.8f
//            ModalBottomSheetLayout(
//                sheetState = appState.sheetState,
//                sheetContent = {
//                    Box(modifier = Modifier.height(with(LocalDensity.current){sheetHeight.toDp()})){
//                        SearchScreen()
//                    }
//                },
//                modifier = Modifier.fillMaxSize(),
//                sheetShape = RoundedCornerShape(20.dp)
//            ) {
//                Scaffold(
//                    topBar = {
//                        if (appState.isMainRoute.value) {
//                            QuickThinkTopAppBar(
//                                onSearchClicked = {
//                                    appState.coroutineScope.launch {
//                                        appState.sheetState.animateTo(ModalBottomSheetValue.Expanded)
//                                    }
//                                },
//                                onChatGPTClicked = { appState.navController.navigate(MainDestination.CHATGPT_ROUTE) },
//                                onSignOutClicked = { appState.navController.navigate(MainDestination.LOGIN_ROUTE) },
//                                onAccountClicked = { appState.navController.navigate(MainDestination.ACCOUNT_ROUTE) }
//                            )
//                        }
//                    }
//                ) { innerPaddingModifier ->
//                    LaunchedEffect(appState.navController) {
//                        appState.addDestinationChangedListener()
//                    }
//                    NavHost( /* Root NavHost */
//                        navController = appState.navController,
//                        startDestination = MainDestination.SPlASH_ROUTE,
//                        modifier = Modifier
//                            .padding(innerPaddingModifier)
//                    ) {
//                        composable(route = MainDestination.SPlASH_ROUTE) {
//                            SplashScreen(navController = appState.navController)
//                        }
//                        composable(route = MainDestination.LOGIN_ROUTE) {
//                            LoginScreen(
//                                onLoginClicked = { appState.navController.navigate(MainDestination.MAIN_ROUTE) },
//                                onSignUpClicked = { appState.navController.navigate(MainDestination.MAIN_ROUTE) }
//                            )
//                        }
//                        navigation(
//                            route = MainDestination.MAIN_ROUTE,
//                            startDestination = MainDestination.FEED_ROUTE
//                        ) {
//                            composable(route = MainDestination.FEED_ROUTE) {
//                                FeedScreen()
//                            }
//                            composable(route = MainDestination.ACCOUNT_ROUTE) {
//                                AccountScreen()
//                            }
//                            composable(route = MainDestination.CHATGPT_ROUTE) {
//                                ChatGPTScreen()
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
