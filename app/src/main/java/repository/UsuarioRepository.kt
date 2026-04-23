package repository

import android.content.Context
import data.AppDatabase
import model.cadastro

class UsuarioRepository(context: Context) {

    private val dao = AppDatabase.getDatabase(context).usuarioDao()

    suspend fun cadastrar(email: String, senha: String, nome: String, telefone: String) {
        dao.inserir(
            Cadastro = cadastro(
                email = email,
                senha = senha,
                nome = nome,
                telefone = telefone
            )
        )
    }

    suspend fun login(email: String, senha: String): Boolean {
        return dao.login(email, senha) != null
    }
}
