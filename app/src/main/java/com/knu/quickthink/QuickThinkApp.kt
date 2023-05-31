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
import androidx.navigation.navOptions
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
import com.knu.quickthink.screens.account.AccountScreen
import com.knu.quickthink.screens.card.CardEditScreen
import com.knu.quickthink.screens.login.LoginScreen

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterialNavigationApi::class,ExperimentalComposeUiApi::class)
@Composable
fun QuickThinkApp(
    appState: QuickThinkAppState = rememberQuickThinkAppState(),
    mainViewModel: MainViewModel = hiltViewModel()
) {
    QuickThinkTheme {
        val context = LocalContext.current as Activity
        appState.navController.navigatorProvider += appState.bottomSheetNavigator

        val isLogOutSuccess by mainViewModel.isLogOutSuccess.collectAsState()
        LaunchedEffect(isLogOutSuccess){
            if(isLogOutSuccess){
                appState.navController.navigate(
                    MainDestination.LOGIN_ROUTE,
                    navOptions {
                        popUpTo(MainDestination.LOGIN_ROUTE){inclusive = true}
                    }
                )
                mainViewModel.logOutFinish()
            }
        }

        ModalBottomSheetLayout(
            bottomSheetNavigator = appState.bottomSheetNavigator,
            sheetShape = RoundedCornerShape(20.dp)
        ) {
            Scaffold(
                topBar = {
                    QuickThinkTopAppBar(
                        isMainRoute = appState.isMainRoute.value,
                        menuExpanded = appState.menuExpanded,
                        onLogoClicked = { appState.navController.navigate(MainDestination.MAIN_ROUTE) },
                        onSearchClicked = {
                            appState.navController.navigate(MainDestination.SERACH_ROUTE)
                            appState.coroutineScope.launch {
                            }
                        },
                        onChatGPTClicked = { appState.navController.navigate(MainDestination.CHATGPT_ROUTE) },
                        onSignOutClicked = { mainViewModel.logOut() },
                        onAccountClicked = { appState.navController.navigate(MainDestination.ACCOUNT_ROUTE) },
                    )
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
//                            viewModel = googleSignInViewModel,
                            onLoginSuccess = { appState.navController.navigate(MainDestination.MAIN_ROUTE) },
                            onSignUpClicked = { appState.navController.navigate(MainDestination.MAIN_ROUTE) }
                        )
                    }
                    navigation(
                        route = MainDestination.MAIN_ROUTE,
                        startDestination = MainDestination.FEED_ROUTE
                    ) {
                        composable(route = MainDestination.FEED_ROUTE) { navBackStackEntry ->
                            FeedScreen(
                                onCardClick = {
                                    appState.navController.navigate(MainDestination.CARD_VIEW_ROUTE)
                                },
                                onCardEditClick = {
                                    appState.navController.navigate(MainDestination.CARD_EDIT_ROUTE)
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
                        composable(route = MainDestination.CARD_EDIT_ROUTE){
                            CardEditScreen(
                                onBackClicked = {appState.navController.navigate(MainDestination.MAIN_ROUTE)},
                                onDoneClicked = {appState.navController.navigate(MainDestination.MAIN_ROUTE)})
                        }
                    }
                }
            }
        }
    }
}
