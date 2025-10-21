package com.exemple.facilita.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun TelaTermos(navController: NavController) {
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF222222), Color(0xFF111111))
                )
            )
            .padding(24.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxSize(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // Título
                Text(
                    text = "Termos de Uso",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF019D31),
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .align(Alignment.CenterHorizontally)
                )

                // Texto rolável
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                        .padding(end = 8.dp)
                ) {
                    Text(
                        text = """
O presente documento tem por finalidade estabelecer os Termos de Uso do aplicativo Facilita, desenvolvido como parte de uma solução tecnológica destinada a intermediar a solicitação e a execução de serviços de compras, entregas e demais tarefas cotidianas. 

Ao realizar o cadastro e utilizar a plataforma, o usuário declara estar ciente e de acordo com todas as condições aqui descritas, reconhecendo que a utilização do sistema implica na aceitação integral dos presentes termos.

O aplicativo disponibiliza dois perfis principais de acesso: o usuário contratante, que é a pessoa responsável por solicitar os serviços, e o prestador de serviços, que é o indivíduo responsável por executá-los. Além disso, o perfil de contratante poderá ser configurado de acordo com as necessidades do usuário, disponibilizando recursos específicos de acessibilidade para idosos, pessoas com deficiência ou usuários que não apresentem tais necessidades, a fim de garantir inclusão e usabilidade para todos.

O Facilita atua exclusivamente como uma plataforma intermediadora, aproximando contratantes e prestadores, não sendo responsável pelos produtos adquiridos, pela qualidade dos itens selecionados ou pela execução direta dos serviços. 

Para maior transparência e segurança, a plataforma disponibiliza ferramentas como rastreamento do prestador em tempo real via GPS, bem como canais de comunicação internos, incluindo chat escrito, chat de voz e videochamada, possibilitando ao usuário acompanhar e participar ativamente da execução da tarefa solicitada.

Os pagamentos realizados pelo contratante ocorrem unicamente por meio da carteira digital interna do aplicativo, na qual o usuário poderá adicionar saldo previamente. O valor correspondente ao serviço será debitado automaticamente após a confirmação da entrega, sendo repassado ao prestador conforme as regras estabelecidas pela plataforma.
""".trimIndent(),
                        fontSize = 15.sp,
                        color = Color(0xFF333333),
                        lineHeight = 22.sp,
                        textAlign = TextAlign.Justify
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botões de ação
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Recusar
                    Button(
                        onClick = { navController.popBackStack() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF888888)),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Recusar", color = Color.White, fontSize = 16.sp)
                    }

                    // Aceitar
                    Button(
                        onClick = {
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("termosAceitos", true)
                            navController.popBackStack()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF019D31)),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Aceitar", color = Color.White, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}
