package com.knu.quickthink

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
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
    mainScreenState: MainScreenState = rememberMainScreenState(),
    coroutineScope :CoroutineScope = rememberCoroutineScope(),

) = remember(navController,mainScreenState,coroutineScope){
    QuickThinkAppState(navController,mainScreenState,coroutineScope)
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterialNavigationApi::class)
class QuickThinkAppState(
    val navController: NavHostController,
    val mainScreenState: MainScreenState,
    val coroutineScope :CoroutineScope,
) {
    val currentRoute: String?
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination?.route

    val isMyFeedScreen : Boolean
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination?.route == MainDestination.FEED_ROUTE
}
@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterialNavigationApi::class)
class MainScreenState(
    val navController: NavHostController,
    val feedScreenState: FeedScreenState,
    val sheetState: ModalBottomSheetState,
    val bottomSheetNavigator: BottomSheetNavigator
){
    fun addDestinationChangedListener(
    ){
        val isMainRoute = feedScreenState.isMainRoute
        val isFeedRoute = feedScreenState.isFeedRoute
        val callback = NavController.OnDestinationChangedListener { _, destination, _ ->
            if(!isMainRoute.value && destination.route!!.contains("main")) {
                isMainRoute.value = true
                Timber.tag("navigation").d("isMainRoute = true")
            }else if(isMainRoute.value && !destination.route!!.contains("main")){
                isMainRoute.value = false
            }
            isFeedRoute.value = destination.route!! == MainDestination.FEED_ROUTE
        }
        navController.addOnDestinationChangedListener(callback)
    }
}
@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterialNavigationApi::class)
@Composable
fun rememberMainScreenState(
    navController: NavHostController = rememberNavController(),
    feedScreenState : FeedScreenState = rememberFeedScreenState(),
    sheetState : ModalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
        confirmValueChange = { it != ModalBottomSheetValue.HalfExpanded }
    ),
    bottomSheetNavigator :BottomSheetNavigator = remember{
        BottomSheetNavigator(sheetState)
    },

) = remember(navController,feedScreenState,sheetState,bottomSheetNavigator){
    MainScreenState(navController,feedScreenState,sheetState,bottomSheetNavigator)
}
class FeedScreenState (
    val hashTagListState : LazyListState,
    val feedListState : LazyListState,
    val isMainRoute: MutableState<Boolean>,
    val isFeedRoute: MutableState<Boolean>,
    val menuExpanded : MutableState<Boolean>,
)

@Composable
fun rememberFeedScreenState() : FeedScreenState{
    return FeedScreenState(
        hashTagListState = rememberLazyListState(),
        feedListState = rememberLazyListState(),
        isMainRoute = remember { mutableStateOf(false)},
        isFeedRoute = remember { mutableStateOf(false)},
        menuExpanded = remember { mutableStateOf(false)},
    )
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