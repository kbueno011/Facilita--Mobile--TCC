package com.exemple.facilita.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun IconButtonComBadge(
    icon: ImageVector,
    contentDescription: String?,
    badgeCount: Int = 0,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconTint: Color = Color.Unspecified
) {
    Box(modifier = modifier) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = iconTint
            )
        }

        // Badge com contador
        if (badgeCount > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-4).dp, y = 4.dp)
                    .size(18.dp)
                    .background(Color(0xFFFF6B6B), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (badgeCount > 99) "99+" else badgeCount.toString(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

