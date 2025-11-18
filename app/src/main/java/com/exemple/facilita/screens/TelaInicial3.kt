
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
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun TelaInicio2(navController: NavController) {
    val infiniteTransition = rememberInfiniteTransition(label = "tela2")

    val waveProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "waves"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
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
                        Color(0xFFE8F5E9),
                        Color.White,
                        Color(0xFFF1F9F4)
                    )
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            for (i in 0..4) {
                val waveRadius = (size.minDimension * 0.3f) * ((waveProgress + i * 0.2f) % 1f)
                val waveAlpha = 1f - ((waveProgress + i * 0.2f) % 1f)

                drawCircle(
                    color = Color(0xFF06C755).copy(alpha = waveAlpha * 0.3f),
                    radius = waveRadius,
                    center = center,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f)
                )
            }
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            val angle1 = rotation * 0.0174533f
            val angle2 = (rotation + 120f) * 0.0174533f
            val angle3 = (rotation + 240f) * 0.0174533f
            val radius = size.minDimension * 0.25f

            drawCircle(
                color = Color(0xFF06C755).copy(alpha = 0.1f),
                radius = size.maxDimension * 0.15f,
                center = Offset(
                    center.x + cos(angle1) * radius,
                    center.y + sin(angle1) * radius
                )
            )
            drawCircle(
                color = Color(0xFF019D31).copy(alpha = 0.08f),
                radius = size.maxDimension * 0.12f,
                center = Offset(
                    center.x + cos(angle2) * radius,
                    center.y + sin(angle2) * radius
                )
            )
            drawCircle(
                color = Color(0xFF06C755).copy(alpha = 0.06f),
                radius = size.maxDimension * 0.18f,
                center = Offset(
                    center.x + cos(angle3) * radius,
                    center.y + sin(angle3) * radius
                )
            )
        }

        Canvas(modifier = Modifier.fillMaxSize().alpha(0.1f)) {
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
                        for (i in 1..3) {
                            val ringRadius = size.minDimension / 2f * (0.8f + i * 0.1f)
                            val ringAlpha = (1f - (waveProgress + i * 0.3f) % 1f) * 0.4f

                            drawCircle(
                                color = Color(0xFF06C755).copy(alpha = ringAlpha),
                                radius = ringRadius,
                                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .scale(iconScale.value)
                            .background(Color(0xFF019D31), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(70.dp),
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Acompanhamento em",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF019D31),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Tempo Real",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF424242),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    CleanFeatureCard(
                        icon = Icons.Default.Navigation,
                        title = "GPS Preciso",
                        subtitle = "Veja a localização exata do prestador"
                    )
                    CleanFeatureCard(
                        icon = Icons.Default.Notifications,
                        title = "Notificações ao Vivo",
                        subtitle = "Receba atualizações em tempo real"
                    )
                    CleanFeatureCard(
                        icon = Icons.Default.Schedule,
                        title = "Previsão de Chegada",
                        subtitle = "Saiba quando seu pedido vai chegar"
                    )
                }
            }

            Column(
                modifier = Modifier.alpha(contentAlpha.value),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { navController.navigate("tela_inicio3") },
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
                    PageIndicator(isActive = false)
                    PageIndicator(isActive = true)
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
fun TelaInicio2Preview() {
    val navController = rememberNavController()
    TelaInicio2(navController = navController)
}