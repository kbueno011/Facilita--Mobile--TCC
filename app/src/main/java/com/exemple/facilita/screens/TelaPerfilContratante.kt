
package com.exemple.facilita.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.exemple.facilita.R
import com.exemple.facilita.components.BottomNavBar
import com.exemple.facilita.utils.TokenManager // Importa o TokenManager
import com.exemple.facilita.viewmodel.PerfilViewModel

@Composable
fun TelaPerfilContratante(
    navController: NavController,
    viewModel: PerfilViewModel = viewModel()
) {
    var notificacoesAtivas by remember { mutableStateOf(false) }
    val usuario by viewModel.usuario.collectAsState() // ✅ usa o novo campo do ViewModel
    val context = LocalContext.current

    // 🔑 Recupera o token salvo usando TokenManager
    val token = TokenManager.obterToken(context)

    // 🚀 Busca o perfil pelo token
    LaunchedEffect(token) {
        if (token != null) {
            viewModel.carregarPerfil("Bearer $token") // ✅ agora só o token, sem ID
        } else {
            println("⚠️ Token não encontrado. Faça login novamente.")
        }
    }

    Scaffold(bottomBar = { BottomNavBar(navController) }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .background(Color(0xFFF5F5F5)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text("Perfil", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(20.dp))

            // 🖼️ Foto de perfil
            Box(modifier = Modifier.size(120.dp), contentAlignment = Alignment.BottomEnd) {
                val imagemPerfil = usuario?.foto_perfil
                if (imagemPerfil != null) {
                    Image(
                        painter = rememberAsyncImagePainter(imagemPerfil),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.size(120.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.foto_perfil),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.size(120.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Adicionar foto",
                    tint = Color(0xFF00A651),
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(4.dp)
                        .align(Alignment.BottomEnd)
                )
            }

            Spacer(modifier = Modifier.height(25.dp))

            // 🧾 Informações do Perfil
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text("Informações do Perfil", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    PerfilInfoItem(Icons.Default.Person, usuario?.nome ?: "Carregando...") {}
                    PerfilInfoItem(Icons.Default.Email, usuario?.email ?: "--") {}
                    PerfilInfoItem(Icons.Default.Phone, usuario?.telefone ?: "--") {}
                    PerfilInfoItem(Icons.Default.DateRange, "Criado em: ${usuario?.criado_em ?: "--"}") {}
                }
            }

            Spacer(modifier = Modifier.height(25.dp))

            // ⚙️ Outras Configurações
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    Text("Outras Configurações", fontWeight = FontWeight.Bold, fontSize = 15.sp)

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lock, contentDescription = "Alterar senha")
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Alterar Senha")
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Notifications, contentDescription = "Ativar notificações")
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Ativar Notificações")
                        }
                        Switch(
                            checked = notificacoesAtivas,
                            onCheckedChange = { notificacoesAtivas = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF00A651)
                            )
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                TokenManager.limparToken(context) // Limpa o token ao sair
                                navController.navigate("tela_login") {
                                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                }
                            }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Sair")
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Sair")
                    }
                }
            }
        }
    }
}

@Composable
fun PerfilInfoItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onEdit: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = Color.Black)
            Spacer(modifier = Modifier.width(10.dp))
            Text(label, fontSize = 15.sp)
        }
        Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color.Gray, modifier = Modifier.clickable { onEdit() })
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TelaPerfilContratantePreview() {
    val navController = rememberNavController()
    TelaPerfilContratante(navController = navController)
}
