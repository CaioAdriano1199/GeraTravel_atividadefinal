package telas

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import viewmodel.FotosViewModel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaFotos(
    viagemId: Int,
    onNavigateBack: () -> Unit,
    viewModel: FotosViewModel = viewModel()
) {
    val context = LocalContext.current
    val fotos by viewModel.fotos.collectAsState()
    
    LaunchedEffect(viagemId) {
        viewModel.carregarFotos(viagemId)
    }

    // Launcher para Galeria
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.adicionarFoto(viagemId, it.toString()) }
    }

    // Launcher para Câmera (Preview simples para demonstração rápida)
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            val path = salvarImagemInterna(context, it)
            if (path != null) {
                viewModel.adicionarFoto(viagemId, path)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Fotos da Viagem 📸") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        floatingActionButton = {
            Column {
                FloatingActionButton(
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier.padding(bottom = 8.dp),
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = "Galeria")
                }
                FloatingActionButton(
                    onClick = { cameraLauncher.launch() }
                ) {
                    Icon(Icons.Default.AddAPhoto, contentDescription = "Câmera")
                }
            }
        }
    ) { padding ->
        if (fotos.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Nenhuma foto adicionada ainda.")
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(fotos) { foto ->
                    AsyncImage(
                        model = foto.path,
                        contentDescription = null,
                        modifier = Modifier.aspectRatio(1f).fillMaxWidth(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}

private fun salvarImagemInterna(context: Context, bitmap: Bitmap): String? {
    val filename = "viagem_${System.currentTimeMillis()}.jpg"
    val file = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), filename)
    return try {
        val out = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        out.flush()
        out.close()
        Uri.fromFile(file).toString()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
