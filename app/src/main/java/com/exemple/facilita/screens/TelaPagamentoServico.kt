package com.exemple.facilita.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.exemple.facilita.utils.TokenManager
import com.exemple.facilita.viewmodel.CarteiraViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaPagamentoServico(
    navController: NavController,
    servicoId: String,
    valorServico: Double,
    origemEndereco: String,
    destinoEndereco: String
) {
    val context = LocalContext.current
    val viewModel: CarteiraViewModel = viewModel()
    val scope = rememberCoroutineScope()

    val saldo by viewModel.saldo.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var processandoPagamento by remember { mutableStateOf(false) }
    var mostrarDialogSaldoInsuficiente by remember { mutableStateOf(false) }
    var pagamentoConfirmado by remember { mutableStateOf(false) }

    val token = TokenManager.obterToken(context) ?: ""

    // Animações
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Carregar saldo
    LaunchedEffect(Unit) {
        if (token.isNotEmpty()) {
            viewModel.carregarSaldo(token)
        }
    }

    val formatoMoeda = remember { NumberFormat.getCurrencyInstance(Locale("pt", "BR")) }
    val temSaldoSuficiente = saldo.saldoDisponivel >= valorServico

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F7))) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header com cores da aplicação
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(88.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF3C604B), Color(0xFF00B14F))
                        ),
                        shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Voltar",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Pagamento do Serviço",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // Card de Saldo
                AnimatedVisibility(
                    visible = !pagamentoConfirmado,
                    enter = fadeIn() + expandVertically()
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Ícone
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .background(
                                        Color(0xFF00B14F).copy(alpha = 0.1f),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalanceWallet,
                                    contentDescription = null,
                                    tint = Color(0xFF00B14F),
                                    modifier = Modifier.size(40.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Saldo Disponível",
                                fontSize = 14.sp,
                                color = Color(0xFF6D6D6D),
                                fontWeight = FontWeight.Medium
                            )

                            Text(
                                text = formatoMoeda.format(saldo.saldoDisponivel),
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (temSaldoSuficiente) Color(0xFF00B14F) else Color(0xFFFF6B6B)
                            )
                        }
                    }
                }

                // Card do Serviço
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Detalhes do Serviço",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2D2D2D)
                            )
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = Color(0xFF00B14F),
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Origem
                        Row(
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(
                                        Color(0xFF00B14F).copy(alpha = 0.1f),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(Color(0xFF00B14F), CircleShape)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "ORIGEM",
                                    fontSize = 11.sp,
                                    color = Color(0xFF00B14F),
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = origemEndereco,
                                    fontSize = 14.sp,
                                    color = Color(0xFF2D2D2D),
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }

                        // Linha conectora
                        Box(
                            modifier = Modifier
                                .padding(start = 15.dp, top = 4.dp, bottom = 4.dp)
                                .width(2.dp)
                                .height(30.dp)
                                .background(Color(0xFF00B14F).copy(alpha = 0.3f))
                        )

                        // Destino
                        Row(
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(
                                        Color(0xFF00B14F).copy(alpha = 0.1f),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = Color(0xFF00B14F),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "DESTINO",
                                    fontSize = 11.sp,
                                    color = Color(0xFF00B14F),
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = destinoEndereco,
                                    fontSize = 14.sp,
                                    color = Color(0xFF2D2D2D),
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        HorizontalDivider(
                            color = Color(0xFFE0E0E0),
                            thickness = 1.dp
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Valor Total
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "VALOR TOTAL",
                                    fontSize = 12.sp,
                                    color = Color(0xFF6D6D6D),
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "Será debitado da carteira",
                                    fontSize = 11.sp,
                                    color = Color(0xFF6D6D6D)
                                )
                            }
                            Text(
                                text = formatoMoeda.format(valorServico),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00B14F)
                            )
                        }
                    }
                }

                // Animação de sucesso
                AnimatedVisibility(
                    visible = pagamentoConfirmado,
                    enter = fadeIn() + scaleIn()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 40.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .background(
                                    Color(0xFF00B14F).copy(alpha = 0.1f),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF00B14F),
                                modifier = Modifier.size(80.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Pagamento Confirmado!",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2D2D2D),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Redirecionando...",
                            fontSize = 14.sp,
                            color = Color(0xFF6D6D6D),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        // Botão de Pagamento Flutuante
        if (!pagamentoConfirmado) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                Color(0xFFF5F5F7)
                            )
                        )
                    )
                    .padding(20.dp)
            ) {
                Button(
                    onClick = {
                        if (temSaldoSuficiente) {
                            processandoPagamento = true
                            scope.launch {
                                try {
                                    // Debita da carteira usando o ViewModel
                                    viewModel.debitarParaServico(
                                        valorServico = valorServico,
                                        servicoId = servicoId,
                                        descricaoServico = "Pagamento do serviço #$servicoId",
                                        onSuccess = {
                                            // Débito bem-sucedido
                                            pagamentoConfirmado = true
                                            scope.launch {
                                                delay(2000)

                                                Toast.makeText(
                                                    context,
                                                    "Pagamento realizado com sucesso!",
                                                    Toast.LENGTH_SHORT
                                                ).show()

                                                // Recarrega saldo para garantir atualização
                                                viewModel.carregarSaldo(token)

                                                navController.navigate("tela_aguardo_servico/$servicoId/$origemEndereco/$destinoEndereco") {
                                                    popUpTo("tela_home") { inclusive = false }
                                                }
                                            }
                                        },
                                        onError = { erro ->
                                            processandoPagamento = false
                                            Toast.makeText(
                                                context,
                                                "Erro: $erro",
                                                Toast.LENGTH_LONG
                                            ).show()
                                            Log.e("PAGAMENTO_ERRO", erro)
                                        }
                                    )
                                } catch (e: Exception) {
                                    processandoPagamento = false
                                    Toast.makeText(
                                        context,
                                        "Erro: ${e.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    Log.e("PAGAMENTO_ERRO", "Exceção ao processar pagamento", e)
                                }
                            }
                        } else {
                            mostrarDialogSaldoInsuficiente = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    enabled = !processandoPagamento,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (temSaldoSuficiente) Color(0xFF00B14F) else Color(0xFFFF6B6B),
                        disabledContainerColor = Color(0xFFE0E0E0)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    if (processandoPagamento) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (temSaldoSuficiente) Icons.Default.CheckCircle else Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = if (temSaldoSuficiente) "Confirmar Pagamento" else "Saldo Insuficiente",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Loading overlay
        if (isLoading || processandoPagamento) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFF00B14F),
                        strokeWidth = 4.dp,
                        modifier = Modifier.size(60.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Processando...",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }

    // Dialog Saldo Insuficiente
    if (mostrarDialogSaldoInsuficiente) {
        AlertDialog(
            onDismissRequest = { mostrarDialogSaldoInsuficiente = false },
            containerColor = Color.White,
            icon = {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            Color(0xFFFF6B6B).copy(alpha = 0.1f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color(0xFFFF6B6B),
                        modifier = Modifier.size(48.dp)
                    )
                }
            },
            title = {
                Text(
                    text = "Saldo Insuficiente",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D2D2D),
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Você precisa de ${formatoMoeda.format(valorServico)} mas tem apenas ${formatoMoeda.format(saldo.saldoDisponivel)}",
                        fontSize = 14.sp,
                        color = Color(0xFF6D6D6D),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Faltam ${formatoMoeda.format(valorServico - saldo.saldoDisponivel)}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF6B6B)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogSaldoInsuficiente = false
                        navController.navigate("tela_carteira") {
                            launchSingleTop = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00B14F)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Adicionar Saldo",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { mostrarDialogSaldoInsuficiente = false }
                ) {
                    Text("Cancelar", color = Color(0xFF6D6D6D))
                }
            }
        )
    }
}

