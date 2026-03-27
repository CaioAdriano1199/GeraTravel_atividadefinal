package componentes

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun CampoSenha(
    senha: String,
    onSenhaChange: (String) -> Unit,
    label: String = "Senha"
) {
    var senhaVisivel by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = senha,
        label = { Text(label) },
        onValueChange = onSenhaChange,
        modifier = Modifier.fillMaxWidth()
            .padding(vertical = 6.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = if (senhaVisivel)
            VisualTransformation.None
        else
            PasswordVisualTransformation(),
        trailingIcon = {
            val icon = if (senhaVisivel)
                Icons.Default.Visibility
            else
                Icons.Default.VisibilityOff

            IconButton(onClick = {
                senhaVisivel = !senhaVisivel
            }) {
                Icon(
                    imageVector = icon,
                    contentDescription = "Mostrar/Esconder senha"
                )
            }
        }
    )
}
