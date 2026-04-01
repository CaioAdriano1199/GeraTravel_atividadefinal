package telas

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun TelaHome(email: String){
    Column() {
        Text("Home")

        Text("E-mail = ${email}")
    }

}