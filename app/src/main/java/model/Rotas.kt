package model

sealed class Rotas(val rota: String) {
    object Home : Rotas("home")
    object NovaViagem : Rotas("nova_viagem")
    object MinhasViagens : Rotas("minhas_viagens")
    object Sobre : Rotas("sobre")
}