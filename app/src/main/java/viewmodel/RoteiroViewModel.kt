package viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import model.Viagem
import repository.GeminiRepository
import java.util.concurrent.TimeUnit

data class RoteiroUiState(
    val destino: String = "",
    val periodo: String = "",
    val interesses: String = "",
    val roteiroGerado: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class RoteiroViewModel(
    private val repository: GeminiRepository,
    private val viagem: Viagem
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        RoteiroUiState(
            destino = viagem.destino,
            periodo = "${TimeUnit.MILLISECONDS.toDays(viagem.dataFim - viagem.dataInicio).toInt() + 1} dias",
            interesses = "Turismo, Gastronomia, Lazer"
        )
    )
    val uiState = _uiState.asStateFlow()

    fun onDestinoChange(value: String) {
        _uiState.update { it.copy(destino = value) }
    }

    fun onPeriodoChange(value: String) {
        _uiState.update { it.copy(periodo = value) }
    }

    fun onInteressesChange(value: String) {
        _uiState.update { it.copy(interesses = value) }
    }

    fun gerarRoteiro() {
        val state = _uiState.value
        if (state.destino.isBlank()) {
            _uiState.update { it.copy(error = "Por favor, informe o destino.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null, roteiroGerado = null) }

        val prompt = """
            Gere um roteiro de viagem detalhado em português para:
            Destino: ${state.destino}
            Período: ${state.periodo}
            Interesses: ${state.interesses}
            Orçamento de referência: R$ ${String.format("%.2f", viagem.orcamento)}
            
            Para cada dia, inclua:
            1. Pontos turísticos recomendados.
            2. Sugestão de Hotel.
            3. Sugestão de Restaurantes (almoço e jantar).
            
            Formate o texto de forma organizada com títulos para cada dia e use emojis.
        """.trimIndent()

        viewModelScope.launch {
            val resultado = repository.generateContent(prompt)

            if (resultado != null) {
                _uiState.update { it.copy(roteiroGerado = resultado, isLoading = false) }
            } else {
                _uiState.update { it.copy(error = "Falha ao gerar roteiro. Verifique sua chave de API ou conexão.", isLoading = false) }
            }
        }
    }

    companion object {
        fun provideFactory(repository: GeminiRepository, viagem: Viagem): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return RoteiroViewModel(repository, viagem) as T
            }
        }
    }
}
