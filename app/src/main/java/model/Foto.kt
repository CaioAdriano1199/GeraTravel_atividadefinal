package model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "fotos",
    foreignKeys = [
        ForeignKey(
            entity = Viagem::class,
            parentColumns = ["id"],
            childColumns = ["viagemId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Foto(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val viagemId: Int,
    val path: String // URI ou caminho do arquivo da foto
)
