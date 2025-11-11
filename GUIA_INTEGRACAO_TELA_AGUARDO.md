# 🚀 GUIA RÁPIDO - Tela de Aguardo

## Como Integrar em 3 Passos

---

### 📍 PASSO 1: Adicionar Rota de Navegação

No seu arquivo de navegação (MainActivity.kt ou NavGraph.kt), adicione:

```kotlin
// Importar
import com.exemple.facilita.screens.TelaAguardoServico

// Na definição das rotas:
composable("tela_aguardo_servico") {
    TelaAguardoServico(navController = navController)
}

// OU com parâmetros:
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
```

---

### 📍 PASSO 2: Navegar Após Criar Pedido

Em `TelaMontarServico.kt` (ou onde você cria o pedido), após sucesso:

```kotlin
// Opção 1: Navegação simples
navController.navigate("tela_aguardo_servico")

// Opção 2: Com dados do pedido
navController.navigate(
    "tela_aguardo_servico/$pedidoId/$enderecoOrigem/$enderecoDestino"
)

// Opção 3: Limpar histórico de navegação
navController.navigate("tela_aguardo_servico") {
    popUpTo("tela_home") { inclusive = false }
}
```

**Exemplo completo:**

```kotlin
Button(
    onClick = {
        scope.launch {
            try {
                val response = apiService.criarPedido(pedido)
                if (response.isSuccessful) {
                    val pedidoId = response.body()?.id ?: "123"
                    
                    // Navega para tela de aguardo
                    navController.navigate(
                        "tela_aguardo_servico/$pedidoId/$origem/$destino"
                    ) {
                        popUpTo("tela_home") { inclusive = false }
                    }
                }
            } catch (e: Exception) {
                // Tratar erro
                Log.e("PEDIDO", "Erro: ${e.message}")
            }
        }
    }
) {
    Text("Confirmar Pedido")
}
```

---

### 📍 PASSO 3: Testar

1. Execute o app
2. Crie um pedido
3. A tela de aguardo aparecerá automaticamente
4. Observe as animações:
   - 3s: Prestador encontrado
   - 8s: A caminho
   - 16s: Chegou
5. Teste o botão cancelar

---

## 🔗 Conectar com API Real (Opcional)

Para receber atualizações em tempo real:

```kotlin
// No TelaAguardoServico.kt, substitua o LaunchedEffect:

LaunchedEffect(pedidoId) {
    // Opção 1: WebSocket
    webSocketManager.conectar(pedidoId).collect { update ->
        statusAtual = update.status
        prestadorNome = update.prestador.nome
        prestadorAvaliacao = update.prestador.avaliacao
        tempoEstimado = update.tempoEstimado
    }
    
    // Opção 2: Polling (requisições periódicas)
    while (statusAtual != StatusServico.CHEGOU) {
        try {
            val status = apiService.buscarStatusPedido(pedidoId)
            statusAtual = status.statusServico
            tempoEstimado = status.tempoEstimado
            // ... atualizar outros dados
        } catch (e: Exception) {
            Log.e("STATUS", "Erro: ${e.message}")
        }
        delay(5000) // Atualiza a cada 5 segundos
    }
}
```

---

## 📱 Exemplo de Fluxo Completo

```
Usuário preenche serviço
        ↓
Clica em "Confirmar Pedido"
        ↓
API cria o pedido
        ↓
[TelaAguardoServico] ← AQUI!
        ↓
Procurando prestador... (3s)
        ↓
Prestador encontrado! ✅
        ↓
Prestador a caminho (8s)
        ↓
Prestador chegou! 🎉
        ↓
[Iniciar Serviço / Chat]
```

---

## ⚙️ Parâmetros da Tela

```kotlin
TelaAguardoServico(
    navController: NavController,     // OBRIGATÓRIO
    pedidoId: String? = "12345",     // Opcional
    origem: String? = "...",          // Opcional
    destino: String? = "..."          // Opcional
)
```

---

## 🎨 Customizar (Opcional)

### Alterar cores:

Procure no código por:
```kotlin
Color(0xFF019D31)  // Verde principal
Color(0xFF06C755)  // Verde claro
```

### Alterar tempos:

```kotlin
delay(3000)  // 3 segundos → Altere aqui
```

### Alterar textos:

```kotlin
"Procurando prestador..."  // ← Seu texto aqui
```

---

## ✅ Checklist de Integração

- [ ] Adicionei a rota no NavGraph
- [ ] Importei TelaAguardoServico
- [ ] Navego para a tela após criar pedido
- [ ] Testei as animações
- [ ] Testei o botão cancelar
- [ ] (Opcional) Integrei com API real

---

## 🆘 Problemas Comuns

### Erro: "Unresolved reference TelaAguardoServico"
**Solução:** Verifique se o import está correto:
```kotlin
import com.exemple.facilita.screens.TelaAguardoServico
```

### Tela não aparece
**Solução:** Verifique se a rota está registrada no NavGraph

### Animações travadas
**Solução:** Execute em um dispositivo físico (emulador pode ficar lento)

---

## 📖 Documentação Completa

Veja: `TELA_AGUARDO_IMPLEMENTADA.md` para detalhes completos

---

**Tempo de integração:** ~5 minutos
**Dificuldade:** ⭐ Fácil

🎉 **Pronto! Sua tela de aguardo está integrada!**

