package com.example.gandesmusicplayer

import android.app.Application
import android.content.ComponentName
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.gandesmusicplayer.service.PlayBackService
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PlayerViewModel(application: Application): AndroidViewModel(application) {
    private val _controller = MutableStateFlow<MediaController?>(null)
    val controller: StateFlow<MediaController?> = _controller

    private var controllerFuture: ListenableFuture<MediaController>? = null

    fun initController(context: Context) {
        val sessionToken = SessionToken(
            context,
            ComponentName(context, PlayBackService::class.java)
        )

        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()

        controllerFuture?.addListener({
            _controller.value = controllerFuture?.get()
            _controller.value?.setMediaItems(providePlayList())
            _controller.value?.prepare()
        }, MoreExecutors.directExecutor())

    }

    fun releaseController() {
        controllerFuture?.let {
            MediaController.releaseFuture(it)
        }
        controllerFuture = null
        _controller.value = null
    }

    fun providePlayList():List<MediaItem> =
        listOf(
            MediaItem.fromUri("https://cdn.pixabay.com/audio/2025/09/29/audio_77a36612dd.mp3"),
            MediaItem.fromUri("https://cdn.pixabay.com/audio/2025/10/22/audio_b1ff57a7f3.mp3"),
            MediaItem.fromUri("https://cdn.pixabay.com/audio/2026/03/24/audio_586c5f0e9d.mp3"),
            MediaItem.fromUri("https://cdn.pixabay.com/audio/2026/03/29/audio_e7b410d7e3.mp3"),
            MediaItem.fromUri("https://cdn.pixabay.com/audio/2026/04/10/audio_74c357bc70.mp3"),
            MediaItem.fromUri("https://cdn.pixabay.com/audio/2026/04/10/audio_e79159e4e1.mp3"),
            MediaItem.fromUri("https://cdn.pixabay.com/audio/2026/04/10/audio_65bf7d6d97.mp3"),
            MediaItem.fromUri("https://cdn.pixabay.com/audio/2025/09/23/audio_1b6f4de1c4.mp3"),
            MediaItem.fromUri("https://cdn.pixabay.com/audio/2025/12/28/audio_4b7b5920b9.mp3"),
            MediaItem.fromUri("https://cdn.pixabay.com/audio/2025/11/17/audio_a86e69ef53.mp3"),
            MediaItem.fromUri("https://cdn.pixabay.com/audio/2025/12/18/audio_fcab73293f.mp3"),
            MediaItem.fromUri("https://cdn.pixabay.com/audio/2025/10/01/audio_e43b1a7255.mp3"),
            MediaItem.fromUri("https://cdn.pixabay.com/audio/2026/01/25/audio_1dd7f3126d.mp3"),
            MediaItem.fromUri("https://cdn.pixabay.com/audio/2025/08/29/audio_fba9035557.mp3"),
            MediaItem.fromUri("https://cdn.pixabay.com/audio/2025/09/30/audio_a59a02b883.mp3"),
        )
}