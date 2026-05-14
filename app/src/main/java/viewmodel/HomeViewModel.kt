package viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import model.Rotas

class HomeViewModel: ViewModel() {

    private val _telaAtual = MutableStateFlow(Rotas.MinhasViagens.rota)
    val telaAtual: StateFlow<String> = _telaAtual

    fun mudarTela(novaTela: String) {
        _telaAtual.value = novaTela
    }

}