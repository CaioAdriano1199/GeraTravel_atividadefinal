package model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "viagens")
data class Viagem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val destino: String,
    val tipo: String,
    val dataInicio: Long,
    val dataFim: Long,
    val orcamento: Double,
    val totalGastos: Double = 0.0,
    val userId: String
)
