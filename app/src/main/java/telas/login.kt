package telas

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.senac.geratravel_atividadefinal.R

@Composable
fun Telalogin(){
    Scaffold(

    ) { innerPadding ->
        Column() {
            Image(
                modifier = Modifier.padding(innerPadding),
                
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo"
            )
            Column() {
                Text("Email")
                OutlinedTextField(value = "", onValueChange = {})
            }
            Column() {
                Text("Senha")
                OutlinedTextField(value = "", onValueChange = {})
            }
            Button(onClick = {}) {
                Text("Login")
            }
            Row() {
                Text("Esqueceu a senha?")
                Text("Cadastre-se")
            }
        }
    }
        }


