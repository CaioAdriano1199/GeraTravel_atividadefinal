package viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import repository.UsuarioRepository
import model.login

class LoginViewModel(private val repository: UsuarioRepository): ViewModel() {

    private val _state = MutableStateFlow(login())
    val uiState = _state.asStateFlow()

    fun updateEmail(email: String) {
        _state.value = _state.value.copy(
            email = email
        )
    }

    fun realizarLogin(onLoginSuccess: (String) -> Unit, onErroLogin: () -> Unit) {
        viewModelScope.launch {
            val logado = repository.login(
                email = _state.value.email,
                senha = _state.value.senha
            )
            if(logado) {
                onLoginSuccess(_state.value.email)
            } else {
                onErroLogin()
            }
        }
    }

    fun updateSenha(senha: String) {
        _state.value = _state.value.copy(
            senha = senha
        )
    }

    companion object {
        fun provideFactory(repository: UsuarioRepository): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return LoginViewModel(repository) as T
            }
        }
    }
}