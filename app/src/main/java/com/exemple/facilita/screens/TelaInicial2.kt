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
                // Texto Pular no canto superior direito
                Text(
                    text = "Pular",
                    color = Color.Gray,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(start = 340.dp, top = 40.dp)
                        .clickable {
                            navController.navigate("tela_login")
                        }
                )
                Image(
                    painter = painterResource(com.exemple.facilita.R.drawable.iconmotomenu),
                    contentDescription = "Icone da moto",
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Logo agrupamento
            Image(
                painter = painterResource(com.exemple.facilita.R.drawable.agrupamento1),
                contentDescription = "Logo tela inicial",
                modifier = Modifier
                    .height(50.dp)
                    .width(45.dp)
                    .padding(top = 20.dp, bottom = 20.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                modifier = Modifier.padding(top = 20.dp),
                text = stringResource(com.exemple.facilita.R.string.bem_vindo),
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                modifier = Modifier
                    .width(250.dp)
                    .padding(top = 10.dp),
                text = stringResource(com.exemple.facilita.R.string.facilita_seu_dia),
                color = Color.White,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(50.dp))

            Button(
                onClick = { navController.navigate("tela_inicio3") },
                modifier = Modifier
                    .width(250.dp)
                    .height(60.dp),
                shape = RoundedCornerShape(50),
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
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TelaInicio1Preview() {
    val navController = rememberNavController()
    TelaInicio1(navController = navController)
}
