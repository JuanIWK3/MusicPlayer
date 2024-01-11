package com.example.music.ui.activity

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.music.R
import com.example.music.ui.components.FastButton
import com.example.music.ui.components.LikeButton
import com.example.music.ui.components.LoadingDialog
import com.example.music.ui.components.PlayPauseButton
import com.example.music.ui.components.TimeBar
import com.example.music.ui.components.Track
import com.example.music.ui.components.WarningMessage
import com.example.music.ui.theme.MusicTheme
import com.example.music.util.FORWARD_BACKWARD_STEP
import com.example.music.util.setupPermissions
import com.example.music.util.showPermissionsRationalDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.example.music.util.screenHeight

/**
 * Main activity of the application
 */
@OptIn(ExperimentalMaterial3Api::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var requestPermissionLauncher: ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mainViewModel by viewModels<MainViewModel>()

        installSplashScreen().apply {
            setKeepOnScreenCondition {
                mainViewModel.state.isLoading
            }
        }



        setContent {
            val sheetState = rememberModalBottomSheetState(
                initialValue = ModalBottomSheetValue.Hidden,
                skipHalfExpanded = true
            )
            val scope = rememberCoroutineScope()
            val state = mainViewModel.state
            val context = LocalContext.current
            var showBottomSheet by remember { mutableStateOf(false) }

            val dialogText = stringResource(id = R.string.txt_permissions)

            val errorText = stringResource(id = R.string.txt_error_app_settings)

            requestPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestMultiplePermissions(),
                onResult = { permissions ->
                    val permissionsGranted = permissions.values.reduce { acc, next -> acc && next }
                    if (!permissionsGranted) {
                        showPermissionsRationalDialog(
                            context = context,
                            okButtonTextResId = R.string.lbl_ok,
                            cancelButtonTextResId = R.string.lbl_cancel,
                            dialogText = dialogText,
                            errorText = errorText,
                            packageName = packageName
                        )
                    }
                }
            )

            MusicTheme {
                ModalBottomSheetLayout(
                    sheetState = sheetState,
                    sheetShape = MaterialTheme.shapes.large,
                    sheetContent = {
                        Column(
                            modifier = Modifier
                                .verticalScroll(state = rememberScrollState())
                                .padding(top = 16.dp)
                        ) {
                            if (state.audios.isEmpty()) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    WarningMessage(
                                        text = stringResource(id = R.string.txt_no_media),
                                        iconResId = R.drawable.circle_info_solid,
                                        modifier = Modifier.padding(vertical = 16.dp)
                                    )
                                }
                            } else {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.lbl_tracks),
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(bottom = 3.dp, top = 12.dp),
                                        textDecoration = TextDecoration.Underline,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                                state.audios.forEach { audio ->
                                    Track(
                                        audio = audio,
                                        isPlaying = audio.id == state.selectedAudio.id,
                                        modifier = Modifier
                                            .padding(
                                                horizontal = 8.dp, vertical = 10.dp
                                            )
                                            .requiredHeight(height = 100.dp),
                                        onClick = {
                                            scope.launch {
                                                mainViewModel.onEvent(event = AudioPlayerEvent.Stop)
                                                sheetState.hide()
                                                mainViewModel.onEvent(event = AudioPlayerEvent.InitAudio(
                                                    audio = it,
                                                    context = context,
                                                    onAudioInitialized = {
                                                        mainViewModel.onEvent(event = AudioPlayerEvent.Play)
                                                    }
                                                ))
                                            }
                                        }
                                    )
                                    Divider(modifier = Modifier.padding(horizontal = 8.dp))
                                }
                            }
                        }
                    },
                    content = {
                        LoadingDialog(
                            isLoading = state.isLoading,
                            modifier = Modifier
                                .clip(shape = MaterialTheme.shapes.large)
                                .background(color = MaterialTheme.colorScheme.surface)
                                .requiredSize(size = 80.dp),
                            onDone = { mainViewModel.onEvent(event = AudioPlayerEvent.HideLoadingDialog) }
                        )
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            TopAppBar(
                                modifier = Modifier
                                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                                    .requiredHeight(height = 80.dp),
                                leadingIcon = {
                                    LikeButton(
                                        isLiked = state.likedSongs.contains(state.selectedAudio.id),
                                        enabled = state.selectedAudio.isNotEmpty(),
                                        onClick = {
                                            mainViewModel.onEvent(
                                                event = AudioPlayerEvent.LikeOrNotSong(
                                                    id = state.selectedAudio.id
                                                )
                                            )
                                        }
                                    )
                                },
                                title = {
                                    if (state.selectedAudio.) {
                                        val artist = if (state.selectedAudio.artist.contains(
                                                "unknown",
                                                ignoreCase = true
                                            )
                                        ) "" else "${state.selectedAudio.artist} - "
                                        Text(
                                            text = buildAnnotatedString {
                                                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                                    append(text = artist)
                                                }
                                                append(text = "  ${state.selectedAudio.title}")
                                            },
                                            color = MaterialTheme.colorScheme.onSurface,
                                            overflow = TextOverflow.Ellipsis,
                                            style = MaterialTheme.typography.titleSmall
                                        )
                                    }
                                    Text(
                                        text = "Music",
                                        color = MaterialTheme.colorScheme.onSurface,
                                        overflow = TextOverflow.Ellipsis,
                                        style = MaterialTheme.typography.bodyLarge
                                    )

                                },
                                actions = {
                                    IconButton(onClick = {
                                        setupPermissions(
                                            context = context,
                                            permissions = arrayOf(
                                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                                Manifest.permission.RECORD_AUDIO,
                                            ),
                                            launcher = requestPermissionLauncher,
                                            onPermissionsGranted = {
                                                scope.launch {
                                                    if (state.audios.isEmpty()) {
                                                        mainViewModel.onEvent(event = AudioPlayerEvent.LoadMedia)
                                                    }
                                                    sheetState.show()
                                                }
                                            }
                                        )
                                    }) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                                            contentDescription = "",
                                            tint = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            )
                            if (state.audios.isEmpty()) {
                                Text(text = "No audios found")
                            } else {
                                state.audios.forEach { audio ->
                                    Text(text = audio.title)
                                }
                            }

                            Spacer(modifier = Modifier.requiredHeight(height = 16.dp))

                            state.selectedAudio.cover?.let {
                                Image(
                                    bitmap = it.asImageBitmap(),
                                    modifier = Modifier
                                        .requiredHeight(height = screenHeight() * 0.4f)
                                        .clip(shape = MaterialTheme.shapes.large),
                                    contentScale = ContentScale.Crop,
                                    contentDescription = ""
                                )
                            } ?: Box(
                                modifier = Modifier.requiredHeight(height = screenHeight() * 0.4f),
                                contentAlignment = Alignment.Center
                            ) {
                                Card(
                                    elevation = 8.dp,
                                    shape = MaterialTheme.shapes.large,
                                    modifier = Modifier.fillMaxHeight(fraction = 0.5f)
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.musical_note_music_svgrepo_com),
                                        modifier = Modifier
                                            .padding(
                                                top = 25.dp,
                                                bottom = 26.dp,
                                                start = 8.dp,
                                                end = 20.dp
                                            ),
                                        contentScale = ContentScale.FillHeight,
                                        contentDescription = ""
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.requiredHeight(height = 16.dp))

                            Spacer(modifier = Modifier.requiredHeight(height = 10.dp))

                            TimeBar(
                                currentPosition = state.currentPosition,
                                onValueChange = { position ->
                                    mainViewModel.onEvent(event = AudioPlayerEvent.SeekTo(position = position))
                                },
                                duration = state.selectedAudio.duration
                            )

                            Spacer(modifier = Modifier.requiredHeight(height = 10.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(all = 10.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                FastButton(
                                    enabled = state.currentPosition > FORWARD_BACKWARD_STEP,
                                    onClick = {
                                        mainViewModel.onEvent(
                                            event = AudioPlayerEvent.SeekTo(position = state.currentPosition - FORWARD_BACKWARD_STEP.toFloat())
                                        )
                                    },
                                    iconResId = R.drawable.backward_solid,
                                    stringResId = R.string.lbl_fast_backward
                                )
                                PlayPauseButton(
                                    modifier = Modifier.padding(horizontal = 26.dp),
                                    enabled = state.selectedAudio.isNotEmpty(),
                                    isPlaying = state.isPlaying,
                                    onPlay = { mainViewModel.onEvent(event = AudioPlayerEvent.Play) },
                                    onPause = { mainViewModel.onEvent(event = AudioPlayerEvent.Pause) }
                                )
                                FastButton(
                                    enabled = state.currentPosition < (state.selectedAudio.duration - FORWARD_BACKWARD_STEP),
                                    onClick = {
                                        mainViewModel.onEvent(
                                            event = AudioPlayerEvent.SeekTo(position = state.currentPosition + FORWARD_BACKWARD_STEP.toFloat())
                                        )
                                    },
                                    iconResId = R.drawable.forward_solid,
                                    stringResId = R.string.lbl_fast_forward
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}
