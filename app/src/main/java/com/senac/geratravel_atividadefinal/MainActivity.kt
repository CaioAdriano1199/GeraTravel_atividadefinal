package com.senac.geratravel_atividadefinal

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.senac.geratravel_atividadefinal.ui.theme.GeraTravel_atividadefinalTheme
import model.Route
import repository.GeminiRepository
import repository.UsuarioRepository
import telas.*
import viewmodel.LoginViewModel

// Importação das chaves do arquivo .env via BuildConfig
import com.senac.geratravel_atividadefinal.BuildConfig

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
    // Gerenciamento de estado de navegação imutável para maior estabilidade
    var backStack by remember { mutableStateOf(listOf<Route>(Route.Login)) }
    
    val context = LocalContext.current
    val usuarioRepository = remember { UsuarioRepository(context) }
    
    // A chave GEMINI_API_KEY deve estar no seu arquivo .env e começar com AIza
    val geminiRepository = remember { 
        GeminiRepository(apiKey = BuildConfig.GEMINI_API_KEY) 
    }

    // Gerencia o botão físico/gesto de voltar do celular
    BackHandler(enabled = backStack.size > 1) {
        backStack = backStack.dropLast(1)
    }

    Scaffold { innerPadding ->
        NavDisplay(
            modifier = Modifier.padding(innerPadding),
            backStack = backStack,
            onBack = { if (backStack.size > 1) backStack = backStack.dropLast(1) },
            entryProvider = { route ->
                when (route) {
                    is Route.Login -> NavEntry(route) {
                        val loginViewModel: LoginViewModel = viewModel(
                            factory = LoginViewModel.provideFactory(usuarioRepository)
                        )
                        Telalogin(
                            viewModel = loginViewModel,
                            onNavigateToCadastro = { 
                                backStack = backStack + Route.Cadastro 
                            },
                            onNavigateToEsqueciSenha = { 
                                backStack = backStack + Route.LembrarSenha 
                            },
                            onNavigateToHome = { email -> 
                                backStack = listOf(Route.Home(email)) 
                            }
                        )
                    }
                    is Route.Cadastro -> NavEntry(route) {
                        Telacadastro(onNavigateToLogin = { 
                            if (backStack.size > 1) backStack = backStack.dropLast(1) 
                        })
                    }
                    is Route.LembrarSenha -> NavEntry(route) {
                        TelaLembraSenha(onNavigateTohome = { 
                            if (backStack.size > 1) backStack = backStack.dropLast(1) 
                        })
                    }
                    is Route.Home -> NavEntry(route) {
                        TelaHome(
                            email = route.email,
                            onNavigateToNovaViagem = { 
                                backStack = backStack + Route.NovaViagem(route.email) 
                            },
                            onNavigateToMinhasViagens = { 
                                backStack = backStack + Route.MinhasViagens(route.email) 
                            },
                            onNavigateToFotos = { id -> 
                                backStack = backStack + Route.Fotos(id) 
                            },
                            onNavigateToRoteiro = { viagem -> 
                                backStack = backStack + Route.Roteiro(viagem) 
                            }
                        )
                    }
                    is Route.NovaViagem -> NavEntry(route) {
                        TelaNovaViagem(
                            userId = route.userId,
                            onNavigateBack = { 
                                if (backStack.size > 1) backStack = backStack.dropLast(1) 
                            }
                        )
                    }
                    is Route.MinhasViagens -> NavEntry(route) {
                        TelaMinhasViagens(
                            userId = route.userId,
                            onNavigateBack = { 
                                if (backStack.size > 1) backStack = backStack.dropLast(1) 
                            },
                            onNavigateToEditar = { viagem -> 
                                backStack = backStack + Route.EditarViagem(viagem) 
                            }
                        )
                    }
                    is Route.EditarViagem -> NavEntry(route) {
                        TelaEditarViagem(
                            viagem = route.viagem,
                            onNavigateBack = { 
                                if (backStack.size > 1) backStack = backStack.dropLast(1) 
                            }
                        )
                    }
                    is Route.Fotos -> NavEntry(route) {
                        TelaFotos(
                            viagemId = route.viagemId,
                            onNavigateBack = { 
                                if (backStack.size > 1) backStack = backStack.dropLast(1) 
                            }
                        )
                    }
                    is Route.Roteiro -> NavEntry(route) {
                        TelaRoteiro(
                            viagem = route.viagem,
                            onNavigateBack = { 
                                if (backStack.size > 1) backStack = backStack.dropLast(1) 
                            },
                            geminiRepository = geminiRepository
                        )
                    }
                }
            }
        )
    }
}
