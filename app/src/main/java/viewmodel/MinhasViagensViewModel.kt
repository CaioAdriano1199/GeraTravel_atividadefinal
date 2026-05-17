package viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import data.AppDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import model.Viagem

class MinhasViagensViewModel(application: Application) : AndroidViewModel(application) {
    private val viagemDao = AppDatabase.getDatabase(application).viagemDao()

    private val _viagens = MutableStateFlow<List<Viagem>>(emptyList())
    val viagens: StateFlow<List<Viagem>> = _viagens.asStateFlow()

    fun carregarViagens(userId: String) {
        viewModelScope.launch {
            viagemDao.getViagensByUser(userId).collectLatest { lista ->
                _viagens.value = lista
            }
        }
    }

    fun excluirViagem(viagem: Viagem) {
        viewModelScope.launch {
            viagemDao.delete(viagem)
        }
    }
}
