package com.senac.geratravel_atividadefinal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.senac.geratravel_atividadefinal.ui.theme.GeraTravel_atividadefinalTheme
import model.Route
import telas.Telacadastro
import telas.TelaLembraSenha
import telas.Telalogin
import telas.TelaHome

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GeraTravel_atividadefinalTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyApp()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApp() {
    val backStack = remember { mutableStateListOf<Route>(Route.Login) }

    BackHandler(enabled = backStack.size > 1) {
        backStack.removeLastOrNull()
    }

    Scaffold(

    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            NavDisplay(
                backStack = backStack,
                onBack = { backStack.removeLastOrNull() },
                entryProvider = { route ->
                    when (route) {
                        is Route.Login -> NavEntry(route) {
                            Telalogin(
                                onNavigateToCadastro = { backStack.add(Route.Cadastro) },
                                onNavigateToEsqueciSenha = { backStack.add(Route.LembrarSenha) },
                                onNavigateToHome = { backStack.add(Route.Home(it)) }
                            )
                        }
                        is Route.Cadastro -> NavEntry(route) {
                            Telacadastro(
                                onNavigateToLogin = { backStack.removeLastOrNull() }
                            )
                        }
                        is Route.LembrarSenha -> NavEntry(route) {
                            TelaLembraSenha(
                                onNavigateTohome = { backStack.removeLastOrNull() }
                            )

                        }
                        is Route.Home -> NavEntry(route) {
                            TelaHome(email = route.email)
                        }
                    }
                }
            )
        }
    }
}


