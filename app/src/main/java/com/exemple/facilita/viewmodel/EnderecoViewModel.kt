package com.exemple.facilita.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exemple.facilita.model.NominatimResult
import com.exemple.facilita.service.RetrofitFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EnderecoViewModel : ViewModel() {

    private val nominatimApi = RetrofitFactory().getNominatimApi()

    // Flow que a UI vai observar
    private val _sugestoes = MutableStateFlow<List<NominatimResult>>(emptyList())
    val sugestoes: StateFlow<List<NominatimResult>> = _sugestoes

    fun buscarSugestoes(query: String) {
        if (query.isBlank()) {
            _sugestoes.value = emptyList()
            return
        }

        viewModelScope.launch {
            try {
                val results = withContext(Dispatchers.IO) {
                    nominatimApi.searchAddress(query)
                }
                _sugestoes.value = results
            } catch (e: Exception) {
                e.printStackTrace()
                _sugestoes.value = emptyList()
            }
        }
    }
}
