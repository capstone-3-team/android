package com.knu.quickthink

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navigation
import androidx.navigation.plusAssign
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.knu.quickthink.components.QuickThinkTopAppBar
import com.knu.quickthink.navigation.MainDestination
import com.knu.quickthink.screens.SplashScreen
import com.knu.quickthink.screens.main.*
import com.knu.quickthink.ui.theme.QuickThinkTheme
import kotlinx.coroutines.launch
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.bottomSheet
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.knu.quickthink.screens.login.GoogleSignInViewModel
import com.knu.quickthink.screens.login.LoginScreen
import javax.inject.Inject

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterialNavigationApi::class,ExperimentalComposeUiApi::class)
@Composable
fun QuickThinkApp(
    appState: QuickThinkAppState = rememberQuickThinkAppState(),
    viewModel : GoogleSignInViewModel = hiltViewModel(),
) {
    QuickThinkTheme {
        val context = LocalContext.current as Activity
        appState.navController.navigatorProvider += appState.bottomSheetNavigator

//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)               // gso for logout
//            .requestIdToken(context.getString(R.string.gcp_web_client_id))
//            .requestEmail()
//            .build()
//        val googleSignInClient = context.let { GoogleSignIn.getClient(it, gso) }

        ModalBottomSheetLayout(
            bottomSheetNavigator = appState.bottomSheetNavigator,
            sheetShape = RoundedCornerShape(20.dp)
        ) {
            Scaffold(
                topBar = {
                    if (appState.isMainRoute.value) {
                        QuickThinkTopAppBar(
                            onLogoClicked = {
                                appState.navController.navigate(MainDestination.MAIN_ROUTE)
                            },
                            onSearchClicked = {
                                appState.navController.navigate(MainDestination.SERACH_ROUTE)
                                appState.coroutineScope.launch {
                                }
                            },
                            onChatGPTClicked = { appState.navController.navigate(MainDestination.CHATGPT_ROUTE) },
                            onSignOutClicked = {
                                viewModel.googleSignInClient.signOut().addOnCompleteListener(context){
                                    appState.navController.navigate(MainDestination.LOGIN_ROUTE)
                                    viewModel.logOut()
                                }
                            },
                            onAccountClicked = { appState.navController.navigate(MainDestination.ACCOUNT_ROUTE) },
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
                            onLoginSuccess = { appState.navController.navigate(MainDestination.MAIN_ROUTE) },
                            onSignUpClicked = { appState.navController.navigate(MainDestination.MAIN_ROUTE) }
                        )
                    }
                    navigation(
                        route = MainDestination.MAIN_ROUTE,
                        startDestination = MainDestination.FEED_ROUTE
                    ) {
                        composable(route = MainDestination.FEED_ROUTE) {
                            FeedScreen(
                                onCardClick = {
                                    appState.navController.navigate(MainDestination.CARD_VIEW_ROUTE)
                                }
                            )
                        }
                        dialog(
                            route = MainDestination.ACCOUNT_ROUTE,
                            dialogProperties = DialogProperties(usePlatformDefaultWidth = false)
                        ) {
                            Surface(
                                modifier = Modifier
                                    .width(400.dp)
                                    .wrapContentHeight()
                                    .padding(dimensionResource(id = R.dimen.vertical_margin))
                                    .clip(RoundedCornerShape(dimensionResource(id = R.dimen.dialog_roundedCorner)))
                                ) {
                                    AccountScreen(
                                        onCloseClicked = {
                                            appState.navController.popBackStack()
                                        }
                                    )
                                }
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
                        dialog(
                            route = MainDestination.CARD_VIEW_ROUTE,
                            dialogProperties = DialogProperties(usePlatformDefaultWidth = false)
                        ){
                            Surface(
                            modifier = Modifier
                                .width(400.dp)
                                .height(600.dp)
                                .padding(10.dp)
                                .clip(RoundedCornerShape(20.dp))
                            ) {
                                CardViewScreen()
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
