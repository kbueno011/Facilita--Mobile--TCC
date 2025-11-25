package com.exemple.facilita.screens

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.exemple.facilita.network.WebSocketManager
import com.exemple.facilita.utils.TokenManager
import com.exemple.facilita.viewmodel.ServicoViewModel
import com.exemple.facilita.network.DirectionsService
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.pow
import kotlin.math.sqrt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaRastreamentoServico(
    navController: NavController,
    servicoId: String
) {
    val context = LocalContext.current
    val viewModel: ServicoViewModel = viewModel()

    val servico by viewModel.servico.collectAsState()
    val servicoPedido by viewModel.servicoPedido.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val token = TokenManager.obterToken(context) ?: ""
    val userId = TokenManager.obterUserId(context) ?: 0

    var mostrarDialogoCancelar by remember { mutableStateOf(false) }
    var mostrarDetalhes by remember { mutableStateOf(false) }

    // WebSocket Manager para rastreamento em tempo real
    val webSocketManager = remember { WebSocketManager.getInstance() }
    val isSocketConnected by webSocketManager.isConnected.collectAsState()
    val locationUpdate by webSocketManager.locationUpdate.collectAsState()

    // Posições no mapa - SEMPRE atualizado via WebSocket em tempo real
    var prestadorLat by remember { mutableStateOf(0.0) }
    var prestadorLng by remember { mutableStateOf(0.0) }
    var prestadorVisivel by remember { mutableStateOf(false) }

    // Log da configuração inicial (mas não usa API para posição)
    LaunchedEffect(servico?.prestador) {
        servico?.prestador?.let { prestador ->
            Log.d("TelaRastreamento", "")
            Log.d("TelaRastreamento", "╔════════════════════════════════════════════════╗")
            Log.d("TelaRastreamento", "║  🚗 PRESTADOR CONECTADO AO SERVIÇO            ║")
            Log.d("TelaRastreamento", "╚════════════════════════════════════════════════╝")
            Log.d("TelaRastreamento", "   👤 Nome: ${prestador.usuario?.nome ?: "N/A"}")
            Log.d("TelaRastreamento", "   📞 Telefone: ${prestador.usuario?.telefone ?: "N/A"}")
            Log.d("TelaRastreamento", "")
            Log.d("TelaRastreamento", "📡 LOCALIZAÇÃO EM TEMPO REAL")
            Log.d("TelaRastreamento", "   • A posição será atualizada via WebSocket")
            Log.d("TelaRastreamento", "   • Evento: location_updated")
            Log.d("TelaRastreamento", "   • Intervalo: ~5 segundos")
            Log.d("TelaRastreamento", "")
            Log.d("TelaRastreamento", "⏳ Aguardando primeira posição via WebSocket...")
            Log.d("TelaRastreamento", "╚════════════════════════════════════════════════╝")
            Log.d("TelaRastreamento", "")
        }
    }

    // Estado para a rota completa (com paradas)
    var routePoints by remember { mutableStateOf<List<LatLng>>(emptyList()) }
    var distanciaTexto by remember { mutableStateOf("") }
    var duracaoTexto by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

    // Log quando o serviço é carregado
    LaunchedEffect(servico, servicoPedido) {
        Log.d("TelaRastreamento", "📦 Dados do serviço carregados:")
        Log.d("TelaRastreamento", "   Serviço ID: ${servico?.id}")
        Log.d("TelaRastreamento", "   Status: ${servico?.status}")
        Log.d("TelaRastreamento", "   Prestador: ${servico?.prestador?.usuario?.nome}")
        Log.d("TelaRastreamento", "   Localização destino: ${servico?.localizacao?.latitude}, ${servico?.localizacao?.longitude}")
        Log.d("TelaRastreamento", "   ServicoPedido: ${servicoPedido != null}")
        Log.d("TelaRastreamento", "   Paradas no ServicoPedido: ${servicoPedido?.paradas?.size ?: 0}")
    }

    // Paradas do serviço (ordenadas)
    val paradas = remember(servicoPedido) {
        val paradasList = servicoPedido?.paradas?.sortedBy { it.ordem } ?: emptyList()
        Log.d("TelaRastreamento", "🔄 Paradas recalculadas: ${paradasList.size}")
        paradasList
    }

    // Pontos da rota: origem, paradas intermediárias, destino
    val origem = remember(paradas) {
        paradas.firstOrNull { it.tipo == "origem" }
    }

    val paradasIntermediarias = remember(paradas) {
        paradas.filter { it.tipo == "parada" }
    }

    val destino = remember(paradas) {
        paradas.lastOrNull { it.tipo == "destino" }
    }

    // Atualiza posição quando recebe do WebSocket - COM VALIDAÇÃO MELHORADA
    LaunchedEffect(locationUpdate) {
        locationUpdate?.let { update ->
            Log.d("TelaRastreamento", "")
            Log.d("TelaRastreamento", "╔════════════════════════════════════════════════╗")
            Log.d("TelaRastreamento", "║  📡 ATUALIZAÇÃO DE LOCALIZAÇÃO RECEBIDA       ║")
            Log.d("TelaRastreamento", "╚════════════════════════════════════════════════╝")
            Log.d("TelaRastreamento", "   🆔 ServicoId recebido: ${update.servicoId}")
            Log.d("TelaRastreamento", "   🎯 ServicoId esperado: $servicoId")
            Log.d("TelaRastreamento", "   🌍 Latitude: ${update.latitude}")
            Log.d("TelaRastreamento", "   🌍 Longitude: ${update.longitude}")
            Log.d("TelaRastreamento", "   👤 Prestador: ${update.prestadorName}")
            Log.d("TelaRastreamento", "   ⏰ Timestamp: ${update.timestamp}")
            Log.d("TelaRastreamento", "")

            // Validar se é para este serviço
            val servicoIdMatch = update.servicoId.toString() == servicoId
            Log.d("TelaRastreamento", "🔍 Validações:")
            Log.d("TelaRastreamento", "   • Serviço correto? $servicoIdMatch")

            if (servicoIdMatch) {
                // Validar se as coordenadas são válidas (não 0,0)
                val coordenadasValidas = update.latitude != 0.0 && update.longitude != 0.0
                Log.d("TelaRastreamento", "   • Coordenadas válidas? $coordenadasValidas")

                if (coordenadasValidas) {
                    val posAnteriorLat = prestadorLat
                    val posAnteriorLng = prestadorLng
                    val primeiraAtualizacao = !prestadorVisivel

                    // ATUALIZA A POSIÇÃO
                    prestadorLat = update.latitude
                    prestadorLng = update.longitude
                    prestadorVisivel = true

                    val distanciaMovida = sqrt(
                        (update.latitude - posAnteriorLat).pow(2.0) +
                        (update.longitude - posAnteriorLng).pow(2.0)
                    ) * 111000 // Converte para metros

                    Log.d("TelaRastreamento", "")
                    Log.d("TelaRastreamento", "✅ ✅ ✅ MARCADOR DO PRESTADOR ATUALIZADO! ✅ ✅ ✅")
                    Log.d("TelaRastreamento", "")
                    if (primeiraAtualizacao) {
                        Log.d("TelaRastreamento", "🎉 PRIMEIRA ATUALIZAÇÃO! Marcador agora VISÍVEL no mapa!")
                    }
                    Log.d("TelaRastreamento", "📍 Posição anterior: $posAnteriorLat, $posAnteriorLng")
                    Log.d("TelaRastreamento", "📍 Nova posição: $prestadorLat, $prestadorLng")
                    Log.d("TelaRastreamento", "📏 Distância movida: ${String.format("%.2f", distanciaMovida)} metros")
                    Log.d("TelaRastreamento", "")
                    Log.d("TelaRastreamento", "🗺️ MARCADOR:")
                    Log.d("TelaRastreamento", "   • Tipo: Círculo azul pulsante (estilo Uber)")
                    Log.d("TelaRastreamento", "   • Visível: SIM")
                    Log.d("TelaRastreamento", "   • Coordenadas: LatLng($prestadorLat, $prestadorLng)")
                    Log.d("TelaRastreamento", "")
                    Log.d("TelaRastreamento", "🎥 Câmera seguirá automaticamente o prestador")
                    Log.d("TelaRastreamento", "╚════════════════════════════════════════════════╝")
                } else {
                    Log.w("TelaRastreamento", "")
                    Log.w("TelaRastreamento", "⚠️ COORDENADAS INVÁLIDAS RECEBIDAS (0,0)")
                    Log.w("TelaRastreamento", "")
                    Log.w("TelaRastreamento", "🔍 Possíveis causas:")
                    Log.w("TelaRastreamento", "   • GPS do prestador está desativado")
                    Log.w("TelaRastreamento", "   • Prestador negou permissão de localização")
                    Log.w("TelaRastreamento", "   • Sinal GPS fraco")
                    Log.w("TelaRastreamento", "")
                    Log.w("TelaRastreamento", "⏳ Aguardando coordenadas válidas...")
                    Log.w("TelaRastreamento", "╚════════════════════════════════════════════════╝")
                }
            } else {
                Log.w("TelaRastreamento", "")
                Log.w("TelaRastreamento", "⚠️ UPDATE IGNORADO - Serviço diferente")
                Log.w("TelaRastreamento", "   Esperado: $servicoId")
                Log.w("TelaRastreamento", "   Recebido: ${update.servicoId}")
                Log.w("TelaRastreamento", "╚════════════════════════════════════════════════╝")
            }
            Log.d("TelaRastreamento", "")
        }
    }

    val prestadorPos = LatLng(prestadorLat, prestadorLng)


    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(prestadorPos, 17f) // Mais próximo!
    }

    // Conecta ao WebSocket automaticamente
    LaunchedEffect(servicoId, userId) {
        if (userId > 0) {
            Log.d("TelaRastreamento", "🔌 Conectando ao WebSocket...")
            webSocketManager.connect(
                userId = userId,
                userType = "contratante",
                userName = TokenManager.obterNomeUsuario(context) ?: "Usuário"
            )
            delay(1000) // Aguarda conexão estabilizar
            webSocketManager.joinServico(servicoId)
            Log.d("TelaRastreamento", "✅ Entrou na sala do serviço: $servicoId")
        }
    }

    // Inicia o monitoramento via API (apenas uma vez)
    LaunchedEffect(Unit) {
        if (token.isNotEmpty() && servicoId.isNotEmpty()) {
            Log.d("TelaRastreamento", "🔍 Iniciando monitoramento do serviço #$servicoId")
            viewModel.iniciarMonitoramento(token, servicoId)
        }
    }

    // Monitora mudanças de status (apenas CONCLUIDO e CANCELADO)
    LaunchedEffect(servico?.status) {
        val status = servico?.status
        Log.d("TelaRastreamento", "📊 Status atual: $status")

        when (status) {
            "CONCLUIDO" -> {
                Toast.makeText(context, "✅ Serviço concluído com sucesso!", Toast.LENGTH_LONG).show()
                webSocketManager.disconnect()
                delay(2000)
                navController.navigate("tela_home") {
                    popUpTo("tela_home") { inclusive = true }
                }
            }
            "CANCELADO" -> {
                Toast.makeText(context, "❌ Serviço cancelado", Toast.LENGTH_SHORT).show()
                webSocketManager.disconnect()
                delay(500)
                navController.navigate("tela_home") {
                    popUpTo("tela_home") { inclusive = true }
                }
            }
            "ACEITO", "EM_ANDAMENTO" -> {
                // Mantém na tela de rastreamento - não navega
                Log.d("TelaRastreamento", "✅ Serviço ativo - permanecendo na tela")
            }
        }
    }

    // Busca a rota completa quando as paradas ou posição do prestador mudam
    LaunchedEffect(paradas, prestadorLat, prestadorLng, servico) {
        coroutineScope.launch {
            Log.d("TelaRastreamento", "🗺️ Iniciando busca de rota...")
            Log.d("TelaRastreamento", "   Paradas: ${paradas.size}")
            Log.d("TelaRastreamento", "   Prestador: $prestadorLat, $prestadorLng")

            // Log detalhado das paradas
            if (paradas.isNotEmpty()) {
                paradas.forEach { parada ->
                    Log.d("TelaRastreamento", "   Parada ${parada.ordem}: ${parada.tipo} - ${parada.lat}, ${parada.lng}")
                }
            }

            // CASO 1: Tem paradas definidas pela API (origem, paradas, destino)
            if (paradas.isNotEmpty() && origem != null && destino != null) {
                Log.d("TelaRastreamento", "📍 Usando paradas da API")

                // Monta a lista de waypoints (paradas intermediárias)
                val waypoints = paradasIntermediarias.map { parada ->
                    LatLng(parada.lat, parada.lng)
                }

                Log.d("TelaRastreamento", "   Origem: ${origem.lat}, ${origem.lng}")
                waypoints.forEachIndexed { index, waypoint ->
                    Log.d("TelaRastreamento", "   Waypoint $index: ${waypoint.latitude}, ${waypoint.longitude}")
                }
                Log.d("TelaRastreamento", "   Destino: ${destino.lat}, ${destino.lng}")

                val route = DirectionsService.getRoute(
                    origin = LatLng(origem.lat, origem.lng),
                    destination = LatLng(destino.lat, destino.lng),
                    waypoints = waypoints
                )

                route?.let {
                    routePoints = it.points
                    distanciaTexto = it.distanceText
                    duracaoTexto = it.durationText
                    Log.d("TelaRastreamento", "✅ Rota com paradas atualizada: ${it.points.size} pontos, " +
                            "${waypoints.size} waypoints, ${it.distanceText}, ${it.durationText}")

                    // Ajusta a câmera para mostrar a rota completa
                    if (it.points.isNotEmpty()) {
                        val boundsBuilder = LatLngBounds.Builder()
                        it.points.forEach { point -> boundsBuilder.include(point) }
                        // Adiciona também as posições das paradas
                        paradas.forEach { parada ->
                            boundsBuilder.include(LatLng(parada.lat, parada.lng))
                        }
                        val bounds = boundsBuilder.build()
                        cameraPositionState.animate(
                            update = CameraUpdateFactory.newLatLngBounds(bounds, 80), // Padding menor = mais próximo
                            durationMs = 1000 // Animação mais rápida
                        )
                    }
                } ?: run {
                    Log.e("TelaRastreamento", "❌❌❌ ERRO: API do Google não retornou rota!")
                    Log.e("TelaRastreamento", "Verifique:")
                    Log.e("TelaRastreamento", "1. Chave da API está correta?")
                    Log.e("TelaRastreamento", "2. Directions API está habilitada?")
                    Log.e("TelaRastreamento", "3. Há erro de rede?")
                    Log.e("TelaRastreamento", "Veja logs do DirectionsService acima")
                }
            }
            // CASO 2: Não tem paradas, usa prestador atual -> destino
            else {
                val servicoAtual = servico
                val localizacao = servicoAtual?.localizacao

                if (localizacao?.latitude != null && localizacao.longitude != null) {
                    Log.d("TelaRastreamento", "📍 Usando rota simples: Prestador -> Destino")
                    val destinoLat = localizacao.latitude
                    val destinoLng = localizacao.longitude

                    Log.d("TelaRastreamento", "   Prestador (origem): $prestadorLat, $prestadorLng")
                    Log.d("TelaRastreamento", "   Destino: $destinoLat, $destinoLng")

                    val route = DirectionsService.getRoute(
                        origin = LatLng(prestadorLat, prestadorLng),
                        destination = LatLng(destinoLat, destinoLng)
                    )

                    route?.let {
                        routePoints = it.points
                        distanciaTexto = it.distanceText
                        duracaoTexto = it.durationText
                        Log.d("TelaRastreamento", "✅ Rota simples atualizada: ${it.points.size} pontos, " +
                                "${it.distanceText}, ${it.durationText}")

                        // Ajusta câmera (mais próximo e rápido)
                        if (it.points.isNotEmpty()) {
                            val boundsBuilder = LatLngBounds.Builder()
                            it.points.forEach { point -> boundsBuilder.include(point) }
                            val bounds = boundsBuilder.build()
                            cameraPositionState.animate(
                                update = CameraUpdateFactory.newLatLngBounds(bounds, 80), // Padding menor
                                durationMs = 1000 // Animação mais rápida
                            )
                        }
                    } ?: run {
                        Log.e("TelaRastreamento", "❌❌❌ ERRO: API do Google não retornou rota!")
                        Log.e("TelaRastreamento", "Veja logs do DirectionsService para detalhes")
                    }
                } else {
                    Log.e("TelaRastreamento", "❌ Sem dados suficientes para traçar rota")
                    Log.e("TelaRastreamento", "   Paradas: ${paradas.size}")
                    Log.e("TelaRastreamento", "   Localizacao: $localizacao")
                }
            }
        }
    }

    // Atualiza câmera suavemente quando prestador se move - MELHORADO
    var cameraJaFoiCentralizada by remember { mutableStateOf(false) }

    LaunchedEffect(prestadorLat, prestadorLng, routePoints) {
        if (prestadorLat != 0.0 && prestadorLng != 0.0) {
            Log.d("TelaRastreamento", "🎥 Atualizando câmera para posição: $prestadorLat, $prestadorLng")

            if (routePoints.isEmpty() || !cameraJaFoiCentralizada) {
                // Primeira vez ou sem rota: centra com zoom adequado
                cameraPositionState.animate(
                    update = CameraUpdateFactory.newLatLngZoom(prestadorPos, 16f),
                    durationMs = 1000
                )
                cameraJaFoiCentralizada = true
                Log.d("TelaRastreamento", "   Câmera centralizada inicial")
            } else {
                // Movimento suave seguindo o prestador (sem mudar zoom)
                cameraPositionState.animate(
                    update = CameraUpdateFactory.newLatLng(prestadorPos),
                    durationMs = 800 // Animação fluida
                )
                Log.d("TelaRastreamento", "   Câmera seguindo movimento")
            }
        }
    }

    // NÃO desconecta WebSocket ao sair - o chat também usa a mesma conexão!
    // A conexão será mantida enquanto o serviço estiver em andamento
    DisposableEffect(Unit) {
        onDispose {
            Log.d("TelaRastreamento", "📱 Saindo da tela (WebSocket permanece ativo)")
            // NÃO chama webSocketManager.disconnect()
            // Motivo: Chat e outras telas precisam da mesma conexão
        }
    }

    val prestadorNome = servico?.prestador?.usuario?.nome ?: "Prestador"
    val prestadorTelefone = servico?.prestador?.usuario?.telefone ?: ""
    val tempoEstimado = viewModel.calcularTempoEstimado()

    // Animação de pulse para o indicador de conexão
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Mapa com visual moderno e interações suaves
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = false,
                mapType = MapType.NORMAL,
                isTrafficEnabled = false, // Sem tráfego para visual limpo
                isIndoorEnabled = true
            ),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = false,
                compassEnabled = false, // Sem bússola para visual mais limpo
                mapToolbarEnabled = false, // Sem toolbar do Google
                scrollGesturesEnabled = true,
                zoomGesturesEnabled = true,
                tiltGesturesEnabled = true, // Permitir inclinação
                rotationGesturesEnabled = true,
                scrollGesturesEnabledDuringRotateOrZoom = true
            )
        ) {
            // Desenha a rota (Polyline) - Estilo FACILITA (Verde moderno)
            if (routePoints.isNotEmpty()) {
                // Linha de fundo (borda escura para profundidade)
                Polyline(
                    points = routePoints,
                    color = Color(0xFF006400),
                    width = 12f,
                    geodesic = true
                )

                // Linha principal (VERDE FACILITA vibrante)
                Polyline(
                    points = routePoints,
                    color = Color(0xFF00C853), // Verde principal do app
                    width = 8f,
                    geodesic = true
                )

                // Linha central (branco fino para destaque)
                Polyline(
                    points = routePoints,
                    color = Color.White.copy(alpha = 0.7f),
                    width = 2f,
                    geodesic = true
                )
            }

            // 🚗 MARCADOR DO PRESTADOR - Design Premium Estilo Uber/99
            if (prestadorVisivel && prestadorLat != 0.0 && prestadorLng != 0.0) {
                Log.d("TelaRastreamento", "🎨 Desenhando marcador do prestador em: $prestadorLat, $prestadorLng")

                // ══════════════════════════════════════════════════════════
                // CAMADA 1: Halo Pulsante (Efeito de Radar)
                // ══════════════════════════════════════════════════════════
                Circle(
                    center = prestadorPos,
                    radius = 80.0 * pulseAlpha, // Raio grande pulsante
                    fillColor = Color(0x2000C853), // Verde translúcido
                    strokeColor = Color.Transparent,
                    strokeWidth = 0f
                )

                // ══════════════════════════════════════════════════════════
                // CAMADA 2: Círculo Médio (Profundidade)
                // ══════════════════════════════════════════════════════════
                Circle(
                    center = prestadorPos,
                    radius = 45.0,
                    fillColor = Color(0x4000C853), // Verde médio
                    strokeColor = Color.Transparent,
                    strokeWidth = 0f
                )

                // ══════════════════════════════════════════════════════════
                // CAMADA 3: Círculo Principal Verde (FACILITA Brand)
                // ══════════════════════════════════════════════════════════
                Circle(
                    center = prestadorPos,
                    radius = 28.0,
                    fillColor = Color(0xFF00C853), // Verde principal FACILITA
                    strokeColor = Color.White,
                    strokeWidth = 6f // Borda branca grossa
                )

                // ══════════════════════════════════════════════════════════
                // CAMADA 4: Sombra Interna (Profundidade)
                // ══════════════════════════════════════════════════════════
                Circle(
                    center = prestadorPos,
                    radius = 22.0,
                    fillColor = Color(0xE000A038), // Verde escuro
                    strokeColor = Color.Transparent,
                    strokeWidth = 0f
                )

                // ══════════════════════════════════════════════════════════
                // CAMADA 5: Ícone Central (Veículo)
                // ══════════════════════════════════════════════════════════
                Circle(
                    center = prestadorPos,
                    radius = 14.0,
                    fillColor = Color.White,
                    strokeColor = Color(0xFF00C853),
                    strokeWidth = 3f
                )

                // ══════════════════════════════════════════════════════════
                // CAMADA 6: Indicador de Direção (Movimento)
                // ══════════════════════════════════════════════════════════
                Circle(
                    center = LatLng(prestadorPos.latitude + 0.00008, prestadorPos.longitude),
                    radius = 6.0,
                    fillColor = Color(0xFF00FF00), // Verde brilhante
                    strokeColor = Color.White,
                    strokeWidth = 2.5f
                )

                // ══════════════════════════════════════════════════════════
                // CAMADA 7: Ponto Central (Precisão)
                // ══════════════════════════════════════════════════════════
                Circle(
                    center = prestadorPos,
                    radius = 4.0,
                    fillColor = Color(0xFF00C853),
                    strokeColor = Color.Transparent,
                    strokeWidth = 0f
                )
            } else if (!prestadorVisivel) {
                Log.w("TelaRastreamento", "⚠️ Marcador do prestador NÃO VISÍVEL - aguardando primeira localização")
            }

            // Marcadores das paradas - ESTILO MODERNO FACILITA
            if (paradas.isNotEmpty()) {
                Log.d("TelaRastreamento", "🎯 Desenhando ${paradas.size} marcadores modernos")

                paradas.forEach { parada ->
                    val markerPos = LatLng(parada.lat, parada.lng)

                    when (parada.tipo) {
                        "origem" -> {
                            // Origem - Círculo verde vibrante com halo
                            Circle(
                                center = markerPos,
                                radius = 30.0,
                                fillColor = Color(0x4000C853),
                                strokeColor = Color.Transparent,
                                strokeWidth = 0f
                            )
                            Circle(
                                center = markerPos,
                                radius = 18.0,
                                fillColor = Color(0xFF00C853),
                                strokeColor = Color.White,
                                strokeWidth = 5f
                            )
                            Circle(
                                center = markerPos,
                                radius = 8.0,
                                fillColor = Color.White,
                                strokeColor = Color.Transparent,
                                strokeWidth = 0f
                            )
                            Log.d("TelaRastreamento", "   🟢 Origem (círculo verde)")
                        }
                        "parada" -> {
                            // Parada - Círculo branco com borda verde grossa
                            Circle(
                                center = markerPos,
                                radius = 22.0,
                                fillColor = Color(0x4000C853),
                                strokeColor = Color.Transparent,
                                strokeWidth = 0f
                            )
                            Circle(
                                center = markerPos,
                                radius = 14.0,
                                fillColor = Color.White,
                                strokeColor = Color(0xFF00C853),
                                strokeWidth = 5f
                            )
                            Circle(
                                center = markerPos,
                                radius = 5.0,
                                fillColor = Color(0xFF00C853),
                                strokeColor = Color.Transparent,
                                strokeWidth = 0f
                            )
                            Log.d("TelaRastreamento", "   ⚪ Parada ${parada.ordem} (círculo branco)")
                        }
                        "destino" -> {
                            // Destino - Pin vermelho moderno estilo Google Maps
                            Circle(
                                center = markerPos,
                                radius = 35.0,
                                fillColor = Color(0x40FF1744),
                                strokeColor = Color.Transparent,
                                strokeWidth = 0f
                            )
                            Circle(
                                center = markerPos,
                                radius = 20.0,
                                fillColor = Color(0xFFFF1744),
                                strokeColor = Color.White,
                                strokeWidth = 5f
                            )
                            // Ponto central branco
                            Circle(
                                center = markerPos,
                                radius = 7.0,
                                fillColor = Color.White,
                                strokeColor = Color.Transparent,
                                strokeWidth = 0f
                            )
                            // Adiciona marcador tradicional também para o snippet
                            Marker(
                                state = MarkerState(position = markerPos),
                                title = "📍 Destino Final",
                                snippet = parada.enderecoCompleto,
                                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED),
                                visible = false // Invisível, apenas para info
                            )
                            Log.d("TelaRastreamento", "   🔴 Destino (pin vermelho)")
                        }
                    }
                }
            } else {
                // Fallback: marcador simples do destino
                Log.d("TelaRastreamento", "📍 Sem paradas, usando marcador de destino simples")
                val servicoAtualFallback = servico
                val localizacaoFallback = servicoAtualFallback?.localizacao

                if (localizacaoFallback?.latitude != null && localizacaoFallback.longitude != null) {
                    val finalDestinoPos = LatLng(
                        localizacaoFallback.latitude,
                        localizacaoFallback.longitude
                    )
                    Marker(
                        state = MarkerState(position = finalDestinoPos),
                        title = "Destino",
                        snippet = localizacaoFallback.endereco ?: "",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                    )
                } else {
                    Log.e("TelaRastreamento", "❌ Sem localização de destino disponível")
                }
            }
        }

        // Header moderno com indicador de tempo real
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .statusBarsPadding()
                .align(Alignment.TopCenter),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.96f)
            ),
            elevation = CardDefaults.cardElevation(12.dp)
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
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .size(42.dp)
                            .background(Color(0xFF019D31).copy(alpha = 0.1f), CircleShape)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Voltar",
                            tint = Color(0xFF019D31),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Serviço em andamento",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2D2D2D)
                        )

                        // Indicador de conexão em tempo real PULSANTE
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            // Status da conexão WebSocket
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        if (isSocketConnected) Color(0xFF00FF00).copy(alpha = pulseAlpha)
                                        else Color(0xFFFF0000),
                                        CircleShape
                                    )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isSocketConnected) "🟢 Conectado" else "🔴 Offline",
                                fontSize = 11.sp,
                                color = if (isSocketConnected) Color(0xFF019D31) else Color(0xFFFF0000),
                                fontWeight = FontWeight.Medium
                            )

                            // Separador
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "•",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.width(8.dp))

                            // Status do rastreamento do prestador - MELHORADO
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        if (prestadorVisivel) Color(0xFF00C853).copy(alpha = pulseAlpha)
                                        else if (isSocketConnected) Color(0xFFFFAB00).copy(alpha = pulseAlpha)
                                        else Color(0xFFFF0000),
                                        CircleShape
                                    )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = when {
                                    prestadorVisivel -> "🚗 Rastreando"
                                    isSocketConnected -> "⏳ Aguardando GPS"
                                    else -> "❌ Offline"
                                },
                                fontSize = 11.sp,
                                color = when {
                                    prestadorVisivel -> Color(0xFF00C853)
                                    isSocketConnected -> Color(0xFFFFAB00)
                                    else -> Color(0xFFFF0000)
                                },
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Mostra distância e tempo da rota em tempo real
                        if (duracaoTexto.isNotEmpty()) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                Text(
                                    text = "📍 $distanciaTexto",
                                    fontSize = 12.sp,
                                    color = Color(0xFF019D31),
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "⏱️ $duracaoTexto",
                                    fontSize = 12.sp,
                                    color = Color(0xFF019D31),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        } else if (tempoEstimado > 0) {
                            Text(
                                text = "⏱️ Chega em ~$tempoEstimado min",
                                fontSize = 13.sp,
                                color = Color(0xFF019D31),
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = { mostrarDetalhes = !mostrarDetalhes },
                        modifier = Modifier
                            .size(42.dp)
                            .background(Color(0xFF019D31).copy(alpha = 0.1f), CircleShape)
                    ) {
                        Icon(
                            if (mostrarDetalhes) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Detalhes",
                            tint = Color(0xFF019D31),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Detalhes expandíveis (estilo Uber)
                if (mostrarDetalhes) {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0xFFE0E0E0))
                    Spacer(modifier = Modifier.height(12.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        InfoRow(
                            icon = Icons.Default.Category,
                            label = "Categoria",
                            value = servico?.categoria?.nome ?: "N/A"
                        )
                        InfoRow(
                            icon = Icons.Default.AttachMoney,
                            label = "Valor",
                            value = "R$ ${servico?.valor ?: "0,00"}"
                        )
                        InfoRow(
                            icon = Icons.Default.LocationOn,
                            label = "Destino",
                            value = servico?.localizacao?.endereco ?: "N/A"
                        )
                    }
                }
            }
        }

        // Card inferior estilo Uber com informações completas
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .navigationBarsPadding(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Linha decorativa no topo (estilo Uber)
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .background(Color(0xFFE0E0E0), RoundedCornerShape(2.dp))
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Cabeçalho do prestador com borda gradiente
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    // Avatar com borda gradiente verde
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .border(
                                width = 3.dp,
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFF019D31), Color(0xFF06C755))
                                ),
                                shape = CircleShape
                            )
                            .padding(3.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFF019D31), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = prestadorNome,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2D2D2D)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Avaliação com 5 estrelas visuais
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            repeat(5) { index ->
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    tint = if (index < (servico?.prestador?.avaliacao?.toInt() ?: 5))
                                        Color(0xFFFFD700) else Color(0xFFE0E0E0),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${servico?.prestador?.avaliacao ?: "5.0"}",
                                fontSize = 13.sp,
                                color = Color(0xFF6D6D6D),
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Telefone do prestador
                        if (prestadorTelefone.isNotEmpty()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Phone,
                                    contentDescription = null,
                                    tint = Color(0xFF6D6D6D),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = prestadorTelefone,
                                    fontSize = 12.sp,
                                    color = Color(0xFF6D6D6D)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botões de ação (Ligar e Chat) lado a lado
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Botão LIGAR (funcional)
                    Button(
                        onClick = {
                            if (prestadorTelefone.isNotEmpty()) {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:$prestadorTelefone")
                                }
                                context.startActivity(intent)
                            } else {
                                Toast.makeText(context, "Telefone não disponível", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF019D31)
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 4.dp
                        )
                    ) {
                        Icon(
                            Icons.Default.Phone,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ligar", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }

                    // Botão Chat (funcional)
                    OutlinedButton(
                        onClick = {
                            val prestadorIdValue = servico?.prestador?.id ?: 0
                            val prestadorPlaca = servico?.prestador?.veiculo?.placa ?: ""

                            Log.d("TelaRastreamento", "💬 Abrindo chat:")
                            Log.d("TelaRastreamento", "   ServicoId: $servicoId")
                            Log.d("TelaRastreamento", "   PrestadorId: $prestadorIdValue")
                            Log.d("TelaRastreamento", "   PrestadorNome: $prestadorNome")

                            navController.navigate(
                                "tela_chat/$servicoId/$prestadorNome/$prestadorTelefone/$prestadorPlaca/$prestadorIdValue"
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF019D31)
                        ),
                        border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF019D31))
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Message,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Chat", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 1.dp)

                Spacer(modifier = Modifier.height(20.dp))

                // Informações do veículo (se disponível)
                servico?.prestador?.veiculo?.let { veiculo ->
                    VeiculoSection(
                        marca = veiculo.marca ?: "N/A",
                        modelo = veiculo.modelo ?: "N/A",
                        placa = veiculo.placa ?: "N/A",
                        cor = veiculo.cor ?: "N/A",
                        ano = veiculo.ano?.toString() ?: "N/A"
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                    HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // Detalhes do serviço
                ServicoSection(
                    status = when (servico?.status) {
                        "EM_ANDAMENTO" -> "Em andamento"
                        "CONCLUIDO" -> "Concluído"
                        else -> "Aguardando"
                    },
                    categoria = servico?.categoria?.nome ?: "N/A",
                    valor = "R$ ${servico?.valor ?: "0,00"}",
                    descricao = servico?.descricao
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Botão cancelar redesenhado
                OutlinedButton(
                    onClick = { mostrarDialogoCancelar = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFFF4444)
                    ),
                    border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFF4444))
                ) {
                    Icon(
                        Icons.Default.Cancel,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        "Cancelar Serviço",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Loading overlay
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF00B14F))
            }
        }
    }

    // Dialog de cancelamento
    if (mostrarDialogoCancelar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoCancelar = false },
            icon = {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFFF6B6B),
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text("Cancelar Serviço?", fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "Tem certeza que deseja cancelar este serviço? Esta ação não pode ser desfeita.",
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.cancelarServico(
                            token = token,
                            servicoId = servicoId,
                            onSuccess = {
                                mostrarDialogoCancelar = false
                                Toast.makeText(context, "Serviço cancelado", Toast.LENGTH_SHORT).show()
                                navController.navigate("tela_home") {
                                    popUpTo("tela_home") { inclusive = true }
                                }
                            },
                            onError = { erro ->
                                Toast.makeText(context, erro, Toast.LENGTH_LONG).show()
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF6B6B)
                    )
                ) {
                    Text("Sim, Cancelar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoCancelar = false }) {
                    Text("Não")
                }
            }
        )
    }
}

