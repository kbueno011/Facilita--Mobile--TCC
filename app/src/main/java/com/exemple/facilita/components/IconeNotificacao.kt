package com.exemple.facilita.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.exemple.facilita.viewmodel.NotificacaoViewModel
import com.exemple.facilita.utils.sdp
import com.exemple.facilita.utils.ssp

/**
 * Ícone de notificação com badge para ser usado na TopBar
 */
@Composable
fun IconeNotificacao(
    navController: NavController,
    viewModel: NotificacaoViewModel = viewModel(),
    corIcone: Color = Color(0xFF2C2C2C)
) {
    val notificacoesNaoLidas by viewModel.notificacoesNaoLidas.collectAsState()

    Box(
        modifier = Modifier
            .size(44.sdp())
            .clickable {
                navController.navigate("tela_notificacoes")
            }
    ) {
        Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = "Notificações",
            tint = corIcone,
            modifier = Modifier
                .size(22.sdp())
                .align(Alignment.Center)
        )

        // Badge com contador
        if (notificacoesNaoLidas > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-3).sdp(), y = 7.sdp())
                    .size(16.sdp())
                    .clip(CircleShape)
                    .background(Color(0xFFFF3B30)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (notificacoesNaoLidas > 9) "9+" else notificacoesNaoLidas.toString(),
                    fontSize = 8.ssp(),
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

