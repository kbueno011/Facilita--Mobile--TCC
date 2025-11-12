package com.exemple.facilita.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.exemple.facilita.data.models.Notificacao
import com.exemple.facilita.data.models.TipoNotificacao
import com.exemple.facilita.utils.TokenManager
import com.exemple.facilita.viewmodel.NotificacaoViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaNotificacoes(
    navController: NavController
) {
    val context = LocalContext.current
    val viewModel: NotificacaoViewModel = viewModel()

    val notificacoes by viewModel.notificacoes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val token = TokenManager.obterToken(context) ?: ""

    var mostrarDialogMarcarTodas by remember { mutableStateOf(false) }

    // Carrega notificações ao abrir
    LaunchedEffect(Unit) {
        if (token.isNotEmpty()) {
            viewModel.buscarNotificacoes(token)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Notificações",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Voltar",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                },
                actions = {
                    // Botão marcar todas como lidas
                    if (notificacoes.any { !it.lida }) {
                        IconButton(onClick = { mostrarDialogMarcarTodas = true }) {
                            Icon(
                                imageVector = Icons.Default.DoneAll,
                                contentDescription = "Marcar todas como lidas",
                                tint = Color(0xFF00B14F)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF2D2D2D)
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F7))
        ) {
            if (isLoading && notificacoes.isEmpty()) {
                // Loading inicial
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF00B14F))
                }
            } else if (notificacoes.isEmpty()) {
                // Sem notificações
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = Color(0xFFE0E0E0),
                        modifier = Modifier.size(100.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Nenhuma notificação",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6D6D6D)
                    )
                    Text(
                        text = "Você receberá notificações sobre seus serviços aqui",
                        fontSize = 14.sp,
                        color = Color(0xFF6D6D6D)
                    )
                }
            } else {
                // Lista de notificações
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = notificacoes,
                        key = { it.id }
                    ) { notificacao ->
                        CardNotificacao(
                            notificacao = notificacao,
                            onClick = {
                                // Marca como lida
                                if (!notificacao.lida) {
                                    viewModel.marcarComoLida(token, notificacao.id)
                                }

                                // Navega se tiver serviço associado
                                notificacao.idServico?.let { servicoId ->
                                    navController.navigate("tela_aguardo_servico/$servicoId")
                                }
                            },
                            onDelete = {
                                viewModel.deletarNotificacao(token, notificacao.id)
                            }
                        )
                    }
                }
            }
        }
    }

    // Dialog marcar todas como lidas
    if (mostrarDialogMarcarTodas) {
        AlertDialog(
            onDismissRequest = { mostrarDialogMarcarTodas = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.DoneAll,
                    contentDescription = null,
                    tint = Color(0xFF00B14F),
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text("Marcar todas como lidas?")
            },
            text = {
                Text("Todas as notificações serão marcadas como lidas.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.marcarTodasComoLidas(token)
                        mostrarDialogMarcarTodas = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00B14F)
                    )
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogMarcarTodas = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CardNotificacao(
    notificacao: Notificacao,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var mostrarDialog by remember { mutableStateOf(false) }

    val tipo = TipoNotificacao.fromString(notificacao.tipo)
    val (icone, corFundo) = when (tipo) {
        TipoNotificacao.SERVICO_ACEITO -> Icons.Default.CheckCircle to Color(0xFF00B14F)
        TipoNotificacao.SERVICO_INICIADO -> Icons.Default.DirectionsCar to Color(0xFF2196F3)
        TipoNotificacao.SERVICO_CONCLUIDO -> Icons.Default.Done to Color(0xFF4CAF50)
        TipoNotificacao.SERVICO_CANCELADO -> Icons.Default.Cancel to Color(0xFFFF6B6B)
        TipoNotificacao.MENSAGEM -> Icons.Default.Message to Color(0xFF9C27B0)
        TipoNotificacao.PAGAMENTO -> Icons.Default.Payment to Color(0xFFFF9800)
        else -> Icons.Default.Notifications to Color(0xFF757575)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notificacao.lida) Color.White else Color(0xFFF0F9F4)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (notificacao.lida) 2.dp else 4.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Ícone
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        corFundo.copy(alpha = 0.1f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icone,
                    contentDescription = null,
                    tint = corFundo,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Conteúdo
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notificacao.titulo,
                        fontSize = 16.sp,
                        fontWeight = if (notificacao.lida) FontWeight.Medium else FontWeight.Bold,
                        color = Color(0xFF2D2D2D),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    // Indicador não lida
                    if (!notificacao.lida) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Color(0xFF00B14F), CircleShape)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notificacao.mensagem,
                    fontSize = 14.sp,
                    color = Color(0xFF6D6D6D),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatarDataHora(notificacao.data),
                        fontSize = 12.sp,
                        color = Color(0xFF9E9E9E)
                    )

                    // Botão deletar
                    IconButton(
                        onClick = { mostrarDialog = true },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Deletar",
                            tint = Color(0xFFFF6B6B),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }

    // Dialog confirmar exclusão
    if (mostrarDialog) {
        AlertDialog(
            onDismissRequest = { mostrarDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = Color(0xFFFF6B6B)
                )
            },
            title = { Text("Excluir notificação?") },
            text = { Text("Esta ação não pode ser desfeita.") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        mostrarDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF6B6B)
                    )
                ) {
                    Text("Excluir")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

private fun formatarDataHora(dataStr: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = inputFormat.parse(dataStr)

        val now = Calendar.getInstance()
        val notifDate = Calendar.getInstance().apply { time = date }

        val diffMillis = now.timeInMillis - notifDate.timeInMillis
        val diffMinutes = diffMillis / (60 * 1000)
        val diffHours = diffMillis / (60 * 60 * 1000)
        val diffDays = diffMillis / (24 * 60 * 60 * 1000)

        when {
            diffMinutes < 1 -> "Agora"
            diffMinutes < 60 -> "${diffMinutes}m atrás"
            diffHours < 24 -> "${diffHours}h atrás"
            diffDays < 7 -> "${diffDays}d atrás"
            else -> {
                val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                outputFormat.format(date)
            }
        }
    } catch (e: Exception) {
        dataStr
    }
}

