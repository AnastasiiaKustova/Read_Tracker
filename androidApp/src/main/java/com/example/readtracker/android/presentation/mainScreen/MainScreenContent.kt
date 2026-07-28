package com.example.readtracker.android.presentation.mainScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainScreenContent(component: MainScreenComponent) {
    MainScreen(
        onCloseAppClick = { component.onCloseAppClick() },
    )
}

@Composable
private fun MainScreen(
    onCloseAppClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {


        Text(
            color = Color.White,
            fontSize = (0.06f * LocalConfiguration.current.screenHeightDp).sp,
            fontWeight = FontWeight.Bold,
            text = "Test"
        )

        Spacer(modifier = Modifier.height(20.dp))

    }
}