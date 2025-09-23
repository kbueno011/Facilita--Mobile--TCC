package com.exemple.facilita.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    val scale = remember { Animatable(0f) }

    // Animação inicial tipo splash
    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1.5f,
            animationSpec = tween(durationMillis = 3000)
        )
        delay(1000)
        // Navega automaticamente para TelaInicio2
        navController.navigate("tela_inicio2") {
            popUpTo("tela_inicio2") { inclusive = true } // Remove a TelaInicio1 da backstack
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A1A)),
        contentAlignment = Alignment.Center
    ) {
        // Animação do círculo
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .scale(scale.value)
        ) {
            val maxRadius = size.minDimension / 2.2f
            val step = maxRadius / 10

            for (i in 2..10) { // Começa do segundo círculo
                drawCircle(
                    color = Color(0xFF019D31),
                    radius = step * i,
                    center = center,
                    style = Stroke(width = 4f)
                )
            }
        }

        // Texto do splash
        Text(
            text = "Facilita",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}


