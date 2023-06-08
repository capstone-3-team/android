package com.knu.quickthink.screens.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.knu.quickthink.R
import com.knu.quickthink.screens.search.UserInfo
import com.knu.quickthink.screens.search.UserSearchViewModel
import com.knu.quickthink.screens.search.testUserInfoList
import kotlinx.coroutines.flow.lastOrNull


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserSearchScreen(
    viewModel: UserSearchViewModel = hiltViewModel(),
    onCloseClicked: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(id = R.dimen.vertical_margin))
    ) {
        BottomSheetHeader(onCloseClicked = onCloseClicked)
        UserSearchBar(
            users = uiState.users,
            onQueryChange = {},
        )
        Text(
            text = "Bottom sheet",
            style = MaterialTheme.typography.h6
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Click outside the bottom sheet to hide it",
            style = MaterialTheme.typography.body1
        )
    }
}

@ExperimentalMaterial3Api
@Composable
fun UserSearchBar(
    users : List<UserInfo>,
    onQueryChange: (String) -> Unit,
) {
    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
    SearchBar(
        modifier = Modifier.fillMaxWidth(),
        query = query,
        onQueryChange = {
            query = it
            onQueryChange(it)
        },
        onSearch = {},
        active = active,
        onActiveChange = { active = it },
        placeholder = {
            Text(text = "유저 ID를 입력하세요")
        },
        leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = null)
        },
        trailingIcon = {
            if (active) {
                Icon(
                    modifier = Modifier.clickable {
                        if (query.isNotEmpty()) {
                            query = ""
                        } else {
                            active = false
                        }
                    },
                    imageVector = Icons.Default.Close, contentDescription = null
                )
            }
        }
    ) {
        users.forEach {
            Row(
                modifier = Modifier.padding(dimensionResource(id = R.dimen.vertical_margin))
            ){
                Icon(imageVector = Icons.Default.Person,contentDescription = null)
                Text(text = it.googleName)
            }
        }
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(
                onClick = onCloseClicked,
                modifier = Modifier
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "close",
                )
            }
        }
    }
}

@Preview
@Composable
fun SearchScreenPrev() {
    Surface {
        UserSearchScreen(
            onCloseClicked = { }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun UserSearchBarPrev() {
    Surface {
        UserSearchBar(
            users = testUserInfoList ,
            onQueryChange = {})
    }
}