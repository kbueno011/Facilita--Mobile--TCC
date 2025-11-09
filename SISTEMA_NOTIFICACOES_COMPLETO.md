
---

## 🔒 Segurança

### Recomendações
1. **Validar** tipo de notificação no backend
2. **Sanitizar** conteúdo das mensagens
3. **Limitar** frequência de notificações por usuário
4. **Criptografar** dados sensíveis
5. **Verificar** permissões antes de exibir

---

## 📊 Métricas Sugeridas

### Analytics
- Taxa de abertura de notificações
- Tempo médio até visualização
- Notificações mais engajadoras
- Taxa de dismissal
- Conversão por tipo de notificação

---

## 🎯 Próximos Passos

### Melhorias Futuras
- [ ] Notificações em grupo (stacking)
- [ ] Sons personalizados por tipo
- [ ] Vibração personalizada
- [ ] Notificações ricas (imagens, botões)
- [ ] Notificações interativas
- [ ] Histórico de notificações arquivadas
- [ ] Configurações de preferências
- [ ] Sincronização entre dispositivos
- [ ] Notificações offline-first
- [ ] Deep linking melhorado

---

## 📞 Suporte

Para dúvidas ou sugestões sobre o sistema de notificações, consulte:
- Documentação do código
- Exemplos no `NotificacaoViewModel`
- Issues do projeto

---

**Versão:** 1.0.0  
**Última Atualização:** 2025-01-08  
**Desenvolvido para:** Facilita Mobile App
# 🔔 Sistema de Notificações Facilita - Documentação Completa

## 📋 Visão Geral

Sistema de notificações completo e moderno para o aplicativo Facilita, incluindo:
- ✅ Notificações in-app (estilo toast)
- ✅ Centro de notificações completo
- ✅ Badge com contador de notificações não lidas
- ✅ Filtros e busca
- ✅ Gestures de swipe (arquivar/deletar)
- ✅ Animações suaves e modernas
- ✅ Tipos de notificação personalizados
- ✅ Sistema de prioridades

---

## 🎯 Funcionalidades Implementadas

### 1. **Tipos de Notificação**
O sistema suporta 15 tipos diferentes de notificações:

- 📦 **PEDIDO_ACEITO** - Pedido aceito pelo prestador
- ❌ **PEDIDO_RECUSADO** - Pedido recusado
- 🚚 **PEDIDO_EM_ANDAMENTO** - Pedido em andamento
- ✅ **PEDIDO_CONCLUIDO** - Pedido finalizado
- 🚫 **PEDIDO_CANCELADO** - Pedido cancelado
- 📍 **PRESTADOR_CHEGOU** - Prestador chegou no local
- 🚗 **PRESTADOR_A_CAMINHO** - Prestador a caminho
- 💳 **PAGAMENTO_APROVADO** - Pagamento aprovado
- ⚠️ **PAGAMENTO_RECUSADO** - Pagamento recusado
- 💰 **SALDO_RECEBIDO** - Saldo creditado
- 🎁 **NOVO_CUPOM** - Novo cupom disponível
- ⭐ **PROMOCAO** - Promoção ativa
- ⭐ **AVALIACAO_RECEBIDA** - Avaliação recebida
- 📢 **MENSAGEM_SISTEMA** - Mensagem do sistema
- 🔄 **ATUALIZACAO_APP** - Atualização disponível

### 2. **Prioridades**
- 🔵 **BAIXA** - Notificações informativas
- 🟢 **MEDIA** - Notificações padrão
- 🟡 **ALTA** - Requerem atenção
- 🔴 **URGENTE** - Requerem ação imediata

### 3. **Status**
- 🔴 **NAO_LIDA** - Ainda não visualizada
- ⚪ **LIDA** - Já visualizada
- 📁 **ARQUIVADA** - Arquivada pelo usuário

---

## 🏗️ Arquitetura

### Estrutura de Arquivos

```
app/src/main/java/com/exemple/facilita/
│
├── model/
│   └── Notificacao.kt           # Modelos de dados
│
├── viewmodel/
│   └── NotificacaoViewModel.kt  # Lógica de negócio
│
├── components/
│   ├── NotificacaoInApp.kt      # Toast de notificação
│   └── IconeNotificacao.kt      # Ícone com badge
│
└── screens/
    └── TelaNotificacoes.kt      # Centro de notificações
```

### Componentes Principais

#### 1. **Model - Notificacao.kt**
```kotlin
data class Notificacao(
    val id: String,
    val tipo: TipoNotificacao,
    val titulo: String,
    val mensagem: String,
    val dataHora: LocalDateTime,
    val prioridade: PrioridadeNotificacao,
    val status: StatusNotificacao,
    val acaoPrincipal: AcaoNotificacao?,
    val acaoSecundaria: AcaoNotificacao?,
    val dadosExtras: Map<String, String>
)
```

#### 2. **ViewModel - NotificacaoViewModel.kt**
Gerencia o estado global das notificações:
- `notificacoes: StateFlow<List<Notificacao>>`
- `notificacoesNaoLidas: StateFlow<Int>`
- `isLoading: StateFlow<Boolean>`
- `notificacaoTemporaria: StateFlow<Notificacao?>`

Métodos principais:
- `adicionarNotificacao()`
- `marcarComoLida()`
- `marcarTodasComoLidas()`
- `removerNotificacao()`
- `arquivarNotificacao()`
- `buscarNotificacoes()`
- `filtrarPorTipo()`

---

## 💻 Como Usar

### 1. Adicionar Notificação Programaticamente

```kotlin
// No seu código, obtenha o ViewModel
val notificacaoViewModel: NotificacaoViewModel = viewModel()

// Criar uma nova notificação
val novaNotificacao = Notificacao(
    id = UUID.randomUUID().toString(),
    tipo = TipoNotificacao.PEDIDO_ACEITO,
    titulo = "Pedido Aceito! 🎉",
    mensagem = "Seu pedido #1234 foi aceito por João Silva",
    prioridade = PrioridadeNotificacao.ALTA,
    acaoPrincipal = AcaoNotificacao(
        texto = "Ver Detalhes",
        rota = "tela_pedido_detalhes/1234"
    )
)

// Adicionar a notificação
notificacaoViewModel.adicionarNotificacao(novaNotificacao)
```

### 2. Exibir Notificação In-App (Toast)

```kotlin
@Composable
fun MinhaTelaComNotificacao(navController: NavController) {
    val notificacaoViewModel: NotificacaoViewModel = viewModel()
    val notificacaoTemporaria by notificacaoViewModel.notificacaoTemporaria.collectAsState()
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Seu conteúdo aqui
        
        // Notificação in-app no topo
        NotificacaoInApp(
            notificacao = notificacaoTemporaria,
            onDismiss = { notificacaoViewModel.limparNotificacaoTemporaria() },
            onTap = {
                // Ação ao clicar na notificação
                notificacaoTemporaria?.acaoPrincipal?.rota?.let { rota ->
                    navController.navigate(rota)
                }
            }
        )
    }
}
```

### 3. Adicionar Ícone de Notificação com Badge

```kotlin
// No TopAppBar de qualquer tela
TopAppBar(
    title = { Text("Minha Tela") },
    actions = {
        IconeNotificacao(navController = navController)
    }
)
```

### 4. Navegar para Centro de Notificações

```kotlin
// De qualquer lugar do app
navController.navigate("tela_notificacoes")
```

---

## 🎨 Personalização

### Cores Personalizadas por Tipo

Cada tipo de notificação tem uma cor padrão, mas você pode personalizá-las:

```kotlin
val notificacao = Notificacao(
    // ... outros campos
    corFundo = 0xFFFF6B6B // Cor personalizada
)
```

### Ícones Personalizados

```kotlin
val notificacao = Notificacao(
    // ... outros campos
    icone = Icons.Default.MeuIconeCustomizado
)
```

### Ações Customizadas

```kotlin
val notificacao = Notificacao(
    // ... outros campos
    acaoPrincipal = AcaoNotificacao(
        texto = "Aceitar",
        callback = { 
            // Lógica customizada
            println("Botão clicado!")
        }
    ),
    acaoSecundaria = AcaoNotificacao(
        texto = "Recusar",
        rota = "tela_recusar"
    )
)
```

---

## 🔄 Integração com API

### Endpoint Sugerido

```kotlin
// No seu RetrofitService
@GET("notificacoes")
suspend fun buscarNotificacoes(
    @Query("usuario_id") usuarioId: String,
    @Query("limite") limite: Int = 50
): NotificacaoResponse

@POST("notificacoes/{id}/marcar-lida")
suspend fun marcarComoLida(@Path("id") notificacaoId: String): Response<Unit>

@POST("notificacoes/marcar-todas-lidas")
suspend fun marcarTodasComoLidas(): Response<Unit>

@DELETE("notificacoes/{id}")
suspend fun removerNotificacao(@Path("id") notificacaoId: String): Response<Unit>
```

### Implementar no ViewModel

```kotlin
fun carregarNotificacoes() {
    viewModelScope.launch {
        _isLoading.value = true
        try {
            val response = notificacaoService.buscarNotificacoes(usuarioId)
            _notificacoes.value = response.notificacoes
            _notificacoesNaoLidas.value = response.totalNaoLidas
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            _isLoading.value = false
        }
    }
}
```

---

## 🔔 Push Notifications (Firebase Cloud Messaging)

### 1. Adicionar Dependências no build.gradle

```gradle
dependencies {
    implementation("com.google.firebase:firebase-messaging:23.3.1")
    implementation("com.google.firebase:firebase-analytics:21.5.0")
}
```

### 2. Criar Service de FCM

```kotlin
class FacilitaFirebaseMessagingService : FirebaseMessagingService() {
    
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        // Converter mensagem FCM para Notificacao
        val notificacao = Notificacao(
            id = remoteMessage.messageId ?: UUID.randomUUID().toString(),
            tipo = TipoNotificacao.valueOf(remoteMessage.data["tipo"] ?: "MENSAGEM_SISTEMA"),
            titulo = remoteMessage.notification?.title ?: "",
            mensagem = remoteMessage.notification?.body ?: "",
            prioridade = PrioridadeNotificacao.ALTA
        )
        
        // Adicionar ao ViewModel
        // notificacaoViewModel.adicionarNotificacao(notificacao)
        
        // Exibir notificação do sistema
        exibirNotificacaoSistema(notificacao)
    }
    
    private fun exibirNotificacaoSistema(notificacao: Notificacao) {
        val channelId = "facilita_notificacoes"
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(notificacao.titulo)
            .setContentText(notificacao.mensagem)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
        
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // Criar canal (Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Notificações Facilita",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }
        
        notificationManager.notify(notificacao.id.hashCode(), notificationBuilder.build())
    }
}
```

### 3. Adicionar no AndroidManifest.xml

```xml
<service
    android:name=".service.FacilitaFirebaseMessagingService"
    android:exported="false">
    <intent-filter>
        <action android:name="com.google.firebase.MESSAGING_EVENT" />
    </intent-filter>
</service>
```

---

## 🎭 Exemplos de Uso por Cenário

### Cenário 1: Pedido Aceito
```kotlin
notificacaoViewModel.adicionarNotificacao(
    Notificacao(
        id = pedido.id,
        tipo = TipoNotificacao.PEDIDO_ACEITO,
        titulo = "Pedido Aceito! 🎉",
        mensagem = "Seu pedido #${pedido.numero} foi aceito por ${prestador.nome}",
        prioridade = PrioridadeNotificacao.ALTA,
        acaoPrincipal = AcaoNotificacao(
            texto = "Rastrear Pedido",
            rota = "tela_rastreamento/${pedido.id}"
        )
    )
)
```

### Cenário 2: Prestador Chegou
```kotlin
notificacaoViewModel.adicionarNotificacao(
    Notificacao(
        id = UUID.randomUUID().toString(),
        tipo = TipoNotificacao.PRESTADOR_CHEGOU,
        titulo = "Prestador Chegou! 📍",
        mensagem = "${prestador.nome} chegou no local de retirada",
        prioridade = PrioridadeNotificacao.URGENTE,
        acaoPrincipal = AcaoNotificacao(
            texto = "Ver Localização",
            rota = "tela_mapa/${pedido.id}"
        )
    )
)
```

### Cenário 3: Promoção
```kotlin
notificacaoViewModel.adicionarNotificacao(
    Notificacao(
        id = UUID.randomUUID().toString(),
        tipo = TipoNotificacao.PROMOCAO,
        titulo = "🎁 Promoção Especial!",
        mensagem = "20% OFF em todas as entregas de farmácia hoje!",
        prioridade = PrioridadeNotificacao.MEDIA,
        acaoPrincipal = AcaoNotificacao(
            texto = "Ver Ofertas",
            rota = "tela_promocoes"
        )
    )
)
```

---

## ✨ Recursos Avançados

### 1. Agrupamento de Notificações
```kotlin
fun agruparNotificacoesPorTipo(): Map<TipoNotificacao, List<Notificacao>> {
    return notificacoes.value.groupBy { it.tipo }
}
```

### 2. Notificações Silenciosas
```kotlin
// Adicionar sem exibir toast
val notificacao = Notificacao(/* ... */)
_notificacoes.value = _notificacoes.value + notificacao
// Não definir _notificacaoTemporaria
```

### 3. Notificações Agendadas
```kotlin
fun agendarNotificacao(notificacao: Notificacao, delay: Long) {
    viewModelScope.launch {
        delay(delay)
        adicionarNotificacao(notificacao)
    }
}
```

### 4. Limpar Notificações Antigas
```kotlin
fun limparNotificacoesAntigas(dias: Int = 7) {
    val dataLimite = LocalDateTime.now().minusDays(dias.toLong())
    _notificacoes.value = _notificacoes.value.filter {
        it.dataHora.isAfter(dataLimite)
    }
}
```

---

## 🧪 Testando o Sistema

### Notificações de Teste
O sistema já vem com 7 notificações de exemplo para testar todas as funcionalidades. Elas são geradas automaticamente no `init` do ViewModel.

Para desabilitar em produção:
```kotlin
// Comentar esta linha no NotificacaoViewModel
// gerarNotificacoesExemplo()
```

### Testar Diferentes Cenários
```kotlin
// Pedido aceito
viewModel.adicionarNotificacao(/* ... PEDIDO_ACEITO ... */)

// Aguardar 2 segundos
delay(2000)

// Prestador a caminho
viewModel.adicionarNotificacao(/* ... PRESTADOR_A_CAMINHO ... */)
```

---

## 📱 UI/UX Features

### ✅ Implementadas
- [x] Notificação toast animada no topo
- [x] Badge com contador de não lidas
- [x] Swipe to dismiss (deletar/arquivar)
- [x] Filtros por tipo
- [x] Busca por texto
- [x] Animações suaves (slide, fade)
- [x] Indicador de não lida (ponto vermelho)
- [x] Ícones coloridos por tipo
- [x] Estado vazio customizado
- [x] Pull to refresh (opcional)
- [x] Ações diretas nos cards

### 🎨 Design System
- **Cores:** Material Design 3
- **Tipografia:** Roboto (padrão Android)
- **Espaçamento:** 4dp grid
- **Cantos:** Rounded 12-16dp
- **Sombras:** Elevation 2-8dp
- **Animações:** 300ms spring/tween

---

## 🚀 Performance

### Otimizações Implementadas
- ✅ StateFlow para reatividade eficiente
- ✅ LazyColumn para lista virtualizada
- ✅ remember para evitar recomposições
- ✅ collectAsState para observação otimizada
- ✅ Keys únicas nos items para animações suaves

### Boas Práticas
- Limitar notificações a 100 itens
- Limpar notificações arquivadas após 30 dias
- Carregar em páginas (pagination)
- Cache local com Room Database (recomendado)

