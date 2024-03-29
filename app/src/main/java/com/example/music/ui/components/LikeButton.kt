package com.example.music.ui.components

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.music.R
import com.example.music.ui.theme.Pink500

@Composable
fun LikeButton(
    isLiked: Boolean,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    buttonSize: Dp = 26.dp
) {

    val transition = updateTransition(targetState = isLiked, label = "transition")

    val shouldBeAnimated = remember { mutableStateOf(value = false) }

    Icon(
        painter = painterResource(id = if (isLiked) R.drawable.heart_solid else R.drawable.heart_outlined),
        contentDescription = "",
        tint = if (enabled) Pink500 else MaterialTheme.colorScheme.onSurface,
        modifier = modifier
            .clip(shape = RoundedCornerShape(size = 100.dp))
            .padding(all = 8.dp)
            .clickable(
                indication = rememberRipple(bounded = false),
                enabled = enabled,
                onClick = {
                    shouldBeAnimated.value = true
                    onClick()
                },
                interactionSource = remember { MutableInteractionSource() },
                role = Role.Button
            )
            .size(size = buttonSize)
    )

}