package cn.beingyi.idea.view

import androidx.compose.runtime.Composable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Card
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

val MiniatureHoverColor = Color(0x66CDC9C9)

@Composable
fun Clickable(
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    onClick: (() -> Unit)? = null,
    enable: Boolean = true,
    children: @Composable () -> Unit
) {
    val cardHover = remember { mutableStateOf(false) }

    Card(
        backgroundColor = if (enable && cardHover.value) MiniatureHoverColor else Color.Transparent,
        modifier = modifier.hover(
            onEnter = {
                cardHover.value = true
                false
            },
            onExit = {
                cardHover.value = false
                false
            },
            onMove = {
                false
            }
        ),
        shape = shape,
        elevation = 0.dp
    ) {
        Box(
            modifier = if (enable) modifier.clickable { onClick?.invoke() } else modifier
        ) {
            children()
        }
    }
}

inline fun Modifier.noRippleClickable(crossinline onClick: () -> Unit): Modifier = composed {
    clickable(indication = null,
        interactionSource = remember { MutableInteractionSource() }) {
        onClick()
    }
}

inline fun Modifier.noRippleClickable(): Modifier = composed {
    clickable(indication = null,
        interactionSource = remember { MutableInteractionSource() }) {
    }
}