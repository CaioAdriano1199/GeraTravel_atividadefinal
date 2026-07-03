package telas

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    onNavigateToFotos: (Int) -> Unit,
    onNavigateToRoteiro: (Viagem) -> Unit,
    homeViewModel: HomeViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val uiState by homeViewModel.uiState.collectAsState()
    val sdf = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // Verifica permissão continuamente para disparar o GPS
    var locationPermissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Callback que recebe as novas coordenadas do GPS
    val locationCallback = remember {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    Log.d("GPS_Coordenadas", "Lat: ${location.latitude}, Lon: ${location.longitude}")
                    homeViewModel.buscarViagemPorLocalizacao(location.latitude, location.longitude, email)
                }
            }
        }
    }

    // Gerenciador de Permissão
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        locationPermissionGranted = permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
                                    permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)
    }

    // Inicia/Para o GPS conforme a permissão ou ciclo de vida
    LaunchedEffect(locationPermissionGranted) {
        if (locationPermissionGranted) {
            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 15000)
                .setMinUpdateIntervalMillis(5000)
                .build()
            try {
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
            } catch (e: SecurityException) {
                Log.e("TelaHome", "Erro de segurança ao pedir GPS", e)
            }
        } else {
            locationPermissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    // Garante que o GPS pare quando sair da tela
    DisposableEffect(Unit) {
        onDispose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    BackHandler(enabled = true) {
        if (drawerState.isOpen) scope.launch { drawerState.close() }
        else (context as? ComponentActivity)?.finish()
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
                    },
                    actions = {
                        // Botão manual de atualização como segurança
                        IconButton(onClick = { 
                            try {
                                fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                                    .addOnSuccessListener { loc -> 
                                        loc?.let { homeViewModel.buscarViagemPorLocalizacao(it.latitude, it.longitude, email) }
                                    }
                            } catch (e: Exception) { Log.e("Home", "Erro refresh", e) }
                        }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Atualizar")
                        }
                    }
                )
            },
            bottomBar = {
                if (uiState.viagemAtual != null) {
                    NavigationBar(
                        modifier = Modifier.height(64.dp),
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        windowInsets = WindowInsets(0)
                    ) {
                        NavigationBarItem(
                            alwaysShowLabel = true,
                            icon = { Icon(Icons.AutoMirrored.Filled.ListAlt, null, Modifier.size(20.dp)) },
                            label = { Text("Roteiro", fontSize = 10.sp) },
                            selected = false,
                            onClick = { onNavigateToRoteiro(uiState.viagemAtual!!) }
                        )
                        NavigationBarItem(
                            alwaysShowLabel = true,
                            icon = { Icon(Icons.Default.PhotoLibrary, null, Modifier.size(20.dp)) },
                            label = { Text("Fotos", fontSize = 10.sp) },
                            selected = false,
                            onClick = { onNavigateToFotos(uiState.viagemAtual!!.id) }
                        )
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues).fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Olá, $email!", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(16.dp))

                if (uiState.carregandoLocalizacao && uiState.cidadeAtual == null) {
                    CircularProgressIndicator()
                    Text("Buscando sua localização...", modifier = Modifier.padding(8.dp))
                } else {
                    uiState.cidadeAtual?.let {
                        Text("Você está em: ", style = MaterialTheme.typography.bodySmall)
                        Text(it, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    uiState.viagemAtual?.let { viagem ->
                        CardViagemAtual(viagem, sdf)
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
                            MapaViagem(destino = viagem.destino)
                        }
                    } ?: run {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Text("Nenhuma viagem ativa para este local hoje. 📭", modifier = Modifier.padding(16.dp))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Dica: Se você mudou de cidade, use o botão de atualizar! 🔄", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
fun MapaViagem(destino: String) {
    val context = LocalContext.current
    var localizacao by remember { mutableStateOf<LatLng?>(null) }
    
    LaunchedEffect(destino) {
        withContext(Dispatchers.IO) {
            try {
                val addresses = Geocoder(context).getFromLocationName(destino, 1)
                if (addresses?.isNotEmpty() == true) {
                    localizacao = LatLng(addresses[0].latitude, addresses[0].longitude)
                }
            } catch (e: Exception) { Log.e("Mapa", "Erro geocoder", e) }
        }
    }

    localizacao?.let { pos ->
        val cameraPositionState = rememberCameraPositionState { position = CameraPosition.fromLatLngZoom(pos, 12f) }
        GoogleMap(modifier = Modifier.fillMaxSize(), cameraPositionState = cameraPositionState) {
            Marker(state = rememberMarkerState(position = pos), title = destino)
        }
    } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Carregando mapa...") }
}

@Composable
fun CardViagemAtual(viagem: Viagem, sdf: SimpleDateFormat) {
    val locale = LocalConfiguration.current.locales[0]
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
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(8.dp))
                Text("Viagem Atual: ${viagem.destino}", style = MaterialTheme.typography.titleLarge)
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            Text("🗓️ Período: ${sdf.format(Date(viagem.dataInicio))} - ${sdf.format(Date(viagem.dataFim))}")
            Text("💰 Orçamento: R$ ${String.format(locale, "%.2f", viagem.orcamento)}")
            Text("📉 Gastos: R$ ${String.format(locale, "%.2f", viagem.totalGastos)}", fontWeight = FontWeight.Bold)
        }
    }
}
