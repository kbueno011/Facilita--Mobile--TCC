import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun TelaInicio1(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF019D31))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Card com imagem
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(474.dp),
                shape = RoundedCornerShape(
                    topStart = 0.dp,
                    topEnd = 0.dp,
                    bottomEnd = 24.dp,
                    bottomStart = 24.dp
                )
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(com.exemple.facilita.R.drawable.iconmotomenu),
                        contentDescription = "Icone da moto",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Logo agrupamento
            Image(
                painter = painterResource(com.exemple.facilita.R.drawable.agrupamento1),
                contentDescription = "Logo tela inicial",
                modifier = Modifier
                    .height(60.dp)
                    .width(55.dp)
                    .padding(top = 20.dp, bottom = 12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                modifier = Modifier.padding(horizontal = 32.dp),
                text = stringResource(com.exemple.facilita.R.string.bem_vindo),
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 12.dp),
                text = stringResource(com.exemple.facilita.R.string.facilita_seu_dia),
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                lineHeight = 28.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            // Botão Continuar fixo na parte inferior
            Button(
                onClick = { navController.navigate("tela_inicio2") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 32.dp, vertical = 0.dp),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF019D31)
                ),
                border = BorderStroke(width = 2.dp, color = Color(0xFF019D31))
            ) {
                Text(
                    text = "CONTINUAR",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Botão Pular no canto superior direito
        Text(
            text = "Pular",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 48.dp, end = 24.dp)
                .clickable {
                    navController.navigate("tela_login")
                }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TelaInicio1Preview() {
    val navController = rememberNavController()
    TelaInicio1(navController = navController)
}
