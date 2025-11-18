
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
import kotlin.math.abs

@Composable
fun TelaInicio3(navController: NavController) {
    val infiniteTransition = rememberInfiniteTransition(label = "tela3")

    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val explosionProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "explosion"
    )

    val sparkle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sparkle"
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
            for (i in 0..30) {
                val angle = (i * 12f) * 0.0174533f
                val distance = size.minDimension * 0.35f * explosionProgress
                val particleX = center.x + cos(angle) * distance
                val particleY = center.y + sin(angle) * distance
                val particleAlpha = (1f - explosionProgress) * 0.6f

                drawCircle(
                    color = Color(0xFF06C755).copy(alpha = particleAlpha),
                    radius = 8f - (explosionProgress * 4f),
                    center = Offset(particleX, particleY)
                )
            }
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            for (i in 1..5) {
                val radius = size.minDimension * (i / 6f) * pulse
                val alpha = ((6 - i) / 15f) * sparkle
                drawCircle(
                    color = Color(0xFF06C755).copy(alpha = alpha),
                    radius = radius,
                    center = center,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
                )
            }
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            val starPositions = listOf(
                Offset(size.width * 0.15f, size.height * 0.15f),
                Offset(size.width * 0.85f, size.height * 0.15f),
                Offset(size.width * 0.15f, size.height * 0.85f),
                Offset(size.width * 0.85f, size.height * 0.85f),
                Offset(size.width * 0.5f, size.height * 0.1f)
            )

            starPositions.forEachIndexed { index, pos ->
                val starAlpha = abs(sin((sparkle + index * 0.2f) * 3.14f)) * 0.5f
                drawCircle(
                    color = Color(0xFF06C755).copy(alpha = starAlpha),
                    radius = 6f,
                    center = pos
                )
                drawCircle(
                    color = Color(0xFF06C755).copy(alpha = starAlpha * 0.3f),
                    radius = 12f,
                    center = pos
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
                    Canvas(modifier = Modifier.size(160.dp)) {
                        drawCircle(
                            color = Color(0xFF06C755).copy(alpha = sparkle * 0.3f),
                            radius = size.minDimension / 2f
                        )
                        for (i in 0..7) {
                            val angle = (i * 45f + explosionProgress * 360f) * 0.0174533f
                            val startRadius = size.minDimension * 0.3f
                            val endRadius = size.minDimension * 0.5f
                            val rayAlpha = sparkle * 0.4f

                            drawLine(
                                color = Color(0xFF06C755).copy(alpha = rayAlpha),
                                start = Offset(
                                    center.x + cos(angle) * startRadius,
                                    center.y + sin(angle) * startRadius
                                ),
                                end = Offset(
                                    center.x + cos(angle) * endRadius,
                                    center.y + sin(angle) * endRadius
                                ),
                                strokeWidth = 3f,
                                cap = androidx.compose.ui.graphics.StrokeCap.Round
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .scale(iconScale.value * pulse)
                            .background(Color(0xFF019D31), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            modifier = Modifier.size(70.dp),
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Comece Agora",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF019D31),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Facilitando seu dia a dia",
                    fontSize = 16.sp,
                    color = Color(0xFF424242),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    CleanFeatureCard(
                        icon = Icons.Default.Security,
                        title = "Segurança Garantida",
                        subtitle = "Todos os prestadores são verificados"
                    )
                    CleanFeatureCard(
                        icon = Icons.Default.Support,
                        title = "Suporte 24/7",
                        subtitle = "Estamos aqui para ajudar sempre"
                    )
                    CleanFeatureCard(
                        icon = Icons.Default.ThumbUp,
                        title = "Satisfação Garantida",
                        subtitle = "Avalie e seja avaliado"
                    )
                }
            }

            Column(
                modifier = Modifier.alpha(contentAlpha.value),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { navController.navigate("tela_login") },
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
                        text = "COMEÇAR AGORA",
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
                    PageIndicator(isActive = false)
                    PageIndicator(isActive = true)
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
fun TelaInicio3Preview() {
    val navController = rememberNavController()
    TelaInicio3(navController = navController)
}