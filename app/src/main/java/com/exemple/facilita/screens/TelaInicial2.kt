import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.exemple.facilita.components.CleanFeatureCard
import com.exemple.facilita.components.PageIndicator

@Composable
fun TelaInicio1(navController: NavController) {
    val infiniteTransition = rememberInfiniteTransition(label = "tela1")

    val particleOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particles"
    )

    val greenPulse by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val contentAlpha = remember { Animatable(0f) }
    val iconScale = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        iconScale.animateTo(1.2f, spring(dampingRatio = Spring.DampingRatioLowBouncy))
        iconScale.animateTo(1f, tween(300))
        contentAlpha.animateTo(1f, tween(1000))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF1F9F4),
                        Color.White,
                        Color(0xFFE8F5E9)
                    )
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color(0xFF06C755).copy(alpha = 0.08f * greenPulse),
                radius = size.maxDimension * 0.5f,
                center = Offset(size.width * 0.7f, size.height * 0.2f)
            )
            drawCircle(
                color = Color(0xFF019D31).copy(alpha = 0.06f * greenPulse),
                radius = size.maxDimension * 0.7f,
                center = Offset(size.width * 0.3f, size.height * 0.4f)
            )
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            for (i in 0..20) {
                val xOffset = (size.width / 20f) * i
                val yProgress = (particleOffset + i * 0.05f) % 1f
                val yPos = size.height * yProgress
                val alpha = if (yProgress < 0.5f) yProgress else (1f - yProgress)

                drawCircle(
                    color = Color(0xFF06C755).copy(alpha = alpha * 0.4f),
                    radius = 6f + (i % 3) * 2f,
                    center = Offset(xOffset, yPos)
                )
            }
        }

        Canvas(modifier = Modifier.fillMaxSize().alpha(0.12f)) {
            for (i in 0..8) {
                val y = size.height * i / 8f
                drawLine(
                    color = Color(0xFF06C755),
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1.5f
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.alpha(contentAlpha.value)
            ) {
                Box(
                    modifier = Modifier.size(140.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(140.dp)) {
                        drawCircle(
                            color = Color(0xFF06C755).copy(alpha = greenPulse * 0.3f),
                            radius = size.minDimension / 2f
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .scale(iconScale.value)
                            .background(Color(0xFF019D31), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            modifier = Modifier.size(70.dp),
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Bem-Vindo ao Facilita",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF019D31),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Seu app de entregas e serviços",
                    fontSize = 16.sp,
                    color = Color(0xFF424242),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    CleanFeatureCard(
                        icon = Icons.Default.LocalShipping,
                        title = "Entregas Rápidas",
                        subtitle = "Receba suas encomendas com agilidade"
                    )
                    CleanFeatureCard(
                        icon = Icons.Default.Star,
                        title = "Prestadores Confiáveis",
                        subtitle = "Profissionais verificados e avaliados"
                    )
                    CleanFeatureCard(
                        icon = Icons.Default.Payment,
                        title = "Pagamento Seguro",
                        subtitle = "Múltiplas formas de pagamento"
                    )
                }
            }

            Column(
                modifier = Modifier.alpha(contentAlpha.value),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { navController.navigate("tela_inicio2") },
                    modifier = Modifier
                        .fillMaxWidth(0.75f)
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF019D31)
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 8.dp
                    )
                ) {
                    Text(
                        text = "CONTINUAR",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PageIndicator(isActive = true)
                    PageIndicator(isActive = false)
                    PageIndicator(isActive = false)
                }
            }
        }

        TextButton(
            onClick = { navController.navigate("tela_login") },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Text(
                text = "Pular",
                color = Color(0xFFBDBDBD),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TelaInicio1Preview() {
    val navController = rememberNavController()
    TelaInicio1(navController = navController)
}
