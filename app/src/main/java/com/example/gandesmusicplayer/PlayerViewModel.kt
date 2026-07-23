package com.example.gandesmusicplayer

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.gandesmusicplayer.service.PlayBackService
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class PlayerViewModel(application: Application): AndroidViewModel(application) {
    private val _controller = MutableStateFlow<MediaController?>(null)
    val controller: StateFlow<MediaController?> = _controller

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState

    private var controllerFuture: ListenableFuture<MediaController>? = null

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            updatePlayerState()
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            updatePlayerState()
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            updatePlayerState()
        }

        override fun onPlayerError(error: PlaybackException) {
            _uiState.update {
                it.copy(
                    errorMessage = error.message ?: error.errorCodeName,
                    playbackState = _controller.value?.playbackState ?: Player.STATE_IDLE
                )
            }
        }
    }

    fun initController(context: Context) {
        if (_controller.value != null) return

        val sessionToken = SessionToken(
            context,
            ComponentName(context, PlayBackService::class.java)
        )

        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()

        controllerFuture?.addListener({
            val controller = controllerFuture?.get() ?: return@addListener
            controller.addListener(playerListener)
            controller.setMediaItems(providePlayList())
            controller.prepare()
            _controller.value = controller
            updatePlayerState()
        }, MoreExecutors.directExecutor())

    }

    fun releaseController() {
        _controller.value?.removeListener(playerListener)
        controllerFuture?.let {
            MediaController.releaseFuture(it)
        }
        controllerFuture = null
        _controller.value = null
        _uiState.value = PlayerUiState()
    }

    fun playPause() {
        val controller = _controller.value ?: return
        if (controller.isPlaying) {
            controller.pause()
        } else {
            if (controller.playbackState == Player.STATE_IDLE) controller.prepare()
            controller.play()
        }
        updatePlayerState()
    }

    fun skipToPrevious() {
        _controller.value?.seekToPreviousMediaItem()
        updatePlayerState()
    }

    fun skipToNext() {
        _controller.value?.seekToNextMediaItem()
        updatePlayerState()
    }

    fun playFromPlaylist(index: Int) {
        val controller = _controller.value ?: return
        if (index !in 0 until controller.mediaItemCount) return

        controller.seekTo(index, 0L)
        if (controller.playbackState == Player.STATE_IDLE) controller.prepare()
        controller.play()
        updatePlayerState()
    }

    fun providePlayList(): List<MediaItem> = playList

    private fun updatePlayerState() {
        val controller = _controller.value
        _uiState.value = PlayerUiState(
            isControllerReady = controller != null,
            isPlaying = controller?.isPlaying == true,
            playbackState = controller?.playbackState ?: Player.STATE_IDLE,
            currentMediaItemIndex = controller?.currentMediaItemIndex ?: 0,
            errorMessage = _uiState.value.errorMessage,
            currentMediaItem = controller?.currentMediaItem,
        )
    }

    private val playList = listOf(
        createMediaItem(
            id = "pixabay-1",
            title = "Escape Your Love (Upbeat Fashion Pop Dance)",
            artist = "FASSounds",
            album = "Pixabay",
            uri = "https://cdn.pixabay.com/download/audio/2025/09/29/audio_77a36612dd.mp3?filename=fassounds-escape-your-love-upbeat-fashion-pop-dance-412230.mp3",
            artworkUri = "https://cdn.pixabay.com/audio/2025/09/29/07-04-43-418_200x200.jpg"
        ),
        createMediaItem(
            id = "pixabay-3",
            title = "Water | Afro-pop Music",
            artist = "kontraa",
            album = "Pixabay",
            uri = "https://cdn.pixabay.com/download/audio/2025/12/11/audio_249561b3fc.mp3?filename=kontraa-water-afro-pop-music-445661.mp3",
            artworkUri = "https://cdn.pixabay.com/audio/2025/12/02/11-15-11-894_200x200.jpg"
        ),
        createMediaItem(
            id = "pixabay-4",
            title = "Stomp Action Music",
            artist = "EnergySound",
            album = "Pixabay",
            uri = "https://cdn.pixabay.com/download/audio/2026/04/10/audio_02a364dca5.mp3?filename=energysound-stomp-action-music-513718.mp3",
            artworkUri = "https://cdn.pixabay.com/audio/2026/04/03/12-16-30-872_200x200.png"
        ),
        createMediaItem(
            id = "pixabay-5",
            title = "Action trailer promo rock",
            artist = "MagpieMusic",
            album = "Pixabay",
            uri = "https://cdn.pixabay.com/download/audio/2026/04/10/audio_65bf7d6d97.mp3?filename=magpiemusic-action-trailer-promo-rock-513687.mp3",
            artworkUri = "https://cdn.pixabay.com/audio/2026/04/03/11-37-07-840_200x200.jpg"
        ),
        createMediaItem(
            id = "pixabay-6",
            title = "Total War (Epic Action Cinematic Trailer Main)",
            artist = "AudioAtlant",
            album = "Pixabay",
            uri = "https://cdn.pixabay.com/download/audio/2026/04/10/audio_8470cb423f.mp3?filename=audioatlant-total-war-epic-action-cinematic-trailer-main-513668.mp3",
            artworkUri = "https://cdn.pixabay.com/audio/2026/04/03/11-14-19-328_200x200.png"
        ),
        createMediaItem(
            id = "pixabay-7",
            title = "Music Promotion No Copyright",
            artist = "MiroMaxMusic",
            album = "Pixabay",
            uri = "https://cdn.pixabay.com/download/audio/2026/04/10/audio_1c3b9bac1b.mp3?filename=miromaxmusic-music-promotion-no-copyright-513944.mp3",
            artworkUri = "https://cdn.pixabay.com/audio/2026/04/03/18-21-18-562_200x200.png"
        ),
        createMediaItem(
            id = "pixabay-8",
            title = "Charming Phonk I Free Background Music I Free Music Lab Release",
            artist = "FreeMusicLab",
            album = "Pixabay",
            uri = "https://cdn.pixabay.com/download/audio/2026/04/10/audio_4897785025.mp3?filename=freemusiclab-charming-phonk-i-free-background-music-i-free-music-lab-release-513626.mp3",
            artworkUri = "https://cdn.pixabay.com/audio/2026/04/03/10-29-56-943_200x200.png"
        ),
        createMediaItem(
            id = "pixabay-9",
            title = "Strong Character (powerful fuzz action sport rock",
            artist = "LightStockMusic",
            album = "Pixabay",
            uri = "https://cdn.pixabay.com/download/audio/2026/04/10/audio_5e1f52fabd.mp3?filename=lightstockmusic-strong-character-powerful-fuzz-action-sport-rock-513742.mp3",
            artworkUri = "https://cdn.pixabay.com/audio/2026/04/03/12-15-00-519_200x200.jpg"
        ),
        createMediaItem(
            id = "pixabay-10",
            title = "Energetic Action Sport",
            artist = "AlexGrohl",
            album = "Pixabay",
            uri = "https://cdn.pixabay.com/download/audio/2026/03/12/audio_bea8e8877a.mp3?filename=alexgrohl-energetic-action-sport-500409.mp3",
            artworkUri = "https://cdn.pixabay.com/audio/2026/03/12/00-31-42-217_200x200.png"
        ),
        createMediaItem(
            id = "pixabay-11",
            title = "Upbeat Happy Corporate",
            artist = "kornevmusic",
            album = "Pixabay",
            uri = "https://cdn.pixabay.com/download/audio/2026/02/19/audio_8e10e01af1.mp3?filename=kornevmusic-upbeat-happy-corporate-487426.mp3",
            artworkUri = "https://cdn.pixabay.com/audio/2026/02/19/13-46-05-544_200x200.jpg"
        ),
        createMediaItem(
            id = "pixabay-12",
            title = "Inspiring Cinematic Music",
            artist = "Tunetank",
            album = "Pixabay",
            uri = "https://cdn.pixabay.com/download/audio/2025/09/23/audio_1b6f4de1c4.mp3?filename=tunetank-inspiring-cinematic-music-409347.mp3",
            artworkUri = "https://cdn.pixabay.com/audio/2025/09/23/10-28-21-825_200x200.jpg"
        ),
        createMediaItem(
            id = "pixabay-13",
            title = "Dark Cyberpunk I Free Background Music I Free Music Lab Release",
            artist = "FreeMusicLab",
            album = "Pixabay",
            uri = "https://cdn.pixabay.com/download/audio/2026/01/20/audio_7fd1a0d3a2.mp3?filename=freemusiclab-dark-cyberpunk-i-free-background-music-i-free-music-lab-release-469493.mp3",
            artworkUri = "https://cdn.pixabay.com/audio/2026/01/20/09-09-18-899_200x200.png"
        ),
        createMediaItem(
            id = "pixabay-14",
            title = "Background Music - New Age Nature",
            artist = "Sonican",
            album = "Pixabay",
            uri = "https://cdn.pixabay.com/download/audio/2026/01/13/audio_9bfdf7a71a.mp3?filename=sonican-background-music-new-age-nature-465069.mp3",
            artworkUri = "https://cdn.pixabay.com/audio/2026/01/13/06-03-56-541_200x200.png"
        ),
        createMediaItem(
            id = "pixabay-15",
            title = "Honey Kisses",
            artist = "DeltaX-Music",
            album = "Pixabay",
            uri = "https://cdn.pixabay.com/download/audio/2025/10/01/audio_e43b1a7255.mp3?filename=deltax-music-honey-kisses-413841.mp3",
            artworkUri = "https://cdn.pixabay.com/audio/2025/10/01/20-57-32-216_200x200.jpg"
        ),
        createMediaItem(
            id = "pixabay-16",
            title = "Deep Abstract Ambient_Snowcap",
            artist = "ummbrella",
            album = "Pixabay",
            uri = "https://cdn.pixabay.com/download/audio/2025/09/08/audio_3e2526c41c.mp3?filename=ummbrella-deep-abstract-ambient_snowcap-401656.mp3",
            artworkUri = "https://cdn.pixabay.com/audio/2025/09/08/17-14-54-371_200x200.jpeg"
        ),
    )

    private fun createMediaItem(
        id: String,
        title: String,
        artist: String,
        album: String,
        uri: String,
        artworkUri: String
    ): MediaItem =
        MediaItem.Builder()
            .setUri(uri)
            .setMediaId(id)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(title)
                    .setArtist(artist)
                    .setAlbumTitle(album)
                    .setArtworkUri(Uri.parse(artworkUri))
                    .build()
            )
            .build()
}

data class PlayerUiState(
    val isControllerReady: Boolean = false,
    val isPlaying: Boolean = false,
    val playbackState: Int = Player.STATE_IDLE,
    val currentMediaItemIndex: Int = 0,
    val errorMessage: String? = null,
    val currentMediaItem: MediaItem? = null,
    val currentRoute: String? = null
)
