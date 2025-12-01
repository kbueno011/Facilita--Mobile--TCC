package com.exemple.facilita.viewmodel

import androidx.lifecycle.ViewModel
import com.exemple.facilita.service.PedidoHistorico

/**
 * ViewModel compartilhado para passar dados do pedido entre telas
 * Solução mais robusta que navegação com argumentos
 */
class PedidoSharedViewModel : ViewModel() {
    private var _pedidoSelecionado: PedidoHistorico? = null

    fun setPedido(pedido: PedidoHistorico) {
        _pedidoSelecionado = pedido
        android.util.Log.d("PedidoSharedViewModel", "✅ Pedido #${pedido.id} armazenado no ViewModel")
    }

    fun getPedido(): PedidoHistorico? {
        android.util.Log.d("PedidoSharedViewModel", "📦 Recuperando pedido: ${_pedidoSelecionado?.id}")
        return _pedidoSelecionado
    }

    fun clearPedido() {
        android.util.Log.d("PedidoSharedViewModel", "🧹 Limpando pedido do ViewModel")
        _pedidoSelecionado = null
    }
}

