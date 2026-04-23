package data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import model.Route
import model.cadastro

@Dao
interface UsuarioDao {

    @Insert
    suspend fun inserir(Cadastro: cadastro)

    @Query("SELECT * FROM usuario WHERE email = :email AND senha = :senha LIMIT 1")
    suspend fun login(email: String, senha: String): cadastro?
}