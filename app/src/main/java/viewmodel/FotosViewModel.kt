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
import model.Foto

class FotosViewModel(application: Application) : AndroidViewModel(application) {
    private val fotoDao = AppDatabase.getDatabase(application).fotoDao()

    private val _fotos = MutableStateFlow<List<Foto>>(emptyList())
    val fotos: StateFlow<List<Foto>> = _fotos.asStateFlow()

    fun carregarFotos(viagemId: Int) {
        viewModelScope.launch {
            fotoDao.getFotosByViagem(viagemId).collectLatest { lista ->
                _fotos.value = lista
            }
        }
    }

    fun adicionarFoto(viagemId: Int, path: String) {
        viewModelScope.launch {
            fotoDao.insert(Foto(viagemId = viagemId, path = path))
        }
    }
}
