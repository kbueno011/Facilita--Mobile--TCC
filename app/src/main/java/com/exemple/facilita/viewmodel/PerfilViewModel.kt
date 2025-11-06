package com.exemple.facilita.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exemple.facilita.service.ApiService
import com.exemple.facilita.service.RetrofitFactory
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// --- Classes de dados atualizadas para suportar ambos os tipos de conta ---

// 1. A resposta completa da API, agora com um campo para contratante
data class ProfileResponse(
    val message: String,
    val token: String,
    val prestador: Prestador?, // Pode ser nulo se o usuário for contratante
    val contratante: Contratante? // Pode ser nulo se o usuário for prestador
)

// 2. O objeto Prestador (já existia)
data class Prestador(
    val id: Int,
    @SerializedName("id_usuario") val idUsuario: Int,
    val usuario: Usuario
)

// 3. NOVO: O objeto Contratante
data class Contratante(
    val id: Int,
    @SerializedName("id_usuario") val idUsuario: Int,
    val usuario: Usuario
)

// 4. O objeto Usuario (sem alterações, usado por ambos)
data class Usuario(
    val id: Int,
    val nome: String,
    val email: String,
    val telefone: String?,
    @SerializedName("foto_p") val foto_perfil: String?,
    @SerializedName("criado_em") val criado_em: String
)

// --- ViewModel com lógica aprimorada ---

class PerfilViewModel : ViewModel() {

    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario

    private val apiService: ApiService = RetrofitFactory.apiService

    fun carregarPerfil(token: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getProfile(token)

                if (response.isSuccessful) {
                    val profileBody = response.body()
                    // Tenta obter o usuário de 'prestador', se for nulo, tenta de 'contratante'
                    _usuario.value = profileBody?.prestador?.usuario ?: profileBody?.contratante?.usuario
                } else {
                    val errorBody = response.errorBody()?.string()
                    println("Erro ao carregar perfil: ${response.code()} - $errorBody")
                }
            } catch (e: Exception) {
                println("Exceção ao carregar perfil: ${e.message}")
            }
        }
    }
}
