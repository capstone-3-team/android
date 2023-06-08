package com.knu.quickthink

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.knu.quickthink.navigation.MainDestination
import kotlinx.coroutines.CoroutineScope
import timber.log.Timber

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterialNavigationApi::class)
@Composable
fun rememberQuickThinkAppState(
    navController: NavHostController = rememberNavController(),
    sheetState : ModalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
        confirmValueChange = { it != ModalBottomSheetValue.HalfExpanded }
    ),
    bottomSheetNavigator :BottomSheetNavigator = remember{
        BottomSheetNavigator(sheetState)
    },
    coroutineScope :CoroutineScope = rememberCoroutineScope(),
) = remember(navController,bottomSheetNavigator,coroutineScope,sheetState){
    QuickThinkAppState(navController,bottomSheetNavigator,coroutineScope,sheetState)
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterialNavigationApi::class)
class QuickThinkAppState(
    val navController: NavHostController,
    val bottomSheetNavigator :BottomSheetNavigator,
    val coroutineScope :CoroutineScope,
    val sheetState : ModalBottomSheetState,
    val isMainRoute : MutableState<Boolean> = mutableStateOf(false),
    val menuExpanded : MutableState<Boolean> = mutableStateOf(false),
) {
    fun addDestinationChangedListener(
    ){
        val callback = NavController.OnDestinationChangedListener { _, destination, _ ->
            if(!isMainRoute.value && destination.route!!.contains("main")) {
                isMainRoute.value = true
                Timber.tag("navigation").d("isMainRoute = true")
            }else if(isMainRoute.value && !destination.route!!.contains("main")){
                isMainRoute.value = false
            }
        }
        navController.addOnDestinationChangedListener(callback)
    }

    val currentRoute: String?
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination?.route

    val isMyFeedScreen : Boolean
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination?.route == MainDestination.FEED_ROUTE
}

@ExperimentalMaterialNavigationApi
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun rememberBottomSheetNavigator(
    animationSpec: AnimationSpec<Float> = SwipeableDefaults.AnimationSpec
): BottomSheetNavigator {
    val sheetState = rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden,
        animationSpec,
        confirmValueChange = { it != ModalBottomSheetValue.Hidden }
    )
    return remember(sheetState) {
        BottomSheetNavigator(sheetState = sheetState)
    }
}