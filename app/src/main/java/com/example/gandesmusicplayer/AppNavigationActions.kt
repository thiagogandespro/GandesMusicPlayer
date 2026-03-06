package com.example.gandesmusicplayer

import androidx.navigation.NavController
import com.example.gandesmusicplayer.Destinations.PLAY_BACK_SCREEN
import com.example.gandesmusicplayer.Destinations.PLAY_LIST_SCREEN

object Destinations{
    const val PLAY_BACK_SCREEN = "playBlackScreen"
    const val PLAY_LIST_SCREEN = "playListScreen"
}

class AppNavigationActions(private val navController: NavController) {
    fun navigateToPlayListScreen(){
        navController.navigate(PLAY_BACK_SCREEN)
    }

    fun navigateToPlayBackScreen(){
        navController.navigate(PLAY_LIST_SCREEN)
    }

}