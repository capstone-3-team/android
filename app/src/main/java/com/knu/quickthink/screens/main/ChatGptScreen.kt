package com.knu.quickthink.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ChatGptScreen(
    viewModel: ChatGptViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var question by remember {mutableStateOf("")}

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .wrapContentSize(Alignment.Center)
    ) {
        TextField(
            value = question,
            onValueChange = { question = it }
        )
        Button(onClick = {viewModel.askToGpt(question)}) {
            Text(text = "눌러랑")
        }
    }
}