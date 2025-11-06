package com.exemple.facilita.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

data class PerguntaFrequente(
    val pergunta: String,
    val resposta: String,
    val categoria: String
)

data class OpcaoContato(
    val titulo: String,
    val descricao: String,
    val icone: ImageVector,
    val cor: Color,
    val acao: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaAjudaSuporte(navController: NavController) {
    var selectedTab by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var showSearchBar by remember { mutableStateOf(false) }

    // Animação de entrada
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }

    val tabs = listOf("Ajuda", "Contato", "FAQ")

    val perguntas = listOf(
        PerguntaFrequente(
            "Como solicitar uma entrega?",
            "Acesse a tela inicial, clique em 'Monte seu serviço', preencha os dados de origem e destino, selecione o tipo de veículo e confirme o pedido.",
            "Pedidos"
        ),
        PerguntaFrequente(
            "Como funciona o pagamento?",
            "Você pode pagar usando saldo da carteira digital, cartão de crédito/débito ou PIX. O valor é debitado apenas após a confirmação da entrega.",
            "Pagamentos"
        ),
        PerguntaFrequente(
            "Posso cancelar uma entrega?",
            "Sim! Você pode cancelar uma entrega antes do entregador iniciá-la. Após o início, entre em contato com o suporte para avaliar a situação.",
            "Pedidos"
        ),
        PerguntaFrequente(
            "Como adicionar saldo à carteira?",
            "Vá em Carteira > Depositar, escolha o valor e selecione a forma de pagamento (PIX, boleto ou cartão).",
            "Carteira"
        ),
        PerguntaFrequente(
            "O que fazer se o entregador não chegar?",
            "Entre em contato imediatamente com nosso suporte através do chat ou telefone. Vamos resolver rapidamente!",
            "Problemas"
        ),
        PerguntaFrequente(
            "Como acompanhar minha entrega em tempo real?",
            "Na tela de pedidos ativos, você pode ver a localização do entregador em tempo real no mapa.",
            "Rastreamento"
        ),
        PerguntaFrequente(
            "Posso agendar entregas futuras?",
            "Sim! Ao criar um pedido, selecione a opção 'Agendar' e escolha data e horário desejados.",
            "Pedidos"
        ),
        PerguntaFrequente(
            "Como avaliar um entregador?",
            "Após a entrega, você receberá uma solicitação automática para avaliar. Também pode avaliar através do histórico de pedidos.",
            "Avaliações"
        )
    )

    val opcoesContato = listOf(
        OpcaoContato(
            "Chat ao Vivo",
            "Resposta em menos de 2 minutos",
            Icons.Default.ChatBubble,
            Color(0xFF00B14F),
            "chat"
        ),
        OpcaoContato(
            "WhatsApp",
            "(11) 98765-4321",
            Icons.AutoMirrored.Filled.Message,
            Color(0xFF25D366),
            "whatsapp"
        ),
        OpcaoContato(
            "Telefone",
            "0800 123 4567",
            Icons.Default.Phone,
            Color(0xFF019D31),
            "phone"
        ),
        OpcaoContato(
            "Email",
            "suporte@facilita.com",
            Icons.Default.Email,
            Color(0xFF3C604B),
            "email"
        )
    )

    Scaffold(
        topBar = {
            AnimatedHeader(
                onBackClick = { navController.popBackStack() },
                onSearchClick = { showSearchBar = !showSearchBar },
                visible = visible
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Fundo com efeito animado
            AnimatedBackground()

            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Barra de pesquisa animada
                AnimatedVisibility(
                    visible = showSearchBar,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        onClear = { searchQuery = "" }
                    )
                }

                // Tabs
                CustomTabRow(
                    selectedTab = selectedTab,
                    tabs = tabs,
                    onTabSelected = { selectedTab = it },
                    visible = visible
                )

                // Conteúdo das tabs
                when (selectedTab) {
                    0 -> AjudaTab(visible = visible)
                    1 -> ContatoTab(opcoesContato = opcoesContato, visible = visible)
                    2 -> FAQTab(
                        perguntas = perguntas.filter {
                            searchQuery.isEmpty() ||
                            it.pergunta.contains(searchQuery, ignoreCase = true) ||
                            it.resposta.contains(searchQuery, ignoreCase = true)
                        },
                        visible = visible
                    )
                }
            }
        }
    }
}

// Header Animado
@Composable
private fun AnimatedHeader(
    onBackClick: () -> Unit,
    onSearchClick: () -> Unit,
    visible: Boolean
) {
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(600)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .alpha(alpha)
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF019D31), Color(0xFF00B14F))
                )
            )
    ) {
        // Efeitos decorativos de fundo
        Canvas(modifier = Modifier.fillMaxSize()) {
            val circleCount = 8
            for (i in 0 until circleCount) {
                val angle = (i * 360f / circleCount) * PI.toFloat() / 180f
                val radius = size.minDimension * 0.4f
                val x = size.width / 2 + cos(angle) * radius
                val y = size.height / 2 + sin(angle) * radius

                drawCircle(
                    color = Color.White.copy(alpha = 0.05f),
                    radius = 40f,
                    center = Offset(x, y)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Top bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = Color.White
                    )
                }

                IconButton(
                    onClick = onSearchClick,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Pesquisar",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Título com ícone animado
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                var rotation by remember { mutableStateOf(0f) }
                LaunchedEffect(Unit) {
                    while (true) {
                        delay(3000)
                        rotation += 360f
                    }
                }

                val animatedRotation by animateFloatAsState(
                    targetValue = rotation,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )

                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .rotate(animatedRotation)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.HeadsetMic,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .size(32.dp)
                            .rotate(-animatedRotation)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "Central de Ajuda",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "Estamos aqui para você",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }
    }
}

// Fundo Animado
@Composable
private fun AnimatedBackground() {
    val infiniteTransition = rememberInfiniteTransition()

    val offset1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .blur(100.dp)
    ) {
        val angle1 = offset1 * PI.toFloat() / 180f
        val x1 = size.width * 0.2f + cos(angle1) * size.width * 0.1f
        val y1 = size.height * 0.3f + sin(angle1) * size.height * 0.1f

        drawCircle(
            color = Color(0xFF019D31).copy(alpha = 0.05f),
            radius = 200f,
            center = Offset(x1, y1)
        )

        val angle2 = -offset1 * PI.toFloat() / 180f
        val x2 = size.width * 0.8f + cos(angle2) * size.width * 0.1f
        val y2 = size.height * 0.7f + sin(angle2) * size.height * 0.1f

        drawCircle(
            color = Color(0xFF00B14F).copy(alpha = 0.05f),
            radius = 180f,
            center = Offset(x2, y2)
        )
    }
}

// Barra de Pesquisa
@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Pesquisar dúvidas...", color = Color.Gray) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = Color(0xFF019D31)
                )
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = onClear) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Limpar",
                            tint = Color.Gray
                        )
                    }
                }
            },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF019D31),
                unfocusedBorderColor = Color(0xFFE0E0E0)
            ),
            singleLine = true
        )
    }
}

// Tab Row Customizada
@Composable
private fun CustomTabRow(
    selectedTab: Int,
    tabs: List<String>,
    onTabSelected: (Int) -> Unit,
    visible: Boolean
) {
    var tabsVisible by remember { mutableStateOf(false) }
    LaunchedEffect(visible) {
        if (visible) {
            delay(300)
            tabsVisible = true
        }
    }

    AnimatedVisibility(
        visible = tabsVisible,
        enter = fadeIn() + slideInVertically(initialOffsetY = { -it })
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            tabs.forEachIndexed { index, title ->
                TabItem(
                    title = title,
                    isSelected = selectedTab == index,
                    onClick = { onTabSelected(index) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

// Item de Tab
@Composable
private fun TabItem(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Box(
        modifier = modifier
            .height(44.dp)
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected)
                    Brush.horizontalGradient(
                        listOf(Color(0xFF019D31), Color(0xFF00B14F))
                    )
                else
                    Brush.horizontalGradient(
                        listOf(Color(0xFFF5F5F5), Color(0xFFF5F5F5))
                    )
            )
            .border(
                width = if (isSelected) 0.dp else 1.dp,
                color = if (isSelected) Color.Transparent else Color(0xFFE0E0E0),
                shape = RoundedCornerShape(12.dp)
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        pressed = true
                        try {
                            awaitRelease()
                        } finally {
                            pressed = false
                        }
                    },
                    onTap = { onClick() }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = if (isSelected) Color.White else Color(0xFF6D6D6D),
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 14.sp
        )
    }
}

// Tab de Ajuda
@Composable
private fun AjudaTab(visible: Boolean) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8)),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Card de boas-vindas
        item {
            AnimatedCard(visible = visible, delay = 0) {
                WelcomeCard()
            }
        }

        // Atalhos rápidos
        item {
            Text(
                text = "Atalhos Rápidos",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D2D2D),
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        itemsIndexed(
            listOf(
                Triple("Como fazer um pedido", Icons.Default.ShoppingCart, "Passo a passo completo"),
                Triple("Métodos de pagamento", Icons.Default.Payment, "Todas as opções disponíveis"),
                Triple("Rastreamento em tempo real", Icons.Default.LocationOn, "Acompanhe sua entrega"),
                Triple("Políticas de cancelamento", Icons.Default.Cancel, "Saiba mais sobre cancelamentos")
            )
        ) { index, item ->
            AnimatedCard(visible = visible, delay = (index + 1) * 100) {
                QuickAccessCard(
                    titulo = item.first,
                    icone = item.second,
                    descricao = item.third
                )
            }
        }

        // Tutoriais em vídeo
        item {
            Text(
                text = "Tutoriais em Vídeo",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D2D2D),
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
        }

        item {
            AnimatedCard(visible = visible, delay = 500) {
                VideoTutorialCard()
            }
        }
    }
}

// Tab de Contato
@Composable
private fun ContatoTab(opcoesContato: List<OpcaoContato>, visible: Boolean) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8)),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Escolha como prefere falar conosco",
                fontSize = 16.sp,
                color = Color(0xFF6D6D6D),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        }

        itemsIndexed(opcoesContato) { index, opcao ->
            AnimatedCard(visible = visible, delay = index * 150) {
                ContactOptionCard(opcao = opcao)
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            AnimatedCard(visible = visible, delay = 600) {
                HorarioAtendimentoCard()
            }
        }
    }
}

// Tab de FAQ
@Composable
private fun FAQTab(perguntas: List<PerguntaFrequente>, visible: Boolean) {
    var expandedIndex by remember { mutableStateOf<Int?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8)),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Perguntas Frequentes",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D2D2D),
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        if (perguntas.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Nenhuma pergunta encontrada",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        } else {
            itemsIndexed(perguntas) { index, pergunta ->
                AnimatedCard(visible = visible, delay = index * 80) {
                    FAQItem(
                        pergunta = pergunta,
                        isExpanded = expandedIndex == index,
                        onClick = {
                            expandedIndex = if (expandedIndex == index) null else index
                        }
                    )
                }
            }
        }
    }
}

// Card Animado Genérico
@Composable
private fun AnimatedCard(
    visible: Boolean,
    delay: Int = 0,
    content: @Composable () -> Unit
) {
    var itemVisible by remember { mutableStateOf(false) }
    LaunchedEffect(visible) {
        if (visible) {
            delay(delay.toLong())
            itemVisible = true
        }
    }

    AnimatedVisibility(
        visible = itemVisible,
        enter = fadeIn(animationSpec = tween(500)) +
                slideInVertically(
                    initialOffsetY = { it / 3 },
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                )
    ) {
        content()
    }
}

// Card de Boas-Vindas
@Composable
private fun WelcomeCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF019D31), Color(0xFF00B14F))
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = Color(0xFFFFEB3B),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Dica",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Use a barra de pesquisa para encontrar rapidamente a resposta que você procura!",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.95f),
                    lineHeight = 20.sp
                )
            }
        }
    }
}

// Card de Acesso Rápido
@Composable
private fun QuickAccessCard(titulo: String, icone: ImageVector, descricao: String) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        pressed = true
                        try {
                            awaitRelease()
                        } finally {
                            pressed = false
                        }
                    },
                    onTap = { /* navegar */ }
                )
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color(0xFF019D31).copy(alpha = 0.1f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icone,
                    contentDescription = null,
                    tint = Color(0xFF019D31),
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = titulo,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D2D2D)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = descricao,
                    fontSize = 12.sp,
                    color = Color(0xFF6D6D6D)
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = Color(0xFF019D31),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// Card de Tutorial em Vídeo
@Composable
private fun VideoTutorialCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF3C604B), Color(0xFF019D31))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Ícone de play animado
                val infiniteTransition = rememberInfiniteTransition()
                val scale by infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = 1.1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1000, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    )
                )

                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .scale(scale)
                        .background(Color.White.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Como usar o Facilita",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D2D2D)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Assista ao tutorial completo • 3 min",
                    fontSize = 12.sp,
                    color = Color(0xFF6D6D6D)
                )
            }
        }
    }
}

// Card de Opção de Contato
@Composable
private fun ContactOptionCard(opcao: OpcaoContato) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        pressed = true
                        try {
                            awaitRelease()
                        } finally {
                            pressed = false
                        }
                    },
                    onTap = { /* ação de contato */ }
                )
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ícone animado
            val infiniteTransition = rememberInfiniteTransition()
            val iconScale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 1.15f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1200, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )

            Box(
                modifier = Modifier
                    .size(64.dp)
                    .scale(iconScale)
                    .background(opcao.cor.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = opcao.icone,
                    contentDescription = null,
                    tint = opcao.cor,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = opcao.titulo,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D2D2D)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = opcao.descricao,
                    fontSize = 13.sp,
                    color = Color(0xFF6D6D6D)
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = opcao.cor,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

// Card de Horário de Atendimento
@Composable
private fun HorarioAtendimentoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = Color(0xFF019D31),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Horário de Atendimento",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D2D2D)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            HorarioRow("Segunda a Sexta", "08:00 - 22:00")
            Spacer(modifier = Modifier.height(8.dp))
            HorarioRow("Sábado", "09:00 - 18:00")
            Spacer(modifier = Modifier.height(8.dp))
            HorarioRow("Domingo e Feriados", "10:00 - 16:00")

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(color = Color(0xFFE0E0E0))

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color(0xFF00FF00), CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Atendimento online disponível 24/7",
                    fontSize = 13.sp,
                    color = Color(0xFF019D31),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// Linha de Horário
@Composable
private fun HorarioRow(dia: String, horario: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = dia,
            fontSize = 14.sp,
            color = Color(0xFF6D6D6D)
        )
        Text(
            text = horario,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF2D2D2D)
        )
    }
}

// Item de FAQ
@Composable
private fun FAQItem(
    pergunta: PerguntaFrequente,
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        pressed = true
                        try {
                            awaitRelease()
                        } finally {
                            pressed = false
                        }
                    },
                    onTap = { onClick() }
                )
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isExpanded) Color.White else Color(0xFFFAFAFA)
        ),
        elevation = CardDefaults.cardElevation(if (isExpanded) 4.dp else 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .padding(top = 6.dp)
                            .background(Color(0xFF019D31), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = pergunta.pergunta,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D2D2D),
                        lineHeight = 20.sp
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color(0xFF019D31),
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(rotationAngle)
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(animationSpec = spring()) + fadeIn(),
                exit = shrinkVertically(animationSpec = spring()) + fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))

                    Divider(color = Color(0xFFE0E0E0))

                    Spacer(modifier = Modifier.height(12.dp))

                    // Categoria tag
                    Box(
                        modifier = Modifier
                            .background(
                                Color(0xFF019D31).copy(alpha = 0.1f),
                                RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = pergunta.categoria,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF019D31)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = pergunta.resposta,
                        fontSize = 14.sp,
                        color = Color(0xFF6D6D6D),
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Botão de feedback
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "Foi útil?",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.ThumbUp,
                            contentDescription = "Sim",
                            tint = Color(0xFF019D31),
                            modifier = Modifier
                                .size(18.dp)
                                .clickable { /* feedback positivo */ }
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(
                            imageVector = Icons.Default.ThumbDown,
                            contentDescription = "Não",
                            tint = Color.Gray,
                            modifier = Modifier
                                .size(18.dp)
                                .clickable { /* feedback negativo */ }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TelaAjudaSuportePreview() {
    val navController = rememberNavController()
    TelaAjudaSuporte(navController)
}

