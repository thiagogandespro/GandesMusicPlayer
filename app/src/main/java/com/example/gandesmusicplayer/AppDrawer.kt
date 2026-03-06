package com.example.gandesmusicplayer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun AppModalDrawer(
    drawerState: DrawerState,
    navigationActions: AppNavigationActions,
    appContent: @Composable () -> Unit,
){
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawerContent(
                navigateToPlayBlackScreen = {navigationActions.navigateToPlayBackScreen()},
                navigateToPlayListScreen = {navigationActions.navigateToPlayListScreen()} ,
            )
        }
    ){
        appContent()
    }
}

@Composable
private fun AppDrawerContent(
    modifier: Modifier = Modifier,
    navigateToPlayBlackScreen: () -> Unit = {},
    navigateToPlayListScreen: () -> Unit = {},
){
    Surface(color = Color.Black) {
        Column(
            modifier = modifier.fillMaxSize()
        ) {
            DrawerButton(isSelected = true, buttonDescription = "PlayBack Screen")
            DrawerButton(isSelected = false, buttonDescription = "PlayList")
        }
    }
}

@Composable
private fun DrawerButton(
    isSelected: Boolean,
    buttonDescription: String,
    modifier: Modifier = Modifier
){
    val tintColor = if (isSelected) {
        MaterialTheme.colorScheme.secondary
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    }
    TextButton(
        onClick = {},
        modifier = modifier.fillMaxWidth()
    ) {
      Text(text = buttonDescription, color = tintColor)
    }
}

@Preview("App Drawer Content")
@Composable()
fun PreviewDrawerConten(){
    AppDrawerContent()
}