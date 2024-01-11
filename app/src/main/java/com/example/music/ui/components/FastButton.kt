package com.example.music.ui.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.music.ui.theme.Gray400
import com.example.music.ui.theme.onSurface

@Composable
fun FastButton(
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    @DrawableRes iconResId: Int,
    @StringRes stringResId: Int
) {

    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(id = iconResId),
            contentDescription = stringResource(id = stringResId),
            modifier = Modifier.padding(all = 8.dp),
            tint = if (enabled) onSurface else Gray400
        )
    }

}