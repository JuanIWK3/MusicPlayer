package com.example.music.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.music.domain.model.AudioMetaData
import com.example.music.R

@Composable
fun Track(
    audio: AudioMetaData,
    onClick: (AudioMetaData) -> Unit,
    modifier: Modifier = Modifier,
    isPlaying: Boolean
) {

    Row(
        modifier = modifier
            .fillMaxSize()
            .clickable { onClick(audio) },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Row(
            modifier = Modifier.fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.mp3_logo),
                contentDescription = "",
                modifier = Modifier
                    .size(size = 60.dp)
                    .padding(all = 5.dp),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(start = 10.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = audio.title,
                    modifier = Modifier.padding(bottom = 3.dp),
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis,
                    color = if (isPlaying) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = audio.artist,
                    color = if (isPlaying) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onBackground
                )
            }
        }

        if (isPlaying) {
            Icon(
                painter = painterResource(id = R.drawable.chart_simple_solid),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(end = 8.dp)
            )
        }
    }

}