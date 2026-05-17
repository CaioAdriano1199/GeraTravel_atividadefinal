package viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import data.AppDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import model.Viagem

data class ViagemUiState(
    val destino: String = "",
    val tipo: String = "Lazer",
    val dataInicio: Long? = null,
    val dataFim: Long? = null,
    val orcamento: String = "",
    val mensagemErro: String? = null,
    val sucesso: Boolean = false
)

class ViagemViewModel(application: Application) : AndroidViewModel(application) {
    private val viagemDao = AppDatabase.getDatabase(application).viagemDao()

    private val _uiState = MutableStateFlow(ViagemUiState())
    val uiState = _uiState.asStateFlow()

    fun onDestinoChange(novoDestino: String) {
        _uiState.update { it.copy(destino = novoDestino) }
    }

    fun onTipoChange(novoTipo: String) {
        _uiState.update { it.copy(tipo = novoTipo) }
    }

    fun onDataInicioChange(novaData: Long?) {
        _uiState.update { it.copy(dataInicio = novaData) }
    }

    fun onDataFimChange(novaData: Long?) {
        _uiState.update { it.copy(dataFim = novaData) }
    }

    fun onOrcamentoChange(novoOrcamento: String) {
        _uiState.update { it.copy(orcamento = novoOrcamento) }
    }

    fun salvarViagem(userId: String) {
        if (!validarCampos()) return

        val state = _uiState.value
        viewModelScope.launch {
            try {
                val novaViagem = Viagem(
                    destino = state.destino,
                    tipo = state.tipo,
                    dataInicio = state.dataInicio!!,
                    dataFim = state.dataFim!!,
                    orcamento = state.orcamento.toDouble(),
                    userId = userId
                )
                viagemDao.insert(novaViagem)
                _uiState.update { it.copy(sucesso = true, mensagemErro = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(mensagemErro = "Erro ao salvar: ${e.message}") }
            }
        }
    }

    fun atualizarViagem(viagemId: Int, userId: String) {
        if (!validarCampos()) return

        val state = _uiState.value
        viewModelScope.launch {
            try {
                val viagemAtualizada = Viagem(
                    id = viagemId,
                    destino = state.destino,
                    tipo = state.tipo,
                    dataInicio = state.dataInicio!!,
                    dataFim = state.dataFim!!,
                    orcamento = state.orcamento.toDouble(),
                    userId = userId
                )
                viagemDao.update(viagemAtualizada)
                _uiState.update { it.copy(sucesso = true, mensagemErro = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(mensagemErro = "Erro ao atualizar: ${e.message}") }
            }
        }
    }

    private fun validarCampos(): Boolean {
        val state = _uiState.value
        if (state.destino.isBlank() || state.orcamento.isBlank() || 
            state.dataInicio == null || state.dataFim == null) {
            _uiState.update { it.copy(mensagemErro = "Todos os campos são obrigatórios!") }
            return false
        }

        if (state.orcamento.toDoubleOrNull() == null) {
            _uiState.update { it.copy(mensagemErro = "Orçamento inválido!") }
            return false
        }
        return true
    }

    fun resetMensagem() {
        _uiState.update { it.copy(mensagemErro = null, sucesso = false) }
    }
}
