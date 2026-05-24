package com.example.gandesmusicplayer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppModalDrawer(
    drawerState: DrawerState,
    navigationActions: AppNavigationActions,
    modifier: Modifier = Modifier,
    openDrawer: () -> Unit,
    closeDrawer: () -> Unit,
    appContent: @Composable (modifier: Modifier) -> Unit,
){
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawerContent(
                navigateToPlayBlackScreen = {
                    closeDrawer()
                    navigationActions.navigateToPlayBackScreen()
                },
                navigateToPlayListScreen = {
                    closeDrawer()
                    navigationActions.navigateToPlayListScreen()
               } ,
            )
        }
    ){
        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = openDrawer) {
                            Icon(Icons.Filled.Menu, "Open Drawer", tint = Color.Black)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        ) { paddingValues ->
            appContent(modifier.padding(paddingValues))
        }
    }
}

@Composable
private fun AppDrawerContent(
    modifier: Modifier = Modifier,
    navigateToPlayBlackScreen: () -> Unit = {},
    navigateToPlayListScreen: () -> Unit = {},
){
    Surface() {
        Column(
            modifier = modifier.fillMaxSize()
        ) {
            DrawerButton(isSelected = true, buttonDescription = "PlayBack Screen", onClick = navigateToPlayBlackScreen)
            DrawerButton(isSelected = false, buttonDescription = "PlayList", onClick = navigateToPlayListScreen)
        }
    }
}

@Composable
private fun DrawerButton(
    isSelected: Boolean,
    buttonDescription: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
){
    val tintColor = if (isSelected) {
        MaterialTheme.colorScheme.secondary
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    }
    TextButton(
        onClick = onClick,
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