package telas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import model.Viagem
import repository.GeminiRepository
import viewmodel.RoteiroViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaRoteiro(
    viagem: Viagem,
    onNavigateBack: () -> Unit,
    geminiRepository: GeminiRepository
) {
    val viewModel: RoteiroViewModel = viewModel(
        factory = RoteiroViewModel.provideFactory(geminiRepository, viagem)
    )
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Roteiro Inteligente 🤖") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Planeje sua viagem para ${viagem.destino}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(8.dp))
                    Text("Preenchemos os dados com base na sua viagem, mas você pode ajustá-los abaixo.", style = MaterialTheme.typography.bodySmall)
                }
            }

            OutlinedTextField(
                value = uiState.destino,
                onValueChange = { viewModel.onDestinoChange(it) },
                label = { Text("Para onde você vai?") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.periodo,
                onValueChange = { viewModel.onPeriodoChange(it) },
                label = { Text("Duração (ex: 5 dias)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.interesses,
                onValueChange = { viewModel.onInteressesChange(it) },
                label = { Text("Seus Interesses (separados por vírgula)") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Ex: Museus, parques, praias, gastronomia") },
                minLines = 2
            )

            if (uiState.error != null) {
                Text(uiState.error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Button(
                onClick = { viewModel.gerarRoteiro() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading,
                shape = MaterialTheme.shapes.medium
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Icon(Icons.Default.AutoAwesome, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Gerar Roteiro com Gemini Flash")
                }
            }

            if (uiState.roteiroGerado != null) {
                Text("Seu Roteiro Personalizado:", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    SelectionContainer {
                        Text(
                            text = uiState.roteiroGerado!!,
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
