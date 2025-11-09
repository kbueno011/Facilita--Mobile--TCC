package com.exemple.facilita.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.exemple.facilita.model.Notificacao
import kotlinx.coroutines.delay

/**
 * Componente de notificação in-app estilo toast moderno
 * Aparece no topo da tela com animação suave
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NotificacaoInApp(
    notificacao: Notificacao?,
    onDismiss: () -> Unit,
    onTap: (() -> Unit)? = null,
    duracao: Long = 5000L
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(notificacao) {
        if (notificacao != null) {
            visible = true
            delay(duracao)
            visible = false
            delay(300) // Aguardar animação de saída
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = visible && notificacao != null,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) + fadeIn(),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = tween(300)
        ) + fadeOut()
    ) {
        notificacao?.let { notif ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(12.dp, RoundedCornerShape(16.dp))
                        .clickable {
                            onTap?.invoke()
                            visible = false
                        },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Ícone colorido
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(notif.obterCorFundo()).copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = notif.obterIcone(),
                                contentDescription = null,
                                tint = Color(notif.obterCorFundo()),
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Conteúdo
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = notif.titulo,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2C2C2C),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = notif.mensagem,
                                fontSize = 13.sp,
                                color = Color(0xFF6B6B6B),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                lineHeight = 18.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Botão fechar
                        IconButton(
                            onClick = {
                                visible = false
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Fechar",
                                tint = Color(0xFF9E9E9E),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Componente de badge com contador de notificações
 */
@Composable
fun NotificacaoBadge(
    contador: Int,
    modifier: Modifier = Modifier
) {
    if (contador > 0) {
        Box(
            modifier = modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(Color(0xFFFF3B30)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (contador > 99) "99+" else contador.toString(),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

/**
 * Indicador de ponto para notificações não lidas
 */
@Composable
fun NotificacaoPontoIndicador(
    visible: Boolean = true,
    modifier: Modifier = Modifier,
    cor: Color = Color(0xFFFF3B30)
) {
    if (visible) {
        Box(
            modifier = modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(cor)
        )
    }
}

/**
 * Animação de pulso para notificações urgentes
 */
@Composable
fun NotificacaoPulseAnimation(
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale
            )
    ) {
        content()
    }
}

