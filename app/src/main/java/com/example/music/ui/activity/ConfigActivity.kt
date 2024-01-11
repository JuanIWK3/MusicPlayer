package com.example.music.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.music.ui.theme.MusicTheme
import com.example.music.util.audio.UserPreferences
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ConfigActivity : ComponentActivity() {

    @Inject
    lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MusicTheme {
                ConfigScreen(userPreferences)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigScreen(userPreferences: UserPreferences) {
    val showWhatsAppAudios by userPreferences.showWhatsAppAudios.collectAsState(true)
    val context = LocalContext.current

    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Configuration")
                },
                navigationIcon = {
                    IconButton(onClick = { (context as ConfigActivity).finish() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Show WhatsApp Audios",
                style = MaterialTheme.typography.bodyLarge
            )
            Switch(
                checked = showWhatsAppAudios,
                onCheckedChange = { newCheckedState ->
                    scope.launch {
                        userPreferences.setShowWhatsAppAudios(newCheckedState)
                    }
                },
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}
