package data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import model.Foto

@Dao
interface FotoDao {
    @Insert
    suspend fun insert(foto: Foto)

    @Delete
    suspend fun delete(foto: Foto)

    @Query("SELECT * FROM fotos WHERE viagemId = :viagemId")
    fun getFotosByViagem(viagemId: Int): Flow<List<Foto>>
}
