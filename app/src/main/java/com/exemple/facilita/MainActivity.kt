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
import com.exemplo.facilita.screens.TelaBuscarServico
import com.google.android.libraries.places.api.Places

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔹 Inicializa Places uma vez aqui
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, "AIzaSyBKFwfrLdbTreqsOwnpMS9-zt9KD-HEH28")
        }

        setContent {
            val navController = rememberNavController()
            AppNavHost(navController)
        }
    }
}
@Composable
fun AppNavHost(navController: NavHostController) {
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
            TelaPedidosHistorico(navController)
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

        // Tela de aguardo de serviço
        composable(
            route = "tela_aguardo_servico/{pedidoId}/{origem}/{destino}",
            arguments = listOf(
                navArgument("pedidoId") { type = NavType.StringType },
                navArgument("origem") { type = NavType.StringType },
                navArgument("destino") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            TelaAguardoServico(
                navController = navController,
                pedidoId = backStackEntry.arguments?.getString("pedidoId"),
                origem = backStackEntry.arguments?.getString("origem"),
                destino = backStackEntry.arguments?.getString("destino")
            )
        }

    }
}
