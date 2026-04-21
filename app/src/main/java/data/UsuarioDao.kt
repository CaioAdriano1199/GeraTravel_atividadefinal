package data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import model.Usuario

@Dao
interface UsuarioDao {

    @Insert
    suspend fun inserir(usuario: Usuario)

    @Query("SELECT * FROM usuario WHERE email = :email AND senha = :senha LIMIT 1")
    suspend fun login(email: String, senha: String): Usuario?
}