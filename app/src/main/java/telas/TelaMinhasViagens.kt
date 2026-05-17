package telas

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import model.Viagem
import viewmodel.MinhasViagensViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TelaMinhasViagens(
    userId: String,
    onNavigateBack: () -> Unit,
    onNavigateToEditar: (Viagem) -> Unit,
    viewModel: MinhasViagensViewModel = viewModel()
) {
    val viagens by viewModel.viagens.collectAsState()
    val sdf = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    LaunchedEffect(userId) {
        viewModel.carregarViagens(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Minhas Viagens 🧳") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        if (viagens.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Nenhuma viagem cadastrada.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(viagens) { viagem ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .combinedClickable(
                                onClick = { /* Talvez abrir detalhes? */ },
                                onLongClick = { onNavigateToEditar(viagem) }
                            ),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Imagem/Ícone para diferenciar Tipo
                            Icon(
                                imageVector = if (viagem.tipo == "Lazer") Icons.Default.BeachAccess else Icons.Default.BusinessCenter,
                                contentDescription = viagem.tipo,
                                modifier = Modifier.size(40.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(viagem.destino, style = MaterialTheme.typography.titleLarge)
                                Text("${sdf.format(Date(viagem.dataInicio))} - ${sdf.format(Date(viagem.dataFim))}")
                                Text("Orçamento: R$ ${String.format("%.2f", viagem.orcamento)}")
                            }

                            // Botão excluir no lado direito
                            IconButton(onClick = { viewModel.excluirViagem(viagem) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = Color.Red)
                            }
                        }
                    }
                }
            }
        }
    }
}
