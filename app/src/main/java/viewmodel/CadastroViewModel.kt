package viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import model.cadastro
import repository.UsuarioRepository

class CadastroViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = UsuarioRepository(application)

    private val _confirmaSenha = MutableStateFlow("")
    val confirmaSenha = _confirmaSenha.asStateFlow()


    private val _cadastroSucesso = MutableStateFlow(false)
    val cadastroSucesso = _cadastroSucesso.asStateFlow()

    private val _state = MutableStateFlow(cadastro())
    val uiState = _state.asStateFlow()

    fun updateEmail(email: String) {
        _state.value = _state.value.copy(email = email)
    }

    fun updateSenha(senha: String) {
        _state.value = _state.value.copy(senha = senha)
    }

    fun updateConfirmaSenha(confirmaSenha: String) {
        _confirmaSenha.value = confirmaSenha
    }

    fun updateNome(nome: String) {
        _state.value = _state.value.copy(nome = nome)
    }

    fun updateTelefone(telefone: String) {
        _state.value = _state.value.copy(telefone = telefone)
    }

    fun cadastrar() {
        val usuario = _state.value
        val confirmacao = _confirmaSenha.value


        if (usuario.email.isBlank() || usuario.senha.isBlank()) {
            return
        }

        if (usuario.senha != confirmacao) {
            return
        }

        viewModelScope.launch {
            repo.cadastrar(usuario.email, usuario.senha, usuario.nome, usuario.telefone)

        _cadastroSucesso.value = true
        }
    }
}