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
    sheetState : ModalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
        confirmValueChange = { it != ModalBottomSheetValue.HalfExpanded }
    ),
    feedScreenState : FeedScreenState = rememberFeedScreenState(),
    bottomSheetNavigator :BottomSheetNavigator = remember{
        BottomSheetNavigator(sheetState)
    },
    coroutineScope :CoroutineScope = rememberCoroutineScope(),
    isMainRoute : MutableState<Boolean> = remember { mutableStateOf(false)},
    isFeedRoute : MutableState<Boolean> = remember { mutableStateOf(false)},
    menuExpanded : MutableState<Boolean> = remember { mutableStateOf(false)},
) = remember(navController,bottomSheetNavigator,coroutineScope,sheetState,feedScreenState,isMainRoute,menuExpanded){
    QuickThinkAppState(navController,bottomSheetNavigator,coroutineScope,sheetState,feedScreenState,isMainRoute,isFeedRoute,menuExpanded)
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterialNavigationApi::class)
class QuickThinkAppState(
    val navController: NavHostController,
    val bottomSheetNavigator :BottomSheetNavigator,
    val coroutineScope :CoroutineScope,
    val sheetState : ModalBottomSheetState,
    val feedScreenState : FeedScreenState,
    val isMainRoute : MutableState<Boolean>,
    val isFeedRoute : MutableState<Boolean>,
    val menuExpanded : MutableState<Boolean>,
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
            isFeedRoute.value = destination.route!! == MainDestination.FEED_ROUTE
        }
        navController.addOnDestinationChangedListener(callback)
    }

    val currentRoute: String?
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination?.route

    val isMyFeedScreen : Boolean
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination?.route == MainDestination.FEED_ROUTE
}

class FeedScreenState (
    val hashTagListState : LazyListState,
    val feedListState : LazyListState
)

@Composable
fun rememberFeedScreenState() : FeedScreenState{
    return FeedScreenState(
        hashTagListState = rememberLazyListState(),
        feedListState = rememberLazyListState()
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