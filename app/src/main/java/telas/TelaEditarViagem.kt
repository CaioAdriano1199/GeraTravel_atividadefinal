package telas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import model.Viagem
import viewmodel.ViagemViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaEditarViagem(
    viagem: Viagem,
    onNavigateBack: () -> Unit,
    viewModel: ViagemViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val sdf = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    var showDatePickerInicio by remember { mutableStateOf(false) }
    var showDatePickerFim by remember { mutableStateOf(false) }

    // Inicializa o ViewModel com os dados da viagem atual
    LaunchedEffect(viagem) {
        viewModel.onDestinoChange(viagem.destino)
        viewModel.onTipoChange(viagem.tipo)
        viewModel.onDataInicioChange(viagem.dataInicio)
        viewModel.onDataFimChange(viagem.dataFim)
        viewModel.onOrcamentoChange(viagem.orcamento.toString())
    }

    LaunchedEffect(uiState.sucesso) {
        if (uiState.sucesso) onNavigateBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Viagem 🔄") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
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

            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = uiState.tipo == "Lazer", onClick = { viewModel.onTipoChange("Lazer") })
                Text("Lazer 🏖️")
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(selected = uiState.tipo == "Negócios", onClick = { viewModel.onTipoChange("Negócios") })
                Text("Negócios 💼")
            }

            OutlinedButton(onClick = { showDatePickerInicio = true }, modifier = Modifier.fillMaxWidth()) {
                val dataTexto = uiState.dataInicio?.let { sdf.format(Date(it)) } ?: "Data de Início"
                Text(dataTexto)
            }

            OutlinedButton(onClick = { showDatePickerFim = true }, modifier = Modifier.fillMaxWidth()) {
                val dataTexto = uiState.dataFim?.let { sdf.format(Date(it)) } ?: "Data de Término"
                Text(dataTexto)
            }

            OutlinedTextField(
                value = uiState.orcamento,
                onValueChange = { viewModel.onOrcamentoChange(it) },
                label = { Text("Orçamento 💰") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Button(
                onClick = { viewModel.atualizarViagem(viagem.id, viagem.userId) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Salvar Alterações")
            }
        }
    }

    // DatePickers (omitidos aqui por brevidade, mas devem ser iguais aos da TelaNovaViagem)
    if (showDatePickerInicio) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = uiState.dataInicio)
        DatePickerDialog(
            onDismissRequest = { showDatePickerInicio = false },
            confirmButton = {
                TextButton(onClick = { 
                    viewModel.onDataInicioChange(datePickerState.selectedDateMillis)
                    showDatePickerInicio = false 
                }) { Text("OK") }
            }
        ) { DatePicker(state = datePickerState) }
    }
    
    if (showDatePickerFim) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = uiState.dataFim)
        DatePickerDialog(
            onDismissRequest = { showDatePickerFim = false },
            confirmButton = {
                TextButton(onClick = { 
                    viewModel.onDataFimChange(datePickerState.selectedDateMillis)
                    showDatePickerFim = false 
                }) { Text("OK") }
            }
        ) { DatePicker(state = datePickerState) }
    }
}
