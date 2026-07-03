package data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import model.Viagem

@Dao
interface ViagemDao {
    @Insert
    suspend fun insert(viagem: Viagem)

    @Update
    suspend fun update(viagem: Viagem)

    @Delete
    suspend fun delete(viagem: Viagem)

    @Query("SELECT * FROM viagens WHERE userId = :userId ORDER BY dataInicio DESC")
    fun getViagensByUser(userId: String): Flow<List<Viagem>>

    @Query("SELECT * FROM viagens WHERE id = :id")
    suspend fun getViagemById(id: Int): Viagem?

    @Query("""
        SELECT * FROM viagens 
        WHERE userId = :userId 
        AND (LOWER(destino) LIKE '%' || LOWER(:cidade) || '%' OR LOWER(:cidade) LIKE '%' || LOWER(destino) || '%')
        AND :dataAtual >= dataInicio 
        AND :dataAtual <= (dataFim + 86399999)
        LIMIT 1
    """)
    suspend fun getViagemAtual(userId: String, cidade: String, dataAtual: Long): Viagem?
}
