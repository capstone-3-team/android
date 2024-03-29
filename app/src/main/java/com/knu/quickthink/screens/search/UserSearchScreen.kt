package com.knu.quickthink.screens.main

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.knu.quickthink.R
import com.knu.quickthink.components.profileImage
import com.knu.quickthink.model.user.testUserList
import com.knu.quickthink.screens.search.UserInfo
import com.knu.quickthink.screens.search.UserSearchViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserSearchScreen(
    viewModel: UserSearchViewModel = hiltViewModel(),
    onCloseClicked: () -> Unit,
    navigateToUserFeedScreen : (String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.isSearchSuccess, uiState.message){
        if(uiState.isSearchSuccess)
            if(uiState.searchUserId.isNotEmpty()){
                navigateToUserFeedScreen(uiState.searchUserId)
            }
            else{
                Toast.makeText(context,"해당 유저의 ID를 찾을 수 없습니다",Toast.LENGTH_SHORT).show()
            }
        if(uiState.message.isNotEmpty()){
            Toast.makeText(context,uiState.message,Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = dimensionResource(id = R.dimen.vertical_margin))
            .padding(horizontal = dimensionResource(id = R.dimen.horizontal_margin) )
    ) {
        BottomSheetHeader(onCloseClicked = onCloseClicked)
        UserSearchBar(
            onQueryChange = { viewModel.searchUsers(it)},
        )
        UserSearchResults(
            users = uiState.users,
            onUserClicked = {
                viewModel.searchUser(it)
            }
        )
    }
}

@Composable
fun UserSearchResults(
    users : List<UserInfo>,
    onUserClicked : (UserInfo) -> Unit
) {
    LazyColumn(
        modifier = Modifier.padding(top = dimensionResource(id = R.dimen.vertical_margin))
    ){
        itemsIndexed(users) { index, user ->
            Row(
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.vertical_margin))
                    .clickable { onUserClicked(user) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                profileImage(
                    imageUrl = user.profilePicture,
                    imageSize = dimensionResource(id = R.dimen.userProfile_image),
                )
                Text(
                    modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.horizontal_margin)),
                    text = user.googleName,
                    fontSize = 18.sp
                )
                Text(
                    modifier = Modifier
                        .padding(horizontal = dimensionResource(id = R.dimen.horizontal_margin))
                        .weight(1f),
                    text = user.profileText ?: "",
                    fontSize = 15.sp,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun UserSearchBar(
    onQueryChange: (String) -> Unit,
) {
    var query by remember { mutableStateOf("") }
//    var active by remember { mutableStateOf(false) }
    val customTextSelectionColors = TextSelectionColors(
        handleColor = colorResource(id = R.color.quickThink_blue),
        backgroundColor = colorResource(id = R.color.quickThink_blue).copy(alpha = 0.4f)
    )
    CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
        SearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(dimensionResource(id = R.dimen.dialog_roundedCorner))),
            query = query,
            onQueryChange = {
                query = it
                onQueryChange(it)
            },
            onSearch = onQueryChange,
            active = false,
            onActiveChange = { },
            placeholder = {
                Text(text = "유저 ID를 입력하세요")
            },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = null)
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    Icon(
                        modifier = Modifier.clickable {
                            query = ""
                            onQueryChange(query)
                        },
                        imageVector = Icons.Default.Close, contentDescription = null
                    )
                }
            },
            colors = SearchBarDefaults.colors(
                containerColor = colorResource(id = R.color.quickThink_blue).copy(alpha = 0.3f),
                dividerColor = Color.Transparent,
                inputFieldColors = TextFieldDefaults.colors(
                    cursorColor = colorResource(id = R.color.quickThink_blue),
                    selectionColors = customTextSelectionColors
                )
            )

        ) {}
    }
}

@Composable
fun BottomSheetHeader(
    onCloseClicked: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "유저 검색",
            style = MaterialTheme.typography.h6
        )
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.End
//        ) {
//            IconButton(
//                onClick = onCloseClicked,
//                modifier = Modifier
//            ) {
//                Icon(
//                    imageVector = Icons.Filled.Close,
//                    contentDescription = "close",
//                )
//            }
//        }
    }
}

@Preview
@Composable
fun SearchScreenPrev() {
    Surface {
        UserSearchScreen(
            onCloseClicked = { },
            navigateToUserFeedScreen = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun UserSearchBarPrev() {
    Surface {
        UserSearchBar(
            onQueryChange = {})
    }
}