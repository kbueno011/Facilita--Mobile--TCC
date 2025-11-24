package com.exemple.facilita.screens

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.exemple.facilita.network.WebSocketManager
import com.exemple.facilita.network.ChatMessage
import com.exemple.facilita.utils.TokenManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaChat(
    navController: NavController,
    servicoId: String,
    prestadorNome: String = "Prestador",
    prestadorTelefone: String = "",
    prestadorPlaca: String = "",
    prestadorId: Int = 0
) {
    val context = LocalContext.current
    val greenColor = Color(0xFF019D31)

    // WebSocket Manager (mesma instância usada para rastreamento!)
    val webSocketManager = remember { WebSocketManager.getInstance() }
    val messages by webSocketManager.chatMessages.collectAsState()
    val isConnected by webSocketManager.isConnected.collectAsState()

    val userId = TokenManager.obterUserId(context) ?: 0
    val userName = TokenManager.obterNomeUsuario(context) ?: "Contratante"

    // Estado da mensagem
    var textState by remember { mutableStateOf(TextFieldValue("")) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Usa WebSocket já conectado (mesma instância do rastreamento!)
    LaunchedEffect(servicoId, userId) {
        if (userId > 0) {
            Log.d("TelaChat", "💬 Configurando chat no WebSocket...")
            Log.d("TelaChat", "   ServicoId: $servicoId")
            Log.d("TelaChat", "   UserId: $userId")
            Log.d("TelaChat", "   UserName: $userName")
            Log.d("TelaChat", "   PrestadorId: $prestadorId")

            // Verifica se já está conectado
            val jaConectado = webSocketManager.isSocketConnected()
            Log.d("TelaChat", "   Socket já conectado? $jaConectado")

            if (!jaConectado) {
                Log.d("TelaChat", "🔌 WebSocket não está conectado! Conectando...")
                webSocketManager.connect(
                    userId = userId,
                    userType = "contratante",
                    userName = userName
                )
                Log.d("TelaChat", "⏳ Aguardando 1.5 segundos para estabilizar...")
                kotlinx.coroutines.delay(1500)
            } else {
                Log.d("TelaChat", "✅ Usando WebSocket já conectado (do rastreamento)")
            }

            // Sempre entra na sala do serviço (garante que está na sala correta)
            Log.d("TelaChat", "🚪 Garantindo entrada na sala do serviço: $servicoId")
            webSocketManager.joinServico(servicoId)
            Log.d("TelaChat", "✅ Comando join_servico enviado")
        } else {
            Log.e("TelaChat", "❌ UserId inválido: $userId - não pode usar chat")
        }
    }

    // Rola para última mensagem quando recebe nova
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(messages.size - 1)
            }
        }
    }

    // Limpa ao sair
    DisposableEffect(Unit) {
        onDispose {
            Log.d("TelaChat", "🔙 Saindo do chat (WebSocket continua ativo para rastreamento)")
            // NÃO desconecta o WebSocket pois está sendo usado no rastreamento!
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE7E7E7))
            .statusBarsPadding()
    ) {
        // Topo verde com informações do prestador
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = greenColor,
            tonalElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Botão voltar
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Informações do prestador
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = prestadorNome,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (prestadorPlaca.isNotEmpty()) {
                        Text(
                            text = prestadorPlaca,
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 13.sp
                        )
                    }
                    // Status de conexão
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(
                                    if (isConnected) Color(0xFF00FF00) else Color(0xFFFF0000),
                                    androidx.compose.foundation.shape.CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isConnected) "Online" else "Offline",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 11.sp
                        )
                    }
                }

                // Botão de ligar
                IconButton(
                    onClick = {
                        if (prestadorTelefone.isNotEmpty()) {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:$prestadorTelefone")
                            }
                            context.startActivity(intent)
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "Ligar",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Lista de mensagens
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color(0xFFF9F9F9))
                .padding(horizontal = 14.dp),
            state = listState
        ) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
                // Indicador de data
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        color = Color(0xFFF2F2F2),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Hoje",
                            color = Color(0xFF7C7C7C),
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            if (messages.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.ChatBubbleOutline,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Nenhuma mensagem ainda",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Envie a primeira mensagem!",
                                color = Color.Gray.copy(alpha = 0.7f),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            items(messages) { message ->
                ChatMessageItem(
                    message = message,
                    greenColor = greenColor
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Caixa de texto para enviar mensagem
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            tonalElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .navigationBarsPadding(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFFF5F5F5))
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    if (textState.text.isEmpty()) {
                        Text(
                            text = "Enviar mensagem...",
                            color = Color.Gray,
                            fontSize = 15.sp
                        )
                    }
                    BasicTextField(
                        value = textState,
                        onValueChange = { textState = it },
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = Color.Black,
                            fontSize = 15.sp
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Botão enviar
                IconButton(
                    onClick = {
                        val mensagem = textState.text.trim()
                        if (mensagem.isNotEmpty() && prestadorId > 0) {
                            Log.d("TelaChat", "📤 Enviando mensagem: $mensagem")
                            webSocketManager.sendChatMessage(
                                servicoId = servicoId.toIntOrNull() ?: 0,
                                mensagem = mensagem,
                                sender = "contratante",
                                targetUserId = prestadorId
                            )
                            textState = TextFieldValue("")
                        } else {
                            Log.w("TelaChat", "⚠️ Não pode enviar: mensagem vazia ou prestadorId inválido")
                        }
                    },
                    enabled = textState.text.isNotBlank() && isConnected
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Enviar",
                        tint = if (textState.text.isNotBlank() && isConnected)
                            greenColor else Color.Gray,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatMessageItem(
    message: ChatMessage,
    greenColor: Color
) {
    val dateFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val timeText = dateFormat.format(Date(message.timestamp))

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.isOwn) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .padding(
                    start = if (message.isOwn) 60.dp else 0.dp,
                    end = if (message.isOwn) 0.dp else 60.dp
                )
                .background(
                    color = if (message.isOwn) greenColor else Color.White,
                    shape = RoundedCornerShape(
                        topStart = 12.dp,
                        topEnd = 12.dp,
                        bottomStart = if (message.isOwn) 12.dp else 2.dp,
                        bottomEnd = if (message.isOwn) 2.dp else 12.dp
                    )
                )
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Column {
                if (!message.isOwn) {
                    Text(
                        text = message.userName,
                        color = greenColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                }

                Text(
                    text = message.mensagem,
                    color = if (message.isOwn) Color.White else Color.Black,
                    fontSize = 15.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = timeText,
                    color = if (message.isOwn) Color.White.copy(alpha = 0.8f)
                    else Color.Gray,
                    fontSize = 11.sp,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TelaChatPreview() {
    MaterialTheme {
        TelaChat(
            navController = rememberNavController(),
            servicoId = "1",
            prestadorNome = "João Silva",
            prestadorTelefone = "(11) 98765-4321",
            prestadorPlaca = "ABC-1234",
            prestadorId = 2
        )
    }
}
