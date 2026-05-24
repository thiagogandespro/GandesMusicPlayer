package com.example.gandesmusicplayer

import android.content.ComponentName
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.gandesmusicplayer.service.PlayBackService
import com.example.gandesmusicplayer.ui.theme.GandesMusicPlayerTheme
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    val playerViewModel: PlayerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GandesMusicPlayerTheme {
                NavGraph(playViewModel = playerViewModel)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        playerViewModel.initController(this)
    }

    override fun onStop() {
        super.onStop()
        playerViewModel.releaseController()
    }
}

@Composable
fun NavGraph(
    startDestination: String = Destinations.PLAY_BACK_SCREEN,
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    navActions: AppNavigationActions = remember(navController) {
        AppNavigationActions(navController)
    },
    playViewModel: PlayerViewModel,
    modifier: Modifier = Modifier
){
    val currentNavBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentNavBackStackEntry?.destination?.route ?: startDestination
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier
    ){
        composable(
            route = Destinations.PLAY_BACK_SCREEN
        ) {
            AppModalDrawer(
                drawerState = drawerState,
                navigationActions = navActions,
                openDrawer = {coroutineScope.launch { drawerState.open() }},
                closeDrawer = {coroutineScope.launch { drawerState.close() }}
            ) {
                PlayBackScreen(
                    modifier = Modifier.fillMaxSize(),
                    onSkipPrevious = {

                    },
                    onPlayPause = {
                        playViewModel.controller.value?.let { controller ->
                            if(controller.isPlaying)
                                controller.pause()
                            else
                                controller.play()
                        }
                    },
                    onSkipNext = {

                    }
                )
            }
        }
        composable(
            route = Destinations.PLAY_LIST_SCREEN
        ){
            AppModalDrawer(
                drawerState = drawerState,
                navigationActions = navActions,
                openDrawer = {coroutineScope.launch { drawerState.open() }},
                closeDrawer = {coroutineScope.launch { drawerState.close() }}
            ) {
                PlayListScreen(
                    modifier = Modifier.fillMaxSize(),
                    playList = listOf(
                        Music(1, "Song 1", "Artist 1", "Album 1", 180000),
                        Music(2, "Song 2", "Artist 2", "Album 2", 210000),
                        Music(3, "Song 3", "Artist 3", "Album 3", 240000),
                    )
                )
            }
        }
    }
}

//Here I decided for "hoisting" mediaController so PlayBackScreen becomes more reusable and testable
//fun PlayBackScreen( modifier: Modifier = Modifier, mediaController: MediaController? = null)
//fun PlayBackScreen( modifier: Modifier = Modifier, onSkipPrevious: () -> Unit = {}, onPlayPause: () -> Unit = {}, onSkipNext: () -> Unit = {},)
@Composable
fun PlayBackScreen(
    modifier: Modifier = Modifier,
    onSkipPrevious: () -> Unit = {},
    onPlayPause: () -> Unit = {},
    onSkipNext: () -> Unit = {},
){
    Surface(modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier.weight(2f))
            Image(
                painter = painterResource(R.drawable.ic_launcher_background),
                contentDescription = "Origin Album",
                modifier = modifier.weight(6f).fillMaxWidth(0.8f),
            )
            Spacer(modifier.weight(2f))
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = modifier.fillMaxWidth().weight(2f),
            ) {
                IconButton(
                    onClick = onSkipPrevious,
                    modifier = Modifier.size(60.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_skip_previous),
                        contentDescription = "Back Button",
                        modifier = Modifier.size(60.dp)
                    )
                }
                IconButton(
                    onClick = onPlayPause,
                    modifier = Modifier.size(60.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_play_arrow),
                        contentDescription = "Back Button",
                        modifier = Modifier.size(60.dp)
                    )
                }
                IconButton(
                    onClick = onSkipNext,
                    modifier = Modifier.size(60.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_skip_next),
                        contentDescription = "Back Button",
                        modifier = Modifier.size(60.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewPlayBackScreen(){
    PlayBackScreen()
}

@Preview
@Composable
fun PreviewPlayListScreen(){
    PlayListScreen(
        modifier = Modifier.fillMaxSize(),
        playList = listOf(
            Music(1, "Song 1", "Artist 1", "Album 1", 180000),
            Music(2, "Song 2", "Artist 2", "Album 2", 210000),
            Music(3, "Song 3", "Artist 3", "Album 3", 240000),
        )
    )
}

@Composable
fun PlayListScreen(modifier: Modifier = Modifier, playList: List<Music> = emptyList()){
    Surface(modifier.fillMaxSize()) {
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,

        ) {
            items(playList){ music ->
                MusicCard(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.10f), music = music,)
            }
        }
    }
}

@Composable
fun MusicCard(modifier: Modifier = Modifier, music: Music) {
    Card(
        modifier = modifier,
        shape = RectangleShape,
        onClick = {},
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ){
        Row(
            Modifier.fillMaxSize(),
        ) {
            Surface(
                shape = CircleShape,
                modifier = Modifier.size(84.dp).padding(4.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_launcher_background),
                    contentDescription = "Album Cover",
                    modifier = Modifier.size(64.dp)
                )
            }
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxHeight().weight(1f),
            ) {
                Text(
                    text = music.artist,
                    Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = music.name,
                    Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

data class Music(
    val id: Int,
    val name: String,
    val artist: String,
    val album: String,
    val duration: Long,
)

@Composable
fun AppTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
){

}