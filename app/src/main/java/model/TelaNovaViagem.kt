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
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import kotlin.text.format

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaNovaViagem(userId: String) {
    var destino by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("Lazer") } // Lazer ou Negócios
    var orcamento by remember { mutableStateOf("") }

    // Estados para as datas
    val datePickerStateInicio = rememberDatePickerState()
    val datePickerStateFim = rememberDatePickerState()
    var showDatePickerInicio by remember { mutableStateOf(false) }
    var showDatePickerFim by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Cadastrar Nova Viagem 🌍", style = MaterialTheme.typography.headlineSmall)

        // Campo Destino
        OutlinedTextField(
            value = destino,
            onValueChange = { destino = it },
            label = { Text("Destino 📍") },
            modifier = Modifier.fillMaxWidth()
        )

        // Campo Tipo (Dropdown ou RadioButtons)
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = tipo == "Lazer", onClick = { tipo = "Lazer" })
            Text("Lazer 🏖️")
            Spacer(modifier = Modifier.width(16.dp))
            RadioButton(selected = tipo == "Negócios", onClick = { tipo = "Negócios" })
            Text("Negócios 💼")
        }

        // Data Início
        OutlinedButton(
            onClick = { showDatePickerInicio = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            val dataTexto = datePickerStateInicio.selectedDateMillis?.let {
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(com.google.type.Date(it))
            } ?: "Data de Início 📅"
            Text(dataTexto)
        }

        // Data Fim
        OutlinedButton(
            onClick = { showDatePickerFim = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            val dataTexto = datePickerStateFim.selectedDateMillis?.let {
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(com.google.type.Date(it))
            } ?: "Data de Término 📅"
            Text(dataTexto)
        }

        // Orçamento
        OutlinedTextField(
            value = orcamento,
            onValueChange = { orcamento = it },
            label = { Text("Orçamento 💰") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        // Botão Salvar com Validação
        Button(
            onClick = {
                if (destino.isBlank() || orcamento.isBlank() ||
                    datePickerStateInicio.selectedDateMillis == null ||
                    datePickerStateFim.selectedDateMillis == null) {
                    // Exibir erro (Toast ou Snackbar)
                } else {
                    // Logica para salvar no Room via ViewModel
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Salvar Viagem")
        }
    }

    // Componentes de DatePicker (Modais)
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

@Composable
fun SimpleDateFormat(x0: String, x1: getDefault) {
    TODO("Not yet implemented")
}