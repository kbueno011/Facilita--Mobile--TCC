   ```kotlin
   // ✅ BOM
   tipo = TipoNotificacao.PEDIDO_ACEITO
   
   // ❌ RUIM
   tipo = TipoNotificacao.MENSAGEM_SISTEMA // genérico demais
   ```

2. **Prioridades corretas**
   ```kotlin
   // URGENTE: Requer ação imediata
   prioridade = PrioridadeNotificacao.URGENTE // Prestador chegou
   
   // ALTA: Requer atenção
   prioridade = PrioridadeNotificacao.ALTA // Pedido aceito
   
   // MEDIA: Informativo importante
   prioridade = PrioridadeNotificacao.MEDIA // Pagamento aprovado
   
   // BAIXA: Pode esperar
   prioridade = PrioridadeNotificacao.BAIXA // Lembrete de avaliar
   ```

3. **Mensagens claras e objetivas**
   ```kotlin
   // ✅ BOM
   mensagem = "Seu pedido #1234 foi aceito por João Silva"
   
   // ❌ RUIM
   mensagem = "Aconteceu uma coisa no seu pedido"
   ```

4. **Sempre adicione ações úteis**
   ```kotlin
   acaoPrincipal = AcaoNotificacao(
       texto = "Ver Pedido", // Claro e direto
       rota = "tela_pedido_detalhes/${pedido.id}"
   )
   ```

---

## 🚀 Teste Rápido

Para testar rapidamente, adicione este botão em qualquer tela:

```kotlin
@Composable
fun BotaoTesteNotificacao(navController: NavController) {
    val viewModel: NotificacaoViewModel = viewModel()
    
    Button(onClick = {
        viewModel.adicionarNotificacao(
            Notificacao(
                id = UUID.randomUUID().toString(),
                tipo = TipoNotificacao.PEDIDO_ACEITO,
                titulo = "Teste! 🎉",
                mensagem = "Esta é uma notificação de teste",
                prioridade = PrioridadeNotificacao.ALTA,
                acaoPrincipal = AcaoNotificacao(
                    texto = "Ver",
                    rota = "tela_notificacoes"
                )
            )
        )
    }) {
        Text("Enviar Notificação Teste")
    }
}
```

---

**Agora você tem exemplos completos para todos os cenários!** 🎉
# 🎯 Exemplos Práticos de Uso - Sistema de Notificações

## Cenários Reais de Implementação

### 1. 📦 Notificar Pedido Aceito

```kotlin
// Quando um prestador aceitar um pedido
fun onPedidoAceito(pedido: Pedido, prestador: Prestador) {
    val notificacaoViewModel: NotificacaoViewModel = viewModel()
    
    notificacaoViewModel.adicionarNotificacao(
        Notificacao(
            id = pedido.id,
            tipo = TipoNotificacao.PEDIDO_ACEITO,
            titulo = "Pedido Aceito! 🎉",
            mensagem = "Seu pedido #${pedido.numero} foi aceito por ${prestador.nome}. Estimativa: ${pedido.tempoEstimado}",
            prioridade = PrioridadeNotificacao.ALTA,
            acaoPrincipal = AcaoNotificacao(
                texto = "Ver Pedido",
                rota = "tela_pedido_detalhes/${pedido.id}"
            ),
            dadosExtras = mapOf(
                "pedidoId" to pedido.id,
                "prestadorId" to prestador.id
            )
        )
    )
}
```

---

### 2. 🚗 Rastreamento em Tempo Real

```kotlin
// Quando o prestador estiver próximo
fun onPrestadorProximo(distancia: Int, tempoEstimado: String) {
    val notificacaoViewModel: NotificacaoViewModel = viewModel()
    
    notificacaoViewModel.adicionarNotificacao(
        Notificacao(
            id = UUID.randomUUID().toString(),
            tipo = TipoNotificacao.PRESTADOR_A_CAMINHO,
            titulo = "Prestador Chegando! 🚗",
            mensagem = "Está a ${distancia}m de distância. Chegada em $tempoEstimado",
            prioridade = PrioridadeNotificacao.URGENTE,
            acaoPrincipal = AcaoNotificacao(
                texto = "Rastrear",
                rota = "tela_rastreamento"
            )
        )
    )
}

// Quando chegar
fun onPrestadorChegou() {
    val notificacaoViewModel: NotificacaoViewModel = viewModel()
    
    notificacaoViewModel.adicionarNotificacao(
        Notificacao(
            id = UUID.randomUUID().toString(),
            tipo = TipoNotificacao.PRESTADOR_CHEGOU,
            titulo = "Prestador Chegou! 📍",
            mensagem = "O prestador chegou no local de retirada",
            prioridade = PrioridadeNotificacao.URGENTE,
            acaoPrincipal = AcaoNotificacao(
                texto = "Ver Localização",
                rota = "tela_mapa"
            )
        )
    )
}
```

---

### 3. 💳 Notificações de Pagamento

```kotlin
// Pagamento aprovado
fun onPagamentoAprovado(valor: Double, metodoPagamento: String) {
    val notificacaoViewModel: NotificacaoViewModel = viewModel()
    
    notificacaoViewModel.adicionarNotificacao(
        Notificacao(
            id = UUID.randomUUID().toString(),
            tipo = TipoNotificacao.PAGAMENTO_APROVADO,
            titulo = "Pagamento Aprovado ✅",
            mensagem = "Seu pagamento de R$ ${String.format("%.2f", valor)} via $metodoPagamento foi processado com sucesso!",
            prioridade = PrioridadeNotificacao.MEDIA,
            acaoPrincipal = AcaoNotificacao(
                texto = "Ver Comprovante",
                rota = "tela_comprovante"
            )
        )
    )
}

// Pagamento recusado
fun onPagamentoRecusado(motivo: String) {
    val notificacaoViewModel: NotificacaoViewModel = viewModel()
    
    notificacaoViewModel.adicionarNotificacao(
        Notificacao(
            id = UUID.randomUUID().toString(),
            tipo = TipoNotificacao.PAGAMENTO_RECUSADO,
            titulo = "Pagamento Recusado ⚠️",
            mensagem = "Não foi possível processar seu pagamento. Motivo: $motivo",
            prioridade = PrioridadeNotificacao.ALTA,
            acaoPrincipal = AcaoNotificacao(
                texto = "Tentar Novamente",
                rota = "tela_pagamento"
            )
        )
    )
}
```

---

### 4. 🎁 Cupons e Promoções

```kotlin
// Novo cupom disponível
fun onNovoCupomDisponivel(cupom: Cupom) {
    val notificacaoViewModel: NotificacaoViewModel = viewModel()
    
    notificacaoViewModel.adicionarNotificacao(
        Notificacao(
            id = cupom.id,
            tipo = TipoNotificacao.NOVO_CUPOM,
            titulo = "Novo Cupom Disponível! 🎁",
            mensagem = "Ganhe ${cupom.desconto}% OFF na sua próxima compra! Cupom: ${cupom.codigo}",
            prioridade = PrioridadeNotificacao.MEDIA,
            acaoPrincipal = AcaoNotificacao(
                texto = "Usar Agora",
                rota = "tela_cupons/${cupom.id}"
            )
        )
    )
}

// Promoção relâmpago
fun onPromocaoRelampago(titulo: String, descricao: String, validadeAte: String) {
    val notificacaoViewModel: NotificacaoViewModel = viewModel()
    
    notificacaoViewModel.adicionarNotificacao(
        Notificacao(
            id = UUID.randomUUID().toString(),
            tipo = TipoNotificacao.PROMOCAO,
            titulo = "⚡ $titulo",
            mensagem = "$descricao Válido até $validadeAte!",
            prioridade = PrioridadeNotificacao.ALTA,
            acaoPrincipal = AcaoNotificacao(
                texto = "Aproveitar",
                rota = "tela_promocoes"
            )
        )
    )
}
```

---

### 5. ⭐ Sistema de Avaliação

```kotlin
// Pedido concluído - solicitar avaliação
fun onPedidoConcluido(pedido: Pedido) {
    val notificacaoViewModel: NotificacaoViewModel = viewModel()
    
    // Aguardar alguns segundos antes de solicitar avaliação
    viewModelScope.launch {
        delay(5000) // 5 segundos
        
        notificacaoViewModel.adicionarNotificacao(
            Notificacao(
                id = UUID.randomUUID().toString(),
                tipo = TipoNotificacao.PEDIDO_CONCLUIDO,
                titulo = "Pedido Concluído! ⭐",
                mensagem = "Seu pedido #${pedido.numero} foi concluído. Que tal avaliar o prestador?",
                prioridade = PrioridadeNotificacao.BAIXA,
                acaoPrincipal = AcaoNotificacao(
                    texto = "Avaliar Agora",
                    rota = "tela_avaliacao/${pedido.id}"
                ),
                acaoSecundaria = AcaoNotificacao(
                    texto = "Mais Tarde",
                    callback = { /* Fechar notificação */ }
                )
            )
        )
    }
}
```

---

### 6. 💰 Carteira e Cashback

```kotlin
// Saldo creditado
fun onSaldoCreditado(valor: Double, origem: String) {
    val notificacaoViewModel: NotificacaoViewModel = viewModel()
    
    notificacaoViewModel.adicionarNotificacao(
        Notificacao(
            id = UUID.randomUUID().toString(),
            tipo = TipoNotificacao.SALDO_RECEBIDO,
            titulo = "Saldo Creditado! 💰",
            mensagem = "R$ ${String.format("%.2f", valor)} foram creditados na sua carteira via $origem",
            prioridade = PrioridadeNotificacao.MEDIA,
            acaoPrincipal = AcaoNotificacao(
                texto = "Ver Carteira",
                rota = "tela_carteira"
            )
        )
    )
}

// Cashback recebido
fun onCashbackRecebido(valor: Double, pedidoId: String) {
    val notificacaoViewModel: NotificacaoViewModel = viewModel()
    
    notificacaoViewModel.adicionarNotificacao(
        Notificacao(
            id = UUID.randomUUID().toString(),
            tipo = TipoNotificacao.SALDO_RECEBIDO,
            titulo = "Cashback Recebido! 🎉",
            mensagem = "Você ganhou R$ ${String.format("%.2f", valor)} de cashback do pedido #$pedidoId",
            prioridade = PrioridadeNotificacao.MEDIA,
            acaoPrincipal = AcaoNotificacao(
                texto = "Ver Detalhes",
                rota = "tela_cashback"
            )
        )
    )
}
```

---

### 7. 🔔 Notificações do Sistema

```kotlin
// Atualização disponível
fun onAtualizacaoDisponivel(versao: String, recursos: String) {
    val notificacaoViewModel: NotificacaoViewModel = viewModel()
    
    notificacaoViewModel.adicionarNotificacao(
        Notificacao(
            id = "atualizacao_$versao",
            tipo = TipoNotificacao.ATUALIZACAO_APP,
            titulo = "Atualização Disponível 🔄",
            mensagem = "Nova versão $versao disponível! $recursos",
            prioridade = PrioridadeNotificacao.BAIXA,
            acaoPrincipal = AcaoNotificacao(
                texto = "Atualizar",
                callback = { /* Abrir Play Store */ }
            )
        )
    )
}

// Manutenção programada
fun onManutencaoProgramada(data: String, horario: String) {
    val notificacaoViewModel: NotificacaoViewModel = viewModel()
    
    notificacaoViewModel.adicionarNotificacao(
        Notificacao(
            id = UUID.randomUUID().toString(),
            tipo = TipoNotificacao.MENSAGEM_SISTEMA,
            titulo = "Manutenção Programada 🔧",
            mensagem = "Haverá manutenção em $data às $horario. O app ficará indisponível por alguns minutos.",
            prioridade = PrioridadeNotificacao.MEDIA
        )
    )
}
```

---

### 8. 📱 Integração com WebSocket (Tempo Real)

```kotlin
class WebSocketManager(private val notificacaoViewModel: NotificacaoViewModel) {
    
    private var webSocket: WebSocket? = null
    
    fun conectar(usuarioId: String) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("wss://api.facilita.com/ws/$usuarioId")
            .build()
        
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                val json = JSONObject(text)
                val tipo = json.getString("tipo")
                
                when (tipo) {
                    "pedido_aceito" -> {
                        notificacaoViewModel.adicionarNotificacao(
                            Notificacao(
                                id = json.getString("id"),
                                tipo = TipoNotificacao.PEDIDO_ACEITO,
                                titulo = json.getString("titulo"),
                                mensagem = json.getString("mensagem"),
                                prioridade = PrioridadeNotificacao.ALTA
                            )
                        )
                    }
                    "prestador_proximo" -> {
                        notificacaoViewModel.adicionarNotificacao(
                            Notificacao(
                                id = UUID.randomUUID().toString(),
                                tipo = TipoNotificacao.PRESTADOR_A_CAMINHO,
                                titulo = "Prestador Próximo! 🚗",
                                mensagem = json.getString("mensagem"),
                                prioridade = PrioridadeNotificacao.URGENTE
                            )
                        )
                    }
                    // ... outros tipos
                }
            }
        })
    }
    
    fun desconectar() {
        webSocket?.close(1000, "Fechado pelo usuário")
    }
}
```

---

### 9. 🔄 Sincronização com API

```kotlin
class NotificacaoRepository(
    private val api: NotificacaoService,
    private val viewModel: NotificacaoViewModel
) {
    
    suspend fun sincronizarNotificacoes() {
        try {
            // Buscar notificações não lidas
            val response = api.buscarNotificacoesNaoLidas()
            
            response.notificacoes.forEach { notifApi ->
                viewModel.adicionarNotificacao(
                    Notificacao(
                        id = notifApi.id,
                        tipo = TipoNotificacao.valueOf(notifApi.tipo),
                        titulo = notifApi.titulo,
                        mensagem = notifApi.mensagem,
                        dataHora = LocalDateTime.parse(notifApi.dataHora),
                        prioridade = PrioridadeNotificacao.valueOf(notifApi.prioridade)
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    suspend fun marcarComoLidaNoServidor(notificacaoId: String) {
        try {
            api.marcarComoLida(notificacaoId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
```

---

### 10. 🎨 Personalização Avançada

```kotlin
// Notificação com cor e ícone personalizados
fun notificacaoPersonalizada() {
    val notificacaoViewModel: NotificacaoViewModel = viewModel()
    
    notificacaoViewModel.adicionarNotificacao(
        Notificacao(
            id = UUID.randomUUID().toString(),
            tipo = TipoNotificacao.MENSAGEM_SISTEMA,
            titulo = "Bem-vindo ao Facilita! 👋",
            mensagem = "Explore nossos serviços e aproveite as promoções!",
            prioridade = PrioridadeNotificacao.BAIXA,
            icone = Icons.Default.Celebration, // Ícone customizado
            corFundo = 0xFF6A1B9A // Cor roxa customizada
        )
    )
}

// Notificação com múltiplas ações
fun notificacaoComMultiplasAcoes(pedidoId: String) {
    val notificacaoViewModel: NotificacaoViewModel = viewModel()
    
    notificacaoViewModel.adicionarNotificacao(
        Notificacao(
            id = pedidoId,
            tipo = TipoNotificacao.PEDIDO_EM_ANDAMENTO,
            titulo = "Confirme o Recebimento",
            mensagem = "Você recebeu seu pedido #$pedidoId?",
            prioridade = PrioridadeNotificacao.ALTA,
            acaoPrincipal = AcaoNotificacao(
                texto = "Sim, Recebi",
                callback = { 
                    // Lógica para confirmar recebimento
                    confirmarRecebimento(pedidoId)
                }
            ),
            acaoSecundaria = AcaoNotificacao(
                texto = "Reportar Problema",
                rota = "tela_suporte/$pedidoId"
            )
        )
    )
}
```

---

### 11. ⏰ Notificações Agendadas

```kotlin
class AgendadorNotificacoes(private val viewModel: NotificacaoViewModel) {
    
    // Lembrete para avaliar após 1 hora
    fun agendarLembreteAvaliacao(pedidoId: String) {
        viewModelScope.launch {
            delay(3600000) // 1 hora em milissegundos
            
            viewModel.adicionarNotificacao(
                Notificacao(
                    id = "lembrete_$pedidoId",
                    tipo = TipoNotificacao.PEDIDO_CONCLUIDO,
                    titulo = "Não esqueça de avaliar! ⭐",
                    mensagem = "Sua opinião é importante para melhorarmos!",
                    prioridade = PrioridadeNotificacao.BAIXA,
                    acaoPrincipal = AcaoNotificacao(
                        texto = "Avaliar",
                        rota = "tela_avaliacao/$pedidoId"
                    )
                )
            )
        }
    }
    
    // Lembrete diário de promoções
    fun configurarLembretesDiarios() {
        // Configurar WorkManager para notificações periódicas
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        
        val trabalho = PeriodicWorkRequestBuilder<NotificacaoWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()
        
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "notificacoes_diarias",
            ExistingPeriodicWorkPolicy.KEEP,
            trabalho
        )
    }
}
```

---

### 12. 📊 Analytics e Métricas

```kotlin
class NotificacaoAnalytics {
    
    fun registrarNotificacaoExibida(notificacao: Notificacao) {
        // Firebase Analytics
        firebaseAnalytics.logEvent("notificacao_exibida") {
            param("tipo", notificacao.tipo.name)
            param("prioridade", notificacao.prioridade.name)
            param("id", notificacao.id)
        }
    }
    
    fun registrarNotificacaoClicada(notificacao: Notificacao) {
        firebaseAnalytics.logEvent("notificacao_clicada") {
            param("tipo", notificacao.tipo.name)
            param("tempo_ate_click", calcularTempoAteClick(notificacao))
        }
    }
    
    fun registrarNotificacaoDismissada(notificacao: Notificacao) {
        firebaseAnalytics.logEvent("notificacao_dismissada") {
            param("tipo", notificacao.tipo.name)
            param("tempo_exibicao", calcularTempoExibicao(notificacao))
        }
    }
}
```

---

## 🎯 Dicas de Implementação

### ✅ Boas Práticas

1. **Use tipos apropriados**

