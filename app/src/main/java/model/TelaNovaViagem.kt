package model

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaNovaViagem(userId: String){
    var destino by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("") }
    var orcamento by remember { mutableStateOf("") }

    val datePickerStateInicio = rememberDatePickerState()
    val dataPickerStateFim = rememberDatePickerState()
    var showDatePickerInicio by remember { mutableStateOf(false) }
    var showDatePickerFim by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticaScroll(rememberScrollState())
    ) { }


}