package com.exemple.facilita.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun TelaTermos(navController: NavController) {
    val scrollState = rememberScrollState()
    val accepted = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF444444))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White, shape = MaterialTheme.shapes.medium)
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
            ) {
                Text(
                    text = "Termos de Uso",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Text(
                    text = """
O presente documento tem por finalidade estabelecer os Termos de Uso do aplicativo Facilita, desenvolvido como parte de uma solução tecnológica destinada a intermediar a solicitação e a execução de serviços de compras, entregas e demais tarefas cotidianas. 

Ao realizar o cadastro e utilizar a plataforma, o usuário declara estar ciente e de acordo com todas as condições aqui descritas, reconhecendo que a utilização do sistema implica na aceitação integral dos presentes termos.

O aplicativo disponibiliza dois perfis principais de acesso: o usuário contratante, que é a pessoa responsável por solicitar os serviços, e o prestador de serviços, que é o indivíduo responsável por executá-los. Além disso, o perfil de contratante poderá ser configurado de acordo com as necessidades do usuário, disponibilizando recursos específicos de acessibilidade para idosos, pessoas com deficiência ou usuários que não apresentem tais necessidades, a fim de garantir inclusão e usabilidade para todos.

O Facilita atua exclusivamente como uma plataforma intermediadora, aproximando contratantes e prestadores, não sendo responsável pelos produtos adquiridos, pela qualidade dos itens selecionados ou pela execução direta dos serviços. 

Para maior transparência e segurança, a plataforma disponibiliza ferramentas como rastreamento do prestador em tempo real via GPS, bem como canais de comunicação internos, incluindo chat escrito, chat de voz e videochamada, possibilitando ao usuário acompanhar e participar ativamente da execução da tarefa solicitada.

Os pagamentos realizados pelo contratante ocorrem unicamente por meio da carteira digital interna do aplicativo, na qual o usuário poderá adicionar saldo previamente. O valor correspondente ao serviço será debitado automaticamente após a confirmação da entrega, sendo repassado ao prestador conforme as regras estabelecidas pela plataforma.
""".trimIndent(),
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        // recusar → volta para login ou fecha app
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text("Recusar", color = Color.White)
                }

                Button(
                    onClick = {
                        // aceitar → continua fluxo (ex: cadastro ou home)
                        navController.navigate("tela_tipo_conta")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF019D31))
                ) {
                    Text("Aceitar", color = Color.White)
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TelaTermosPreview() {
    val navController = rememberNavController()
    TelaTermos(navController = navController)
}
