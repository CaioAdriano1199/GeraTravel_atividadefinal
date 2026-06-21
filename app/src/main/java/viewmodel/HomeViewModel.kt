package viewmodel

import android.app.Application
import android.location.Geocoder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import model.Rotas
import model.Viagem
import java.util.Locale

data class HomeUiState(
    val telaAtiva: String = Rotas.MinhasViagens.rota,
    val cidadeAtual: String? = null,
    val viagemAtual: Viagem? = null,
    val carregandoLocalizacao: Boolean = false,
    val permissaoNegada: Boolean = false
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val viagemDao = AppDatabase.getDatabase(application).viagemDao()

    // Flow derivado para manter compatibilidade com observadores da tela atual
    val telaAtual: StateFlow<String> = _uiState
        .map { it.telaAtiva }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Rotas.MinhasViagens.rota
        )

    fun mudarTela(novaTela: String) {
        _uiState.update { it.copy(telaAtiva = novaTela) }
    }

    fun setPermissaoNegada(negada: Boolean) {
        _uiState.update { it.copy(permissaoNegada = negada) }
    }

    fun buscarViagemPorLocalizacao(latitude: Double, longitude: Double, userId: String) {
        _uiState.update { it.copy(carregandoLocalizacao = true) }
        
        viewModelScope.launch {
            val cidade = withContext(Dispatchers.IO) {
                obterCidade(latitude, longitude)
            }

            if (cidade != null) {
                val dataAtual = System.currentTimeMillis()
                val viagem = viagemDao.getViagemAtual(userId, cidade, dataAtual)
                _uiState.update { 
                    it.copy(
                        cidadeAtual = cidade,
                        viagemAtual = viagem,
                        carregandoLocalizacao = false
                    )
                }
            } else {
                _uiState.update { it.copy(carregandoLocalizacao = false) }
            }
        }
    }

    private fun obterCidade(lat: Double, lon: Double): String? {
        return try {
            val geocoder = Geocoder(getApplication(), Locale.getDefault())
            val enderecos = geocoder.getFromLocation(lat, lon, 1)
            enderecos?.firstOrNull()?.locality ?: enderecos?.firstOrNull()?.subAdminArea
        } catch (e: Exception) {
            null
        }
    }
}
