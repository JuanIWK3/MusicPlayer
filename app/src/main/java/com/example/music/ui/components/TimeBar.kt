package com.example.music.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.music.util.milliSecondsToTimeString

@Composable
fun TimeBar(
    currentPosition: Int,
    duration: Int,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = milliSecondsToTimeString(milliseconds = currentPosition),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Slider(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth(fraction = 0.9f),
            value = currentPosition.toFloat(),
            onValueChange = { onValueChange(it) },
            valueRange = 0f..duration.toFloat()
        )
        Text(
            text = milliSecondsToTimeString(milliseconds = duration),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }

}