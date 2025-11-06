
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Text
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
import com.exemple.facilita.R

@Composable
fun TelaInicio2(navController: NavController) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF019D31))
    ) {
        Column (
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
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
                Image(
                    painter = painterResource(R.drawable.iconmapamenu),
                    contentDescription = "Icone da moto",
                    modifier = Modifier
                        .fillMaxSize()
                )
            }

            //FIM DO CARD

            Image(
                painter = painterResource(R.drawable.agrupamento2),
                contentDescription = "Logo tela inicial",
                modifier = Modifier
                    .height(60.dp)
                    .width(55.dp)
                    .padding(top = 20.dp, bottom = 12.dp)
            )

            //FIM DO AGRUPAMENTO (IMAGE)
            Spacer(modifier = Modifier
                .height(8.dp))

            Text(
                modifier = Modifier
                    .padding(horizontal = 32.dp),
                text = stringResource(R.string.acompanhamento),
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 12.dp),
                text = stringResource(R.string.acompanhe),
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                lineHeight = 28.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            // Botão Continuar fixo na parte inferior
            Button(
                onClick = {
                    navController.navigate("tela_inicio3")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 32.dp, vertical = 0.dp),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF019D31)
                ),
                border = BorderStroke(
                    width = 2.dp,
                    color = Color(0xFF019D31)
                )
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
fun TelaInicio2Preview() {
    val navController = rememberNavController()
    TelaInicio2(navController = navController)
}