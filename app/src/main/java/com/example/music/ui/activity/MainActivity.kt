package com.example.music.ui.activity

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.music.R
import com.example.music.ui.theme.MusicTheme
import com.example.music.util.audio.setupPermissions
import com.example.music.util.audio.showPermissionsRationalDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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
            val sheetState = rememberModalBottomSheetState()
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
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TopAppBar(
                        modifier = Modifier
                            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                            .requiredHeight(height = 80.dp),
                        title = {

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
                }
            }
        }
    }
}
