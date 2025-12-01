package com.exemple.facilita

import TelaInicio1
import TelaInicio2
import TelaInicio3
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.exemple.facilita.screens.*
import com.exemple.facilita.viewmodel.EnderecoViewModel
import com.exemple.facilita.viewmodel.PedidoSharedViewModel
import com.exemplo.facilita.screens.TelaBuscarServico
import com.google.android.libraries.places.api.Places

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            android.util.Log.d("MainActivity", "🚀 Iniciando app...")

            // 🔹 Inicializa Places uma vez aqui
            if (!Places.isInitialized()) {
                android.util.Log.d("MainActivity", "📍 Inicializando Google Places...")
                Places.initialize(applicationContext, "AIzaSyBKFwfrLdbTreqsOwnpMS9-zt9KD-HEH28")
            }

            android.util.Log.d("MainActivity", "🎨 Configurando UI...")
            setContent {
                val navController = rememberNavController()
                AppNavHost(navController)
            }

            // Iniciar serviço de monitoramento de chamadas
            android.util.Log.d("MainActivity", "📞 Iniciando CallMonitorService...")
            val callServiceIntent = android.content.Intent(this, com.exemple.facilita.service.CallMonitorService::class.java)
            startService(callServiceIntent)

            android.util.Log.d("MainActivity", "✅ App iniciado com sucesso!")
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "❌ ERRO AO INICIAR: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }
}
@Composable
fun AppNavHost(navController: NavHostController) {
    android.util.Log.d("AppNavHost", "🗺️ Configurando navegação...")

    // ViewModel compartilhado para passar dados entre telas
    val pedidoSharedViewModel: PedidoSharedViewModel = viewModel()

    android.util.Log.d("AppNavHost", "✅ ViewModel criado com sucesso")

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(navController)
        }
        composable("tela_inicio1") {
            TelaInicio1(navController)
        }
        composable("tela_inicio2") {
            TelaInicio2(navController)
        }
        composable("tela_inicio3") {
            TelaInicio3(navController)
        }
        composable("tela_login") {
            TelaLogin(navController)
        }
        composable("tela_cadastro") {
            TelaCadastro(navController)
        }
        composable("tela_termos") {
            TelaTermos(navController)
        }
        composable("tela_recuperar_senha") {
            TelaRecuperacaoSenha(navController)
        }
        composable("tela_tipo_conta") {
            TelaTipoConta(navController)
        }
        composable("tela_completar_perfil_contratante") {
            TelaCompletarPerfilContratante(navController)
        }
        composable("tela_endereco") {
            val enderecoViewModel: EnderecoViewModel = viewModel()
            TelaEnderecoContent(
                navController = navController,
                viewModel = enderecoViewModel,
            )
        }
        composable("tela_home") {
            TelaHome(navController)
        }
        composable("tela_nova_senha/{codigo}") { backStackEntry ->
            val codigo = backStackEntry.arguments?.getString("codigo") ?: ""
            TelaNovaSenha(navController, codigo)
        }
        composable(
            route = "tela_servico_categoria/{categoriaNome}",
            arguments = listOf(
                navArgument("categoriaNome") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val categoriaNome = backStackEntry.arguments?.getString("categoriaNome") ?: "Farmácia"
            TelaCriarServicoCategoria(navController, categoriaNome)
        }
        composable(
            route = "tela_montar_servico/{endereco}?idCategoria={idCategoria}",
            arguments = listOf(
                navArgument("endereco") { type = NavType.StringType },
                navArgument("idCategoria") {
                    type = NavType.IntType
                    defaultValue = 1
                }
            )
        ) { backStackEntry ->
            val endereco = backStackEntry.arguments?.getString("endereco")
            val idCategoria = backStackEntry.arguments?.getInt("idCategoria") ?: 1
            TelaMontarServico(
                navController = navController,
                endereco = endereco,
                idCategoria = idCategoria
            )
        }

        composable("tela_perfil") {
            TelaPerfilContratante(navController)
        }
        composable("tela_status_pagamento") {
            TelaStatusPagamento(navController)
        }
        composable("tela_historico_pedido") {
            TelaPedidosHistorico(navController, pedidoSharedViewModel)
        }
        composable("tela_pedido_confirmado") {
            TelaPedidoConfirmado(navController)
        }
        composable("tela_buscar_categoria") {
            TelaBuscarServico(navController)
        }

        composable("tela_completar_perfil_prestador") {
            TelaCompletarPerfilPrestador(navController)
        }

        composable("tela_carteira") {
            TelaCarteira(navController)
        }

        composable("tela_ajuda_suporte") {
            TelaAjudaSuporte(navController)
        }

        composable("tela_notificacoes") {
            TelaNotificacoes(navController)
        }

        // Tela de pagamento do serviço
        composable(
            route = "tela_pagamento_servico/{servicoId}/{valorServico}/{origem}/{destino}",
            arguments = listOf(
                navArgument("servicoId") { type = NavType.StringType },
                navArgument("valorServico") { type = NavType.StringType },
                navArgument("origem") { type = NavType.StringType },
                navArgument("destino") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            TelaPagamentoServico(
                navController = navController,
                servicoId = backStackEntry.arguments?.getString("servicoId") ?: "",
                valorServico = backStackEntry.arguments?.getString("valorServico")?.toDoubleOrNull() ?: 25.0,
                origemEndereco = backStackEntry.arguments?.getString("origem") ?: "",
                destinoEndereco = backStackEntry.arguments?.getString("destino") ?: ""
            )
        }

        // Tela de aguardo de serviço
        composable(
            route = "tela_aguardo_servico/{servicoId}/{origem}/{destino}",
            arguments = listOf(
                navArgument("servicoId") { type = NavType.StringType },
                navArgument("origem") { type = NavType.StringType; defaultValue = "" },
                navArgument("destino") { type = NavType.StringType; defaultValue = "" }
            )
        ) { backStackEntry ->
            TelaAguardoServico(
                navController = navController,
                servicoId = backStackEntry.arguments?.getString("servicoId") ?: "",
                origemEndereco = backStackEntry.arguments?.getString("origem"),
                destinoEndereco = backStackEntry.arguments?.getString("destino")
            )
        }

        // Tela de rastreamento em tempo real
        composable(
            route = "tela_rastreamento_servico/{servicoId}",
            arguments = listOf(
                navArgument("servicoId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            TelaRastreamentoServico(
                navController = navController,
                servicoId = backStackEntry.arguments?.getString("servicoId") ?: ""
            )
        }

        // Tela de finalização do serviço
        composable(
            route = "tela_finalizacao/{servicoId}/{prestadorNome}/{valorServico}",
            arguments = listOf(
                navArgument("servicoId") { type = NavType.StringType },
                navArgument("prestadorNome") { type = NavType.StringType },
                navArgument("valorServico") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            TelaFinalizacaoServico(
                navController = navController,
                servicoId = backStackEntry.arguments?.getString("servicoId") ?: "0",
                prestadorNome = backStackEntry.arguments?.getString("prestadorNome") ?: "Prestador",
                valorServico = backStackEntry.arguments?.getString("valorServico") ?: "0.00"
            )
        }

        // Tela de avaliação do serviço
        composable(
            route = "tela_avaliacao/{servicoId}/{prestadorNome}/{valorServico}",
            arguments = listOf(
                navArgument("servicoId") { type = NavType.IntType },
                navArgument("prestadorNome") { type = NavType.StringType },
                navArgument("valorServico") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            TelaAvaliacaoCliente(
                navController = navController,
                servicoId = backStackEntry.arguments?.getInt("servicoId") ?: 0,
                clienteNome = backStackEntry.arguments?.getString("prestadorNome") ?: "Prestador",
                valorServico = backStackEntry.arguments?.getString("valorServico") ?: "0.00"
            )
        }

        // Tela de detalhes do pedido concluído
        composable(route = "detalhes_pedido_concluido") {
            TelaDetalhesPedidoConcluido(navController = navController, sharedViewModel = pedidoSharedViewModel)
        }

        // Tela de corrida em andamento (tempo real)
        composable(
            route = "tela_corrida_andamento/{servicoId}",
            arguments = listOf(
                navArgument("servicoId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            TelaCorridaEmAndamento(
                navController = navController,
                servicoId = backStackEntry.arguments?.getString("servicoId") ?: ""
            )
        }

        // Tela de Chat em tempo real
        composable(
            route = "tela_chat/{servicoId}/{prestadorNome}/{prestadorTelefone}/{prestadorPlaca}/{prestadorId}",
            arguments = listOf(
                navArgument("servicoId") { type = NavType.StringType },
                navArgument("prestadorNome") { type = NavType.StringType },
                navArgument("prestadorTelefone") { type = NavType.StringType },
                navArgument("prestadorPlaca") { type = NavType.StringType },
                navArgument("prestadorId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            TelaChat(
                navController = navController,
                servicoId = backStackEntry.arguments?.getString("servicoId") ?: "",
                prestadorNome = backStackEntry.arguments?.getString("prestadorNome") ?: "Prestador",
                prestadorTelefone = backStackEntry.arguments?.getString("prestadorTelefone") ?: "",
                prestadorPlaca = backStackEntry.arguments?.getString("prestadorPlaca") ?: "",
                prestadorId = backStackEntry.arguments?.getInt("prestadorId") ?: 0
            )
        }

        // Tela de chamada de vídeo
        composable(
            route = "video_call/{servicoId}/{prestadorId}/{prestadorNome}",
            arguments = listOf(
                navArgument("servicoId") { type = NavType.StringType },
                navArgument("prestadorId") { type = NavType.StringType },
                navArgument("prestadorNome") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            TelaVideoCall(
                navController = navController,
                servicoId = backStackEntry.arguments?.getString("servicoId") ?: "",
                prestadorId = backStackEntry.arguments?.getString("prestadorId") ?: "",
                prestadorNome = backStackEntry.arguments?.getString("prestadorNome") ?: "Prestador"
            )
        }

        // Tela de chamada de áudio
        composable(
            route = "audio_call/{servicoId}/{prestadorId}/{prestadorNome}",
            arguments = listOf(
                navArgument("servicoId") { type = NavType.StringType },
                navArgument("prestadorId") { type = NavType.StringType },
                navArgument("prestadorNome") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            TelaAudioCall(
                navController = navController,
                servicoId = backStackEntry.arguments?.getString("servicoId") ?: "",
                prestadorId = backStackEntry.arguments?.getString("prestadorId") ?: "",
                prestadorNome = backStackEntry.arguments?.getString("prestadorNome") ?: "Prestador"
            )
        }

    }
}
