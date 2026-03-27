package telas

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.senac.geratravel_atividadefinal.R
import componentes.CampoSenha
import viewmodel.LoginViewModel


@Composable
fun Telalogin(
    viewModel: LoginViewModel = viewModel(),
    onNavigateToCadastro: () -> Unit,
    onNavigateToEsqueciSenha: () -> Unit,
    onNavigateToHome: () -> Unit

){
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.fillMaxWidth()
            )
            Column() {
            OutlinedTextField(
                value = uiState.email,
                onValueChange = { viewModel.updateEmail(it) },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            
            CampoSenha(
                senha = uiState.senha,
                onSenhaChange = { viewModel.updateSenha(it) },
                label = "Senha"
            )
            }
            Button(
                onClick = {onNavigateToHome()},
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Text("Login")
            }
            
            Row(modifier = Modifier.padding(top = 16.dp)
                .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround) {
                    Text("Esqueceu a senha?", modifier = Modifier.clickable(
                        onClick = (onNavigateToEsqueciSenha)
                    ))


                    Text("Cadastre-se", modifier = Modifier.clickable(
                        onClick = (onNavigateToCadastro)
                    ))

            }
        }
    }
}
