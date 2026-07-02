package viewmodel

import android.app.Application
import android.location.Geocoder
import android.util.Log
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

    fun buscarViagemPorLocalizacao(latitude: Double, longitude: Double, userId: String) {
        Log.d("HomeViewModel", "Buscando viagem para Lat: $latitude, Lon: $longitude, User: $userId")
        _uiState.update { it.copy(carregandoLocalizacao = true) }
        
        viewModelScope.launch {
            val cidade = withContext(Dispatchers.IO) {
                obterCidade(latitude, longitude)
            }

            Log.d("HomeViewModel", "Cidade identificada: $cidade")

            if (cidade != null) {
                val dataAtual = System.currentTimeMillis()
                val viagem = viagemDao.getViagemAtual(userId, cidade, dataAtual)
                Log.d("HomeViewModel", "Viagem encontrada: ${viagem?.destino}")
                _uiState.update { 
                    it.copy(
                        cidadeAtual = cidade,
                        viagemAtual = viagem,
                        carregandoLocalizacao = false
                    )
                }
            } else {
                Log.e("HomeViewModel", "Não foi possível identificar a cidade via Geocoder")
                _uiState.update { it.copy(carregandoLocalizacao = false) }
            }
        }
    }

    private fun obterCidade(lat: Double, lon: Double): String? {
        return try {
            val geocoder = Geocoder(getApplication(), Locale.getDefault())
            val enderecos = geocoder.getFromLocation(lat, lon, 1)
            val endereco = enderecos?.firstOrNull()
            // Tenta vários campos para garantir que a cidade apareça
            val cidade = endereco?.locality 
                ?: endereco?.subAdminArea 
                ?: endereco?.adminArea 
                ?: endereco?.featureName
            cidade
        } catch (e: Exception) {
            Log.e("HomeViewModel", "Erro no Geocoder", e)
            null
        }
    }
}
