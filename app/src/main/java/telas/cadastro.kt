package telas

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.senac.geratravel_atividadefinal.ui.theme.GeraTravel_atividadefinalTheme
import componentes.CampoSenha
import viewmodel.CadastroViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Telacadastro(viewModel: CadastroViewModel = viewModel(),
                 onNavigateToLogin: () -> Unit){
    val uiState by viewModel.uiState.collectAsState()
    val cadastroSucesso by viewModel.cadastroSucesso.collectAsState()
    val confirmaSenha by viewModel.confirmaSenha.collectAsState()
    val context = LocalContext.current
    LaunchedEffect(cadastroSucesso) {
        if (cadastroSucesso) {
            Toast.makeText(context, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show()
            onNavigateToLogin()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Novo Usuário")
                }
            )
        }
    ) {paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = uiState.nome,
                onValueChange = { viewModel.updateNome(it) },
                label = { Text("Nome") },
                modifier = Modifier.fillMaxWidth()
                    .padding(vertical = 6.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )

            OutlinedTextField(
                value = uiState.email,
                onValueChange = { viewModel.updateEmail(it) },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
                    .padding(vertical = 6.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            OutlinedTextField(
                value = uiState.telefone,
                onValueChange = { viewModel.updateTelefone(it) },
                label = { Text("Telefone") },
                modifier = Modifier.fillMaxWidth()
                    .padding(vertical = 6.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            CampoSenha(
                senha = uiState.senha,
                onSenhaChange = { viewModel.updateSenha(it) },
                label = "Senha"
            )

            CampoSenha(
                senha = confirmaSenha,
                onSenhaChange = { viewModel.updateConfirmaSenha(it) },
                label = "Confirmar Senha"
            )

            Button(
                onClick = {viewModel.cadastrar()},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text("Registrar", modifier = Modifier.padding(8.dp))
            }
        }
    }
}

@Composable
@Preview(showSystemUi = true, showBackground = true)
fun PreviewCadastroScreen() {
    GeraTravel_atividadefinalTheme {
        Telacadastro( onNavigateToLogin = {} )
    }
}
