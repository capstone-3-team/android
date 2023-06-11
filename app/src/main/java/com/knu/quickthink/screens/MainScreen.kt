package com.knu.quickthink.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.*
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
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.bottomSheet
import com.knu.quickthink.FeedScreenState
import com.knu.quickthink.MainScreenState
import com.knu.quickthink.R
import com.knu.quickthink.components.FABContent
import com.knu.quickthink.components.QuickThinkTopAppBar
import com.knu.quickthink.navigation.MainDestination
import com.knu.quickthink.rememberMainScreenState
import com.knu.quickthink.screens.account.AccountScreen
import com.knu.quickthink.screens.card.CardEditScreen
import com.knu.quickthink.screens.feed.OthersFeedScreen
import com.knu.quickthink.screens.main.*
import timber.log.Timber

@OptIn(ExperimentalMaterialNavigationApi::class)
@Composable
fun MainScreen(
    mainScreenState : MainScreenState,
    mainViewModel: MainViewModel = hiltViewModel(),
) {
    val navController = mainScreenState.navController
    val userState by mainViewModel.userState.collectAsState()
    val feedScreenState = mainScreenState.feedScreenState
    var firstRendering by remember { mutableStateOf(true) }

    LaunchedEffect(userState){
        if(!userState.isLoggedIn && !firstRendering){
            mainScreenState.navController.navigate(
                MainDestination.LOGIN_ROUTE,
                navOptions {
                    popUpTo(MainDestination.LOGIN_ROUTE){inclusive = true}
                }
            )
        }else firstRendering = false
    }

    navController.navigatorProvider += mainScreenState.bottomSheetNavigator
    ModalBottomSheetLayout(
        bottomSheetNavigator = mainScreenState.bottomSheetNavigator,
        sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        LaunchedEffect(Unit) {
            mainScreenState.addDestinationChangedListener()
        }
        Scaffold(
            topBar = {
                QuickThinkTopAppBar(
                    isMainRoute = feedScreenState.isMainRoute.value,
                    menuExpanded = feedScreenState.menuExpanded,
                    onLogoClicked = {
                        if(!feedScreenState.isFeedRoute.value){
                            navController.navigate(MainDestination.MAIN_ROUTE)
                        }
                    },
                    onSearchClicked = {
                        navController.navigate(MainDestination.SERACH_ROUTE)
                    },
                    onChatGPTClicked = { navController.navigate(MainDestination.CHATGPT_ROUTE) },
                    onSignOutClicked = { mainViewModel.logout() },
                    onAccountClicked = { navController.navigate(MainDestination.ACCOUNT_ROUTE) },
                )
            },
            floatingActionButton = {
                if (feedScreenState.isFeedRoute.value) {
                    FABContent {
                        val cardId : Long = -1
                        navController.navigate("${MainDestination.CARD_EDIT_ROUTE}/$cardId")
                    }
                }
            }
        ) { innerPaddingModifier ->
            NavHost(
                navController = navController,
                route = MainDestination.MAIN_ROUTE,
                startDestination = MainDestination.FEED_ROUTE,
                modifier = Modifier.padding(innerPaddingModifier)
            ){
                composable(route = MainDestination.FEED_ROUTE) { navBackStackEntry ->
                    FeedScreen(
                        feedScreenState = mainScreenState.feedScreenState,
                        onCardClick = {
                            navController.navigate("${MainDestination.CARD_VIEW_ROUTE}/$it")
                        },
                        onCardEditClick = {
                            navController.navigate("${MainDestination.CARD_EDIT_ROUTE}/$it")
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
                            .height(400.dp)
                            .padding(dimensionResource(id = R.dimen.vertical_margin))
                            .clip(RoundedCornerShape(dimensionResource(id = R.dimen.dialog_roundedCorner)))
                    ) {
                        AccountScreen(
                            onCloseClicked = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
                composable(route = MainDestination.CHATGPT_ROUTE) {
                    ChatGptScreen(
                        onBackPressed = {navController.popBackStack()}
                    )
                }
                bottomSheet(route = MainDestination.SERACH_ROUTE) {
                    BoxWithConstraints() {
                        val sheetHeight = this.constraints.maxHeight * 0.8f
                        Box(modifier = Modifier.height(with(LocalDensity.current) { sheetHeight.toDp() })) {
                            UserSearchScreen(
                                navigateToUserFeedScreen = {
                                    navController.navigate("${MainDestination.OTHERS_FEED_ROUTE}/$it")
                                },
                                onCloseClicked = {
                                    navController.popBackStack()
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
                                navController.navigate("${MainDestination.CARD_EDIT_ROUTE}/$cardId")
                            },
                            onCloseBtnClicked = {navController.popBackStack()}
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
                        onBackClicked = {navController.popBackStack()},
                        onDoneClicked = {navController.navigate(MainDestination.MAIN_ROUTE)})
                }
            }
        }
    }

}