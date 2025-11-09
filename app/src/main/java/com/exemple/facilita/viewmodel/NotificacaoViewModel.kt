package com.exemple.facilita.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exemple.facilita.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

/**
 * ViewModel para gerenciar o estado das notificações
 */
class NotificacaoViewModel : ViewModel() {

    // Estado das notificações
    private val _notificacoes = MutableStateFlow<List<Notificacao>>(emptyList())
    val notificacoes: StateFlow<List<Notificacao>> = _notificacoes.asStateFlow()

    // Contagem de notificações não lidas
    private val _notificacoesNaoLidas = MutableStateFlow(0)
    val notificacoesNaoLidas: StateFlow<Int> = _notificacoesNaoLidas.asStateFlow()

    // Estado de carregamento
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Notificação temporária para exibição in-app
    private val _notificacaoTemporaria = MutableStateFlow<Notificacao?>(null)
    val notificacaoTemporaria: StateFlow<Notificacao?> = _notificacaoTemporaria.asStateFlow()

    init {
        carregarNotificacoes()
        // Simular notificações de exemplo (remover em produção)
        gerarNotificacoesExemplo()
    }

    /**
     * Carrega as notificações do servidor
     */
    fun carregarNotificacoes() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // TODO: Implementar chamada à API
                // val response = notificacaoService.buscarNotificacoes()
                // _notificacoes.value = response.notificacoes
                // _notificacoesNaoLidas.value = response.totalNaoLidas
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Adiciona uma nova notificação
     */
    fun adicionarNotificacao(notificacao: Notificacao) {
        viewModelScope.launch {
            val listaAtual = _notificacoes.value.toMutableList()
            listaAtual.add(0, notificacao)
            _notificacoes.value = listaAtual

            if (notificacao.status == StatusNotificacao.NAO_LIDA) {
                _notificacoesNaoLidas.value += 1
            }

            // Exibir notificação temporária (toast-like)
            _notificacaoTemporaria.value = notificacao
        }
    }

    /**
     * Marca uma notificação como lida
     */
    fun marcarComoLida(notificacaoId: String) {
        viewModelScope.launch {
            val listaAtualizada = _notificacoes.value.map { notif ->
                if (notif.id == notificacaoId && notif.status == StatusNotificacao.NAO_LIDA) {
                    _notificacoesNaoLidas.value = (_notificacoesNaoLidas.value - 1).coerceAtLeast(0)
                    notif.copy(status = StatusNotificacao.LIDA)
                } else {
                    notif
                }
            }
            _notificacoes.value = listaAtualizada

            // TODO: Atualizar no servidor
            // notificacaoService.marcarComoLida(notificacaoId)
        }
    }

    /**
     * Marca todas as notificações como lidas
     */
    fun marcarTodasComoLidas() {
        viewModelScope.launch {
            val listaAtualizada = _notificacoes.value.map { notif ->
                if (notif.status == StatusNotificacao.NAO_LIDA) {
                    notif.copy(status = StatusNotificacao.LIDA)
                } else {
                    notif
                }
            }
            _notificacoes.value = listaAtualizada
            _notificacoesNaoLidas.value = 0

            // TODO: Atualizar no servidor
            // notificacaoService.marcarTodasComoLidas()
        }
    }

    /**
     * Remove uma notificação
     */
    fun removerNotificacao(notificacaoId: String) {
        viewModelScope.launch {
            val notifRemovida = _notificacoes.value.find { it.id == notificacaoId }
            if (notifRemovida?.status == StatusNotificacao.NAO_LIDA) {
                _notificacoesNaoLidas.value = (_notificacoesNaoLidas.value - 1).coerceAtLeast(0)
            }

            _notificacoes.value = _notificacoes.value.filter { it.id != notificacaoId }

            // TODO: Remover no servidor
            // notificacaoService.removerNotificacao(notificacaoId)
        }
    }

    /**
     * Arquiva uma notificação
     */
    fun arquivarNotificacao(notificacaoId: String) {
        viewModelScope.launch {
            val listaAtualizada = _notificacoes.value.map { notif ->
                if (notif.id == notificacaoId) {
                    if (notif.status == StatusNotificacao.NAO_LIDA) {
                        _notificacoesNaoLidas.value = (_notificacoesNaoLidas.value - 1).coerceAtLeast(0)
                    }
                    notif.copy(status = StatusNotificacao.ARQUIVADA)
                } else {
                    notif
                }
            }
            _notificacoes.value = listaAtualizada
        }
    }

    /**
     * Limpa a notificação temporária
     */
    fun limparNotificacaoTemporaria() {
        _notificacaoTemporaria.value = null
    }

    /**
     * Filtra notificações por tipo
     */
    fun filtrarPorTipo(tipo: TipoNotificacao): List<Notificacao> {
        return _notificacoes.value.filter { it.tipo == tipo }
    }

    /**
     * Busca notificações por texto
     */
    fun buscarNotificacoes(query: String): List<Notificacao> {
        if (query.isBlank()) return _notificacoes.value

        val queryLower = query.lowercase()
        return _notificacoes.value.filter {
            it.titulo.lowercase().contains(queryLower) ||
            it.mensagem.lowercase().contains(queryLower)
        }
    }

    /**
     * Gera notificações de exemplo para demonstração
     */
    private fun gerarNotificacoesExemplo() {
        val exemplos = listOf(
            Notificacao(
                id = "1",
                tipo = TipoNotificacao.PEDIDO_ACEITO,
                titulo = "Pedido Aceito! 🎉",
                mensagem = "Seu pedido #1234 foi aceito por João Silva. O prestador está a caminho!",
                dataHora = LocalDateTime.now().minusMinutes(5),
                prioridade = PrioridadeNotificacao.ALTA,
                acaoPrincipal = AcaoNotificacao("Ver Detalhes", "tela_pedido_detalhes/1234")
            ),
            Notificacao(
                id = "2",
                tipo = TipoNotificacao.PRESTADOR_A_CAMINHO,
                titulo = "Prestador a Caminho 🚗",
                mensagem = "João Silva está a 5 minutos de distância. Tempo estimado: 12:30",
                dataHora = LocalDateTime.now().minusMinutes(15),
                prioridade = PrioridadeNotificacao.ALTA,
                acaoPrincipal = AcaoNotificacao("Rastrear", "tela_rastreamento")
            ),
            Notificacao(
                id = "3",
                tipo = TipoNotificacao.PAGAMENTO_APROVADO,
                titulo = "Pagamento Aprovado ✅",
                mensagem = "Seu pagamento de R$ 45,00 foi processado com sucesso!",
                dataHora = LocalDateTime.now().minusHours(1),
                prioridade = PrioridadeNotificacao.MEDIA,
                status = StatusNotificacao.LIDA
            ),
            Notificacao(
                id = "4",
                tipo = TipoNotificacao.NOVO_CUPOM,
                titulo = "Novo Cupom Disponível! 🎁",
                mensagem = "Ganhe 20% OFF na sua próxima compra! Cupom: FACILITA20",
                dataHora = LocalDateTime.now().minusHours(3),
                prioridade = PrioridadeNotificacao.MEDIA,
                acaoPrincipal = AcaoNotificacao("Usar Agora", "tela_cupons")
            ),
            Notificacao(
                id = "5",
                tipo = TipoNotificacao.PEDIDO_CONCLUIDO,
                titulo = "Pedido Concluído! ⭐",
                mensagem = "Seu pedido #1233 foi concluído. Que tal avaliar o prestador?",
                dataHora = LocalDateTime.now().minusHours(5),
                prioridade = PrioridadeNotificacao.BAIXA,
                status = StatusNotificacao.LIDA,
                acaoPrincipal = AcaoNotificacao("Avaliar", "tela_avaliacao/1233")
            ),
            Notificacao(
                id = "6",
                tipo = TipoNotificacao.SALDO_RECEBIDO,
                titulo = "Saldo Creditado 💰",
                mensagem = "R$ 15,00 foram creditados na sua carteira pelo cashback!",
                dataHora = LocalDateTime.now().minusDays(1),
                prioridade = PrioridadeNotificacao.MEDIA,
                status = StatusNotificacao.LIDA
            ),
            Notificacao(
                id = "7",
                tipo = TipoNotificacao.PROMOCAO,
                titulo = "Promoção Relâmpago! ⚡",
                mensagem = "Frete grátis em pedidos de farmácia até às 18h! Aproveite!",
                dataHora = LocalDateTime.now().minusDays(2),
                prioridade = PrioridadeNotificacao.ALTA,
                status = StatusNotificacao.LIDA
            )
        )

        _notificacoes.value = exemplos
        _notificacoesNaoLidas.value = exemplos.count { it.status == StatusNotificacao.NAO_LIDA }
    }
}

