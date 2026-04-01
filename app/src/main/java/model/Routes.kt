package model

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route : NavKey {
    @Serializable data object Login : Route
    @Serializable data object Cadastro : Route
    @Serializable data object LembrarSenha : Route

    @Serializable data object Home : Route
}
