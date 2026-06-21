package telas

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import model.Viagem
import viewmodel.HomeViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaHome(
    email: String,
    onNavigateToNovaViagem: () -> Unit,
    onNavigateToMinhasViagens: () -> Unit,
    homeViewModel: HomeViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val uiState by homeViewModel.uiState.collectAsState()
    val sdf = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // Função para disparar a busca de localização
    val dispararBuscaLocalizacao = {
        obterLocalizacao(fusedLocationClient, email, homeViewModel)
    }

    // Launcher para pedir permissão se não tiver
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
                      permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)
        if (granted) {
            dispararBuscaLocalizacao()
        }
    }

    // Tenta obter localização ao iniciar a tela
    LaunchedEffect(Unit) {
        val hasFineLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarseLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        
        if (hasFineLocation || hasCoarseLocation) {
            dispararBuscaLocalizacao()
        } else {
            locationPermissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    BackHandler(enabled = true) {
        if (drawerState.isOpen) {
            scope.launch { drawerState.close() }
        } else {
            (context as? ComponentActivity)?.finish()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Gera Travel Menu", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge)
                HorizontalDivider()
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.AirplaneTicket, null) },
                    label = { Text("Nova viagem") },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() }; onNavigateToNovaViagem() },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.CardTravel, null) },
                    label = { Text("Minhas Viagens") },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() }; onNavigateToMinhasViagens() },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Gera Travel 🌍") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = null)
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Olá, $email!", style = MaterialTheme.typography.titleMedium)
                
                Spacer(modifier = Modifier.height(16.dp))

                if (uiState.carregandoLocalizacao) {
                    CircularProgressIndicator()
                    Text("Identificando sua cidade...", modifier = Modifier.padding(8.dp))
                } else {
                    uiState.cidadeAtual?.let {
                        Text("Você está em: ", style = MaterialTheme.typography.bodySmall)
                        Text(it, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    } ?: Text("Não foi possível detectar sua cidade.")

                    Spacer(modifier = Modifier.height(24.dp))

                    uiState.viagemAtual?.let { viagem ->
                        CardViagemAtual(viagem, sdf)
                    } ?: run {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Text(
                                "Nenhuma viagem em andamento para este local hoje. 📭",
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CardViagemAtual(viagem: Viagem, sdf: SimpleDateFormat) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (viagem.tipo == "Lazer") Icons.Default.BeachAccess else Icons.Default.BusinessCenter,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text("Viagem Atual: ${viagem.destino}", style = MaterialTheme.typography.titleLarge)
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            
            Text("🗓️ Período: ${sdf.format(Date(viagem.dataInicio))} - ${sdf.format(Date(viagem.dataFim))}")
            Text("🏷️ Tipo: ${viagem.tipo}")
            Text("💰 Orçamento: R$ ${String.format("%.2f", viagem.orcamento)}")
            Text("📉 Total de Gastos: R$ ${String.format("%.2f", viagem.totalGastos)}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@SuppressLint("MissingPermission")
private fun obterLocalizacao(client: com.google.android.gms.location.FusedLocationProviderClient, userId: String, viewModel: HomeViewModel) {
    client.lastLocation.addOnSuccessListener { location ->
        if (location != null) {
            viewModel.buscarViagemPorLocalizacao(location.latitude, location.longitude, userId)
        }
    }
}
