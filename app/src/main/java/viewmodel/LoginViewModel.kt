package viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import model.login

class LoginViewModel: ViewModel() {

    private val _state = MutableStateFlow(login())
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
}