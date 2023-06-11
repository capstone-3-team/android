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
import com.knu.quickthink.screens.MainScreen
import com.knu.quickthink.screens.account.AccountScreen
import com.knu.quickthink.screens.card.CardEditScreen
import com.knu.quickthink.screens.feed.OthersFeedScreen
import com.knu.quickthink.screens.login.LoginScreen
import timber.log.Timber

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterialNavigationApi::class,ExperimentalComposeUiApi::class)
@Composable
fun QuickThinkApp(
    appState: QuickThinkAppState = rememberQuickThinkAppState(),
) {
    QuickThinkTheme {
        NavHost( /* Root NavHost */
            navController = appState.navController,
            startDestination = MainDestination.SPlASH_ROUTE,
            modifier = Modifier
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
            composable(route = MainDestination.MAIN_ROUTE){
                MainScreen(appState.mainScreenState)
            }
        }
    }
}
