package com.example.music.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.music.R
import com.example.music.ui.components.WarningMessage
import com.example.music.ui.theme.MusicTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main activity of the application
 */
@OptIn(ExperimentalMaterial3Api::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
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
                skipPartiallyExpanded = true
            )

            val state = mainViewModel.state

            MusicTheme {
                ModalBottomSheet(
                    sheetState = sheetState,
                    content = {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(state = rememberScrollState())
                                .padding(top = 16.dp)
                        ) {
                            if (state.audios.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp)
                                ) {
                                    WarningMessage(
                                        text = stringResource(id = R.string.txt_no_media),
                                        iconResId = R.drawable.circle_info_solid,
                                        modifier = Modifier
                                    ) {

                                    }
                                }
                            } else {

                            }
                        }
                    },
                    onDismissRequest = {
                        TODO()
                    }
                )
            }
        }
    }
}
