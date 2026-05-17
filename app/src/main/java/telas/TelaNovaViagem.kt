package telas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import viewmodel.ViagemViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaNovaViagem(
    userId: String,
    onNavigateBack: () -> Unit,
    viewModel: ViagemViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val sdf = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    var showDatePickerInicio by remember { mutableStateOf(false) }
    var showDatePickerFim by remember { mutableStateOf(false) }

    val datePickerStateInicio = rememberDatePickerState()
    val datePickerStateFim = rememberDatePickerState()

    // Sincroniza o estado do DatePicker com o ViewModel
    LaunchedEffect(datePickerStateInicio.selectedDateMillis) {
        viewModel.onDataInicioChange(datePickerStateInicio.selectedDateMillis)
    }
    LaunchedEffect(datePickerStateFim.selectedDateMillis) {
        viewModel.onDataFimChange(datePickerStateFim.selectedDateMillis)
    }

    // Se salvou com sucesso, volta para a tela anterior
    LaunchedEffect(uiState.sucesso) {
        if (uiState.sucesso) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nova Viagem 🌍") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = uiState.destino,
                onValueChange = { viewModel.onDestinoChange(it) },
                label = { Text("Destino 📍") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Tipo de Viagem:", style = MaterialTheme.typography.bodyLarge)
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = uiState.tipo == "Lazer",
                    onClick = { viewModel.onTipoChange("Lazer") }
                )
                Text("Lazer 🏖️")
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(
                    selected = uiState.tipo == "Negócios",
                    onClick = { viewModel.onTipoChange("Negócios") }
                )
                Text("Negócios 💼")
            }

            // Data Início
            OutlinedButton(
                onClick = { showDatePickerInicio = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                val dataTexto = uiState.dataInicio?.let { sdf.format(Date(it)) } ?: "Data de Início 📅"
                Text(dataTexto)
            }

            // Data Fim
            OutlinedButton(
                onClick = { showDatePickerFim = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                val dataTexto = uiState.dataFim?.let { sdf.format(Date(it)) } ?: "Data de Término 📅"
                Text(dataTexto)
            }

            OutlinedTextField(
                value = uiState.orcamento,
                onValueChange = { viewModel.onOrcamentoChange(it) },
                label = { Text("Orçamento 💰") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            if (uiState.mensagemErro != null) {
                Text(
                    text = uiState.mensagemErro!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Button(
                onClick = { viewModel.salvarViagem(userId) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Salvar Viagem")
            }
        }
    }

    // DatePickers
    if (showDatePickerInicio) {
        DatePickerDialog(
            onDismissRequest = { showDatePickerInicio = false },
            confirmButton = {
                TextButton(onClick = { showDatePickerInicio = false }) { Text("OK") }
            }
        ) {
            DatePicker(state = datePickerStateInicio)
        }
    }

    if (showDatePickerFim) {
        DatePickerDialog(
            onDismissRequest = { showDatePickerFim = false },
            confirmButton = {
                TextButton(onClick = { showDatePickerFim = false }) { Text("OK") }
            }
        ) {
            DatePicker(state = datePickerStateFim)
        }
    }
}
