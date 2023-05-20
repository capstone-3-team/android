package com.knu.quickthink

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import kotlinx.coroutines.CoroutineScope
import timber.log.Timber

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterialNavigationApi::class)
@Composable
fun rememberQuickThinkAppState(
    navController: NavHostController = rememberNavController(),
    bottomSheetNavigator :BottomSheetNavigator = rememberBottomSheetNavigator(),
    coroutineScope :CoroutineScope = rememberCoroutineScope(),
    sheetState : ModalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
//        confirmStateChange = { it != ModalBottomSheetValue.Hidden }
    )
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

}