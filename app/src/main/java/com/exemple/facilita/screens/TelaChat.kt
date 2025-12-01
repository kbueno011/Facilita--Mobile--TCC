package com.exemple.facilita.screens

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
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
import kotlinx.coroutines.delay
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
                delay(1500)
            } else {
                Log.d("TelaChat", "✅ Usando WebSocket já conectado (do rastreamento)")
                Log.d("TelaChat", "🔄 Garantindo que listeners de chat estão registrados...")
                // Garante que os listeners estão registrados mesmo se socket já estava conectado
                webSocketManager.ensureListenersRegistered()
            }

            // Sempre entra na sala do serviço (garante que está na sala correta)
            Log.d("TelaChat", "🚪 Garantindo entrada na sala do serviço: $servicoId")
            webSocketManager.joinServico(servicoId)
            Log.d("TelaChat", "✅ Comando join_servico enviado")
        } else {
            Log.e("TelaChat", "❌ UserId inválido: $userId - não pode usar chat")
        }
    }

    // Monitora mudanças nas mensagens
    LaunchedEffect(messages) {
        Log.d("TelaChat", "")
        Log.d("TelaChat", "╔════════════════════════════════════════════════╗")
        Log.d("TelaChat", "║  📨 MENSAGENS ATUALIZADAS!                    ║")
        Log.d("TelaChat", "╚════════════════════════════════════════════════╝")
        Log.d("TelaChat", "   📊 Total de mensagens: ${messages.size}")
        messages.forEachIndexed { index, msg ->
            Log.d("TelaChat", "   [$index] ${if (msg.isOwn) "VOCÊ" else msg.userName}: ${msg.mensagem}")
        }
        Log.d("TelaChat", "╚════════════════════════════════════════════════╝")
        Log.d("TelaChat", "")
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

    // 🎨 DESIGN FUTURISTA E MINIMALISTA
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .statusBarsPadding()
    ) {
        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        // 🟢 HEADER FUTURISTA - DESIGN CLEAN
        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = greenColor,
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botão voltar minimalista
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            Color.White.copy(alpha = 0.15f),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Avatar futurista com inicial
                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .background(Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = prestadorNome.firstOrNull()?.uppercase() ?: "P",
                        color = greenColor,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    // Indicador de status (sem animação)
                    if (isConnected) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(12.dp)
                                .background(Color(0xFF00E676), CircleShape)
                                .border(2.dp, Color.White, CircleShape)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Info do prestador - design clean
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = prestadorNome,
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = if (isConnected) "Online" else "Offline",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 12.sp
                    )
                }

                // Botão de ligar (se disponível)
                if (prestadorTelefone.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:$prestadorTelefone")
                            }
                            context.startActivity(intent)
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                Color.White.copy(alpha = 0.15f),
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Ligar",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        // 💬 ÁREA DE MENSAGENS - DESIGN FUTURISTA
        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            state = listState
        ) {
            // Data separadora
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFE8E8E8)
                    ) {
                        Text(
                            text = "Hoje",
                            color = Color(0xFF666666),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Estado vazio
            if (messages.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Ícone simples sem animação
                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .background(
                                        greenColor.copy(alpha = 0.1f),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ChatBubbleOutline,
                                    contentDescription = null,
                                    tint = greenColor.copy(alpha = 0.5f),
                                    modifier = Modifier.size(35.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Sem mensagens",
                                color = Color(0xFF666666),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Envie a primeira mensagem para iniciar\na conversa com o prestador!",
                                color = Color.Gray.copy(alpha = 0.7f),
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 18.sp
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

        // 💬 CAMPO DE INPUT MODERNO
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 12.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .navigationBarsPadding(),
                verticalAlignment = Alignment.Bottom
            ) {
                // Caixa de texto
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .shadow(2.dp, RoundedCornerShape(28.dp))
                        .clip(RoundedCornerShape(28.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFF9F9F9),
                                    Color(0xFFFAFAFA)
                                )
                            )
                        )
                        .border(
                            width = 1.dp,
                            color = if (textState.text.isNotEmpty())
                                greenColor.copy(alpha = 0.3f)
                            else
                                Color(0xFFE5E5E5),
                            shape = RoundedCornerShape(28.dp)
                        )
                        .padding(horizontal = 18.dp, vertical = 14.dp)
                ) {
                    if (textState.text.isEmpty()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = null,
                                tint = Color.Gray.copy(alpha = 0.5f),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Digite sua mensagem...",
                                color = Color.Gray.copy(alpha = 0.6f),
                                fontSize = 15.sp
                            )
                        }
                    }
                    BasicTextField(
                        value = textState,
                        onValueChange = { textState = it },
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = Color(0xFF1A1A1A),
                            fontSize = 15.sp,
                            lineHeight = 20.sp
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 5
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // ✈️ BOTÃO ENVIAR - DESIGN FUTURISTA
                val isEnabled = textState.text.isNotBlank() && isConnected

                IconButton(
                    onClick = {
                        val mensagem = textState.text.trim()
                        if (mensagem.isNotEmpty() && prestadorId > 0) {
                            Log.d("TelaChat", "📤 Enviando mensagem: $mensagem")
                            webSocketManager.sendChatMessage(
                                servicoId = servicoId.toIntOrNull() ?: 0,
                                mensagem = mensagem,
                                sender = "contratante",
                                targetUserId = prestadorId,
                                senderName = userName // Passa nome do usuário
                            )
                            textState = TextFieldValue("")
                        }
                    },
                    enabled = isEnabled,
                    modifier = Modifier
                        .size(50.dp)
                        .shadow(
                            if (isEnabled) 4.dp else 1.dp,
                            CircleShape
                        )
                        .background(
                            if (isEnabled) greenColor else Color(0xFFE0E0E0),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Enviar",
                        tint = if (isEnabled) Color.White else Color.Gray.copy(alpha = 0.5f),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// 💬 COMPONENTE DE MENSAGEM - DESIGN FUTURISTA
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
@Composable
fun ChatMessageItem(
    message: ChatMessage,
    greenColor: Color
) {
    val dateFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val timeText = dateFormat.format(Date(message.timestamp))
    val isOwn = message.isOwn

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isOwn) Arrangement.End else Arrangement.Start
    ) {
        // Avatar do prestador (apenas para mensagens recebidas)
        if (!isOwn) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(greenColor.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = greenColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        // 💬 BOLHA DA MENSAGEM - DESIGN LIMPO
        Surface(
            modifier = Modifier.widthIn(max = 260.dp),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isOwn) 16.dp else 4.dp,
                bottomEnd = if (isOwn) 4.dp else 16.dp
            ),
            color = if (isOwn) greenColor else Color.White,
            shadowElevation = if (isOwn) 3.dp else 1.dp
        ) {
            Column(
                modifier = Modifier.padding(
                    horizontal = 14.dp,
                    vertical = 10.dp
                )
            ) {
                // Nome (só para mensagens recebidas)
                if (!isOwn) {
                    Text(
                        text = message.userName,
                        color = greenColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                // Texto da mensagem
                Text(
                    text = message.mensagem,
                    color = if (isOwn) Color.White else Color(0xFF1A1A1A),
                    fontSize = 14.sp,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Hora e status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = timeText,
                        color = if (isOwn)
                            Color.White.copy(alpha = 0.7f)
                        else
                            Color.Gray.copy(alpha = 0.6f),
                        fontSize = 10.sp
                    )

                    // Check para mensagens enviadas
                    if (isOwn) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = "Enviado",
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }
        }

        // Avatar próprio (apenas para mensagens enviadas)
        if (isOwn) {
            Spacer(modifier = Modifier.width(8.dp))
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
