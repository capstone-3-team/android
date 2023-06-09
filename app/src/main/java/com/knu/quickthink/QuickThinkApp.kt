package com.knu.quickthink

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.knu.quickthink.components.QuickThinkTopAppBar
import com.knu.quickthink.navigation.MainDestination
import com.knu.quickthink.screens.SplashScreen
import com.knu.quickthink.screens.main.*
import com.knu.quickthink.ui.theme.QuickThinkTheme
import kotlinx.coroutines.launch
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.bottomSheet
import com.knu.quickthink.components.FABContent
import com.knu.quickthink.screens.account.AccountScreen
import com.knu.quickthink.screens.card.CardEditScreen
import com.knu.quickthink.screens.feed.OthersFeedScreen
import com.knu.quickthink.screens.login.LoginScreen
import timber.log.Timber

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterialNavigationApi::class,ExperimentalComposeUiApi::class)
@Composable
fun QuickThinkApp(
    appState: QuickThinkAppState = rememberQuickThinkAppState(),
    mainViewModel: MainViewModel = hiltViewModel(),
) {
    val userState by mainViewModel.userState.collectAsState()
    var firstRendering by remember { mutableStateOf(true) }
    Timber.d("userState : ${userState}")
    Timber.d("firstRendering : ${firstRendering}")
    LaunchedEffect(userState){
        if(!userState.isLoggedIn && !firstRendering){
            appState.navController.navigate(
                MainDestination.LOGIN_ROUTE,
                navOptions {
                    popUpTo(MainDestination.LOGIN_ROUTE){inclusive = true}
                }
            )
        }else firstRendering = false
    }
//    val isLogOutSuccess by mainViewModel.isLogOutSuccess.collectAsState()
//    LaunchedEffect(isLogOutSuccess){
//        if(isLogOutSuccess){
//            appState.navController.navigate(
//                MainDestination.LOGIN_ROUTE,
//                navOptions {
//                    popUpTo(MainDestination.LOGIN_ROUTE){inclusive = true}
//                }
//            )
//            mainViewModel.logOutFinish()
//        }
//    }
    QuickThinkTheme {
        appState.navController.navigatorProvider += appState.bottomSheetNavigator
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
                        onSignOutClicked = { mainViewModel.logout() },
                        onAccountClicked = { appState.navController.navigate(MainDestination.ACCOUNT_ROUTE) },
                    )
                },
                floatingActionButton = {
                    if (appState.isMyFeedScreen) {
                        FABContent {
                            val cardId : Long = -1
                            appState.navController.navigate("${MainDestination.CARD_EDIT_ROUTE}/$cardId")
                        }
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
                                    appState.navController.navigate("${MainDestination.CARD_VIEW_ROUTE}/$it")
                                },
                                onCardEditClick = {
                                    appState.navController.navigate("${MainDestination.CARD_EDIT_ROUTE}/$it")
                                }
                            )
                        }
                        composable(
                            route = "${MainDestination.OTHERS_FEED_ROUTE}/{id}",
                            arguments = listOf(navArgument("id"){ type= NavType.StringType }),
                        ) {
                            val othersGoogleId = it.arguments?.getString("id") ?:""
                            OthersFeedScreen(othersGoogleId = othersGoogleId)
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
                            ChatGptScreen()
                        }
                        bottomSheet(route = MainDestination.SERACH_ROUTE) {
                            BoxWithConstraints() {
                                val sheetHeight = this.constraints.maxHeight * 0.8f
                                Box(modifier = Modifier.height(with(LocalDensity.current) { sheetHeight.toDp() })) {
                                    UserSearchScreen(
                                        navigateToUserFeedScreen = {
                                            appState.navController.navigate("${MainDestination.OTHERS_FEED_ROUTE}/$it")
                                        },
                                        onCloseClicked = {
                                            appState.navController.popBackStack()
                                        }
                                    )
                                }
                            }
                        }
                        dialog(
                            route = "${MainDestination.CARD_VIEW_ROUTE}/{cardId}",
                            arguments = listOf(navArgument("cardId"){ type= NavType.LongType }),
                            dialogProperties = DialogProperties(usePlatformDefaultWidth = false)
                        ){
                            val cardId = it.arguments?.getLong("cardId") ?: -1
                            Surface(
                            modifier = Modifier
                                .width(400.dp)
                                .height(600.dp)
                                .padding(10.dp)
                                .clip(RoundedCornerShape(20.dp))
                            ) {
                                CardReviewScreen( cardId,
                                    onEditBtnClicked = {
                                        appState.navController.navigate("${MainDestination.CARD_EDIT_ROUTE}/$cardId")
                                    },
                                    onCloseBtnClicked = {appState.navController.navigate(MainDestination.FEED_ROUTE)}
                                )
                            }
                        }
                        composable(
                            route = "${MainDestination.CARD_EDIT_ROUTE}/{cardId}",
                            arguments = listOf(navArgument("cardId"){ type= NavType.LongType })
                        ){
                            val cardId = it.arguments?.getLong("cardId") ?: -1
                            CardEditScreen(
                                cardId = cardId,
                                onBackClicked = {appState.navController.navigate(MainDestination.MAIN_ROUTE)},
                                onDoneClicked = {appState.navController.navigate(MainDestination.MAIN_ROUTE)})
                        }
                    }
                }
            }
        }
    }
}
