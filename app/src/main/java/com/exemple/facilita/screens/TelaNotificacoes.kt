package com.exemple.facilita.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items as lazyColumnItems
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.exemple.facilita.components.BottomNavBar
import com.exemple.facilita.components.NotificacaoPontoIndicador
import com.exemple.facilita.model.Notificacao
import com.exemple.facilita.model.StatusNotificacao
import com.exemple.facilita.model.TipoNotificacao
import com.exemple.facilita.viewmodel.NotificacaoViewModel

/**
 * Tela principal do centro de notificações
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaNotificacoes(
    navController: NavController,
    viewModel: NotificacaoViewModel = viewModel()
) {
    val notificacoes by viewModel.notificacoes.collectAsState()
    val notificacoesNaoLidas by viewModel.notificacoesNaoLidas.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var filtroSelecionado by remember { mutableStateOf<TipoNotificacao?>(null) }
    var textoBusca by remember { mutableStateOf("") }
    var mostrarMenuOpcoes by remember { mutableStateOf(false) }

    val notificacoesFiltradas = remember(notificacoes, filtroSelecionado, textoBusca) {
        var lista = notificacoes

        // Filtrar por tipo
        if (filtroSelecionado != null) {
            lista = lista.filter { it.tipo == filtroSelecionado }
        }

        // Filtrar por busca
        if (textoBusca.isNotBlank()) {
            lista = viewModel.buscarNotificacoes(textoBusca)
        }

        lista.filter { it.status != StatusNotificacao.ARQUIVADA }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Notificações",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (notificacoesNaoLidas > 0) {
                            Text(
                                "$notificacoesNaoLidas não lida${if (notificacoesNaoLidas > 1) "s" else ""}",
                                fontSize = 12.sp,
                                color = Color(0xFF6B6B6B)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    // Botão de opções
                    Box {
                        IconButton(onClick = { mostrarMenuOpcoes = !mostrarMenuOpcoes }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Opções")
                        }

                        DropdownMenu(
                            expanded = mostrarMenuOpcoes,
                            onDismissRequest = { mostrarMenuOpcoes = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Marcar todas como lidas") },
                                onClick = {
                                    viewModel.marcarTodasComoLidas()
                                    mostrarMenuOpcoes = false
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.DoneAll, contentDescription = null)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Configurações") },
                                onClick = {
                                    // TODO: Navegar para configurações
                                    mostrarMenuOpcoes = false
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Settings, contentDescription = null)
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = { BottomNavBar(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF7F9F7))
        ) {
            // Campo de busca
            OutlinedTextField(
                value = textoBusca,
                onValueChange = { textoBusca = it },
                placeholder = { Text("Buscar notificações...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                trailingIcon = {
                    if (textoBusca.isNotEmpty()) {
                        IconButton(onClick = { textoBusca = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Limpar")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(30.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color(0xFF5D9C68),
                    unfocusedBorderColor = Color(0xFFE0E0E0)
                ),
                singleLine = true
            )

            // Chips de filtro
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filtroSelecionado == null,
                    onClick = { filtroSelecionado = null },
                    label = { Text("Todas") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )

                FilterChip(
                    selected = filtroSelecionado == TipoNotificacao.PEDIDO_ACEITO,
                    onClick = {
                        filtroSelecionado = if (filtroSelecionado == TipoNotificacao.PEDIDO_ACEITO)
                            null else TipoNotificacao.PEDIDO_ACEITO
                    },
                    label = { Text("Pedidos") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.ShoppingBag,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )

                FilterChip(
                    selected = filtroSelecionado == TipoNotificacao.PAGAMENTO_APROVADO,
                    onClick = {
                        filtroSelecionado = if (filtroSelecionado == TipoNotificacao.PAGAMENTO_APROVADO)
                            null else TipoNotificacao.PAGAMENTO_APROVADO
                    },
                    label = { Text("Pagamentos") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Payment,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )

                FilterChip(
                    selected = filtroSelecionado == TipoNotificacao.PROMOCAO,
                    onClick = {
                        filtroSelecionado = if (filtroSelecionado == TipoNotificacao.PROMOCAO)
                            null else TipoNotificacao.PROMOCAO
                    },
                    label = { Text("Promoções") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.LocalOffer,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )
            }

            HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 1.dp)

            // Lista de notificações
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF5D9C68))
                }
            } else if (notificacoesFiltradas.isEmpty()) {
                // Estado vazio
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsOff,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = Color(0xFFCCCCCC)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Nenhuma notificação",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6B6B6B)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Você está em dia com tudo!",
                            fontSize = 14.sp,
                            color = Color(0xFF9E9E9E),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    lazyColumnItems(
                        items = notificacoesFiltradas,
                        key = { it.id }
                    ) { notificacao ->
                        NotificacaoCard(
                            notificacao = notificacao,
                            onTap = {
                                viewModel.marcarComoLida(notificacao.id)
                                notificacao.acaoPrincipal?.rota?.let { rota ->
                                    navController.navigate(rota)
                                }
                            },
                            onDismiss = {
                                viewModel.removerNotificacao(notificacao.id)
                            },
                            onArquivar = {
                                viewModel.arquivarNotificacao(notificacao.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Card individual de notificação
 */
@Composable
fun NotificacaoCard(
    notificacao: Notificacao,
    onTap: () -> Unit,
    onDismiss: () -> Unit,
    onArquivar: () -> Unit
) {
    var mostrarOpcoes by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onTap() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notificacao.status == StatusNotificacao.NAO_LIDA)
                Color(0xFFF0F7F1) else Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Indicador de não lida
            NotificacaoPontoIndicador(
                visible = notificacao.status == StatusNotificacao.NAO_LIDA,
                modifier = Modifier.padding(end = 12.dp)
            )

            // Ícone
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(notificacao.obterCorFundo()).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = notificacao.obterIcone(),
                    contentDescription = null,
                    tint = Color(notificacao.obterCorFundo()),
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
                        fontSize = 15.sp,
                        fontWeight = if (notificacao.status == StatusNotificacao.NAO_LIDA)
                            FontWeight.Bold else FontWeight.SemiBold,
                        color = Color(0xFF2C2C2C),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Menu de opções
                    Box {
                        IconButton(
                            onClick = { mostrarOpcoes = !mostrarOpcoes },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = "Opções",
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = mostrarOpcoes,
                            onDismissRequest = { mostrarOpcoes = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Arquivar") },
                                onClick = {
                                    onArquivar()
                                    mostrarOpcoes = false
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Archive, contentDescription = null)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Excluir") },
                                onClick = {
                                    onDismiss()
                                    mostrarOpcoes = false
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Delete, contentDescription = null)
                                }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = notificacao.obterTempoDecorrido(),
                        fontSize = 11.sp,
                        color = Color(0xFF9E9E9E)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notificacao.mensagem,
                    fontSize = 13.sp,
                    color = Color(0xFF6B6B6B),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )

                // Botões de ação
                if (notificacao.acaoPrincipal != null) {
                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(
                        onClick = onTap,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color(0xFF5D9C68)
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = notificacao.acaoPrincipal.texto,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

