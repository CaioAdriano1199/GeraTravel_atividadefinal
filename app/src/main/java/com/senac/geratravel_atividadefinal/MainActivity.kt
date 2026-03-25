package com.senac.geratravel_atividadefinal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.senac.geratravel_atividadefinal.ui.theme.GeraTravel_atividadefinalTheme
import telas.Telalogin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GeraTravel_atividadefinalTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Myapp()
                }
            }
        }
    }
}

@Composable
fun Myapp() {
    Telalogin()
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GeraTravel_atividadefinalTheme {
        Myapp()
    }
}