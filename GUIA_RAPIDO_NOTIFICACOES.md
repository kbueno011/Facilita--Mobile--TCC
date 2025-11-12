# ⚡ GUIA RÁPIDO - ATIVAR NOTIFICAÇÕES

## 🎯 3 PASSOS SIMPLES

### PASSO 1: Adicionar Rota no NavGraph

No arquivo onde você define as rotas (MainActivity.kt ou NavGraph.kt):

```kotlin
composable("tela_notificacoes") {
    TelaNotificacoes(navController = navController)
}
```

---

### PASSO 2: Adicionar Ícone com Badge na TelaHome

Abra `TelaHome.kt` e adicione:

```kotlin
import com.exemple.facilita.components.IconButtonComBadge
import com.exemple.facilita.viewmodel.NotificacaoViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.exemple.facilita.utils.TokenManager

@Composable
fun TelaHome(navController: NavController) {
    val context = LocalContext.current
    val notifViewModel: NotificacaoViewModel = viewModel()
    val contadorNaoLidas by notifViewModel.contadorNaoLidas.collectAsState()
    val token = TokenManager.obterToken(context) ?: ""
    
    // Inicia monitoramento de notificações
    LaunchedEffect(Unit) {
        if (token.isNotEmpty()) {
            notifViewModel.iniciarMonitoramento(token, 30000) // 30 segundos
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Facilita") },
                actions = {
                    // Ícone de notificações com badge
                    IconButtonComBadge(
                        icon = Icons.Default.Notifications,
                        contentDescription = "Notificações",
                        badgeCount = contadorNaoLidas,
                        onClick = {
                            navController.navigate("tela_notificacoes")
                        }
                    )
                }
            )
        }
    ) { padding ->
        // Seu conteúdo da tela...
    }
}
```

---

### PASSO 3: Testar!

1. ✅ Build do projeto
2. ✅ Abra o app
3. ✅ Veja o ícone de notificação no topo
4. ✅ Crie um serviço
5. ✅ Quando prestador aceitar, notificação aparecerá
6. ✅ Badge vermelho mostrará quantidade

---

## ✅ PRONTO!

O sistema de notificações está funcionando:
- 🔔 Polling automático a cada 30 segundos
- 🔴 Badge vermelho com contador
- 📱 Tela completa de notificações
- ✓ Marcar como lida
- 🗑️ Deletar notificações
- 🔗 Navegar para serviço

---

## 🎨 VISUAL FINAL

```
┌─────────────────────────────┐
│ 📱 Facilita        🔔 (3)   │ ← Badge vermelho com 3 não lidas
├─────────────────────────────┤
│                             │
│  Conteúdo da Home           │
│                             │
└─────────────────────────────┘
```

Clica no 🔔:

```
┌─────────────────────────────┐
│ ← Notificações         ✓✓  │
├─────────────────────────────┤
│ 🟢 Serviço aceito!        ● │
│    João aceitou            │
│    5m atrás                │
├─────────────────────────────┤
│ 🔵 Serviço iniciado        │
│    Prestador começou       │
│    1h atrás                │
├─────────────────────────────┤
│ 🟢 Concluído!         ✓    │
│    Serviço finalizado      │
│    2h atrás                │
└─────────────────────────────┘
```

---

**Implementação:** ✅ COMPLETA  
**Tempo:** 5 minutos  
**Dificuldade:** ⭐ (Muito Fácil)

