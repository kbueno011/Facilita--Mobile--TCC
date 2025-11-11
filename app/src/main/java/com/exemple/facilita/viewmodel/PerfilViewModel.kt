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

// 1. A resposta completa da API
data class ProfileResponse(
    val message: String?,
    val data: PerfilData
)

// 2. Dados do perfil (estrutura principal)
data class PerfilData(
    val id: Int,
    val nome: String,
    val email: String,
    val telefone: String?,
    @SerializedName("foto_perfil") val foto_perfil: String?,
    @SerializedName("tipo_conta") val tipo_conta: String,
    @SerializedName("criado_em") val criado_em: String,
    val carteira: String?,
    @SerializedName("dados_contratante") val dados_contratante: DadosContratante?,
    @SerializedName("dados_prestador") val dados_prestador: DadosPrestador?
)

// 3. Dados específicos do contratante
data class DadosContratante(
    val id: Int,
    val cpf: String?,
    val necessidade: String?,
    val localizacao: LocalizacaoInfo?
)

// 4. Dados específicos do prestador (se necessário no futuro)
data class DadosPrestador(
    val id: Int,
    val cpf: String?
)

// 5. Informações de localização
data class LocalizacaoInfo(
    val id: Int,
    val logradouro: String?,
    val numero: String?,
    val bairro: String?,
    val cidade: String?,
    val cep: String?,
    val latitude: String?,
    val longitude: String?
)


// --- ViewModel com lógica aprimorada ---

class PerfilViewModel : ViewModel() {

    private val _perfilData = MutableStateFlow<PerfilData?>(null)
    val perfilData: StateFlow<PerfilData?> = _perfilData

    private val apiService: ApiService = RetrofitFactory.apiService

    fun carregarPerfil(token: String) {
        viewModelScope.launch {
            try {
                println("🔍 Carregando perfil com token...")
                val response = apiService.getProfile(token)

                if (response.isSuccessful) {
                    val profileBody = response.body()
                    println("✅ Perfil recebido: ${profileBody?.data}")
                    _perfilData.value = profileBody?.data
                } else {
                    val errorBody = response.errorBody()?.string()
                    println("❌ Erro ao carregar perfil: ${response.code()} - $errorBody")
                }
            } catch (e: Exception) {
                println("❌ Exceção ao carregar perfil: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    // Atualizar nome
    fun atualizarNome(token: String, novoNome: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                println("🔄 Atualizando nome para: $novoNome")
                val dados = mapOf("nome" to novoNome)
                val response = apiService.updateProfile(token, dados)

                if (response.isSuccessful) {
                    println("✅ Nome atualizado com sucesso")
                    carregarPerfil(token) // Recarrega o perfil
                    onSuccess()
                } else {
                    val errorBody = response.errorBody()?.string()
                    println("❌ Erro ao atualizar nome: ${response.code()} - $errorBody")
                    onError("Erro ao atualizar nome: ${response.code()}")
                }
            } catch (e: Exception) {
                println("❌ Exceção ao atualizar nome: ${e.message}")
                onError("Erro: ${e.message}")
            }
        }
    }

    // Atualizar email
    fun atualizarEmail(token: String, novoEmail: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                println("🔄 Atualizando email para: $novoEmail")
                val dados = mapOf("email" to novoEmail)
                val response = apiService.updateProfile(token, dados)

                if (response.isSuccessful) {
                    println("✅ Email atualizado com sucesso")
                    carregarPerfil(token)
                    onSuccess()
                } else {
                    val errorBody = response.errorBody()?.string()
                    println("❌ Erro ao atualizar email: ${response.code()} - $errorBody")
                    onError("Erro ao atualizar email: ${response.code()}")
                }
            } catch (e: Exception) {
                println("❌ Exceção ao atualizar email: ${e.message}")
                onError("Erro: ${e.message}")
            }
        }
    }

    // Atualizar telefone
    fun atualizarTelefone(token: String, novoTelefone: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                println("🔄 Atualizando telefone para: $novoTelefone")
                val dados = mapOf("telefone" to novoTelefone)
                val response = apiService.updateProfile(token, dados)

                if (response.isSuccessful) {
                    println("✅ Telefone atualizado com sucesso")
                    carregarPerfil(token)
                    onSuccess()
                } else {
                    val errorBody = response.errorBody()?.string()
                    println("❌ Erro ao atualizar telefone: ${response.code()} - $errorBody")
                    onError("Erro ao atualizar telefone: ${response.code()}")
                }
            } catch (e: Exception) {
                println("❌ Exceção ao atualizar telefone: ${e.message}")
                onError("Erro: ${e.message}")
            }
        }
    }

    // Atualizar localização (cidade e bairro)
    fun atualizarLocalizacao(
        token: String,
        cidade: String,
        bairro: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                println("🔄 Atualizando localização: $cidade / $bairro")
                val dados = mapOf("cidade" to cidade, "bairro" to bairro)
                val response = apiService.updateLocalizacao(token, dados)

                if (response.isSuccessful) {
                    println("✅ Localização atualizada com sucesso")
                    carregarPerfil(token)
                    onSuccess()
                } else {
                    val errorBody = response.errorBody()?.string()
                    println("❌ Erro ao atualizar localização: ${response.code()} - $errorBody")
                    onError("Erro ao atualizar localização: ${response.code()}")
                }
            } catch (e: Exception) {
                println("❌ Exceção ao atualizar localização: ${e.message}")
                onError("Erro: ${e.message}")
            }
        }
    }
}
