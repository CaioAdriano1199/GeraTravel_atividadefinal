package viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import model.cadastro


class CadastroViewModel : ViewModel() {
    private val _state = MutableStateFlow(cadastro())
    val uiState = _state.asStateFlow()

    fun updateEmail(email: String) {
        _state.value = _state.value.copy(
            email = email
        )
    }

    fun updateSenha(senha: String) {
        _state.value = _state.value.copy(
            senha = senha
        )
    }
    fun updateConfirmaSenha(confirmaSenha: String) {
        _state.value = _state.value.copy(
            confirmaSenha = confirmaSenha
        )
    }
    fun updateNome(nome: String) {
        _state.value = _state.value.copy(
            nome = nome
        )
    }
    fun updateTelefone(telefone: String) {
        _state.value = _state.value.copy(
            telefone = telefone
        )
    }

}
