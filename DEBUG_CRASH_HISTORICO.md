# 🔧 DEBUG - CRASH NO HISTÓRICO

## 📋 SITUAÇÃO ATUAL

O app ainda está crashando. Vamos identificar o erro exato.

---

## 🔍 PASSO A PASSO PARA DEBUG

### 1. Conectar o Dispositivo e Ver Logs

Abra um terminal/prompt separado e execute:

```cmd
# Se tiver adb configurado
adb logcat -c  # Limpa logs antigos
adb logcat | findstr "DetalhesPedido TelaHistorico PedidoCache AndroidRuntime FATAL"

# OU use o Android Studio
# Run > View > Tool Windows > Logcat
```

### 2. Reproduzir o Erro

1. Abra o app
2. Vá para "Histórico de Pedidos"
3. Clique em um pedido
4. **Observe os logs no terminal**

### 3. O Que Procurar nos Logs

#### ✅ Logs Esperados (Sucesso):
```
TelaHistorico: 🔍 Clicado no pedido #123 - Status: CONCLUIDO
PedidoCache: ✅ Pedido #123 armazenado em cache
TelaHistorico: ✅ Navegação iniciada
DetalhesPedido: 🔍 Buscando pedido #123
PedidoCache: 📦 Recuperando pedido do cache: 123
DetalhesPedido: ✅ Pedido #123 encontrado no cache
```

#### ❌ Logs de Erro (O que procurar):
```
FATAL EXCEPTION: main
AndroidRuntime: FATAL EXCEPTION: main
java.lang.RuntimeException: Unable to start activity
java.lang.NullPointerException
java.lang.IllegalArgumentException
```

---

## 🐛 POSSÍVEIS CAUSAS E SOLUÇÕES

### Causa 1: Rota Não Encontrada
**Sintoma:** `IllegalArgumentException: navigation destination XXX is not found`

**Solução:** A rota está errada no MainActivity

**Como verificar:**
```kotlin
// MainActivity.kt deve ter exatamente:
route = "detalhes_pedido_concluido/{pedidoId}"
```

### Causa 2: Tipo de Argumento Incompatível
**Sintoma:** `IllegalArgumentException: Wrong argument type`

**Solução:** O tipo do argumento não bate

**Como verificar:**
```kotlin
// MainActivity.kt
navArgument("pedidoId") { type = NavType.IntType }  // ✅ CORRETO

// TelaPedidosHistorico.kt
navigate("detalhes_pedido_concluido/${pedido.id}")  // ✅ pedido.id é Int
```

### Causa 3: Objeto Null no Cache
**Sintoma:** App abre tela mas depois crasha

**Solução:** Cache não foi populado corretamente

**Debug:**
- Verificar se `PedidoCache.setPedido()` é chamado ANTES de navegar
- Verificar se o pedido não é null

### Causa 4: Erro na UI (LazyColumn)
**Sintoma:** Crash ao renderizar a tela

**Solução:** Algum campo do pedido está null e a UI não trata

**Debug:**
- Verificar se `pedido.contratante` pode ser null
- Verificar se `pedido.categoria` pode ser null

---

## 🔧 TESTE MANUAL RÁPIDO

Execute este teste:

1. **Abra o app**
2. **Vá para Histórico**
3. **Clique no primeiro pedido**
4. **ANOTE o que acontece:**
   - [ ] App fecha imediatamente
   - [ ] Tela branca e depois fecha
   - [ ] Loading infinito
   - [ ] Mensagem de erro aparece

---

## 🚨 SE O APP FECHAR IMEDIATAMENTE

O problema é na navegação. Verifique:

### MainActivity.kt - A rota está correta?
```kotlin
composable(
    route = "detalhes_pedido_concluido/{pedidoId}",
    arguments = listOf(
        navArgument("pedidoId") { type = NavType.IntType }
    )
) { backStackEntry ->
    TelaDetalhesPedidoConcluido(
        navController = navController,
        pedidoId = backStackEntry.arguments?.getInt("pedidoId") ?: 0
    )
}
```

### TelaPedidosHistorico.kt - A navegação está correta?
```kotlin
navController.navigate("detalhes_pedido_concluido/${pedido.id}") {
    launchSingleTop = true
}
```

---

## 💡 SOLUÇÃO ALTERNATIVA SIMPLES

Se nada funcionar, use esta solução 100% garantida:

### Adicione isto em MainActivity.kt:

```kotlin
// Adicione uma variável global no MainActivity
companion object {
    var pedidoSelecionado: PedidoHistorico? = null
}

// Na rota, passe 0 como ID:
composable("detalhes_pedido_concluido") { 
    MainActivity.pedidoSelecionado?.let { pedido ->
        TelaDetalhesPedidoConcluido(navController, pedido)
    }
}
```

### Em TelaPedidosHistorico.kt:

```kotlin
onClick = {
    MainActivity.pedidoSelecionado = pedido
    navController.navigate("detalhes_pedido_concluido")
}
```

---

## 📞 PRÓXIMO PASSO

**COPIE E COLE OS LOGS DO CRASH AQUI** para que eu possa ver o erro exato e corrigir!

Procure por linhas que começam com:
- `FATAL EXCEPTION`
- `AndroidRuntime`
- `Caused by:`
- `at com.exemple.facilita`

---

## 🎯 COMANDO PARA CAPTURAR LOGS

Execute ANTES de clicar no pedido:

```cmd
# Windows PowerShell
adb logcat -c; adb logcat > crash_log.txt

# Depois de crashar, pare com Ctrl+C
# Abra crash_log.txt e procure por "FATAL"
```

---

**Aguardando os logs para continuar o debug! 🔍**