// Componentes auxiliares estilo Uber

@Composable
fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = Color(0xFF019D31),
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$label: ",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF6D6D6D)
        )
        Text(
            text = value,
            fontSize = 13.sp,
            color = Color(0xFF2D2D2D),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun VeiculoSection(
    marca: String,
    modelo: String,
    placa: String,
    cor: String,
    ano: String
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(Color(0xFF019D31).copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.DirectionsCar,
                    contentDescription = null,
                    tint = Color(0xFF019D31),
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "🚗 Veículo",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D2D2D)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF5F5F5))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            VeiculoInfoRow("Modelo", "$marca $modelo")
            VeiculoInfoRow("Placa", placa)
            VeiculoInfoRow("Cor", cor)
            VeiculoInfoRow("Ano", ano)
        }
    }
}

@Composable
fun VeiculoInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF6D6D6D),
            modifier = Modifier.weight(0.4f)
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF2D2D2D),
            textAlign = TextAlign.End,
            modifier = Modifier.weight(0.6f)
        )
    }
}

@Composable
fun ServicoSection(
    status: String,
    categoria: String,
    valor: String,
    descricao: String?
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(Color(0xFF019D31).copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = null,
                    tint = Color(0xFF019D31),
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "📋 Detalhes do Serviço",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D2D2D)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF5F5F5))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            VeiculoInfoRow("Status", status)
            VeiculoInfoRow("Categoria", categoria)
            VeiculoInfoRow("Valor", valor)
            if (!descricao.isNullOrEmpty()) {
                Column {
                    Text(
                        text = "Descrição",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF6D6D6D)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = descricao,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF2D2D2D)
                    )
                }
            }
        }
    }
}

