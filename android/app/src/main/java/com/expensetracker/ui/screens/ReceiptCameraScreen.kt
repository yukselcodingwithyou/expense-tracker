package com.expensetracker.ui.screens

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.exifinterface.media.ExifInterface
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ReceiptCameraScreen(
    onReceiptCaptured: (File) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    // Camera permission
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    
    var flashEnabled by remember { mutableStateOf(false) }
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    var preview by remember { mutableStateOf<Preview?>(null) }
    var cameraProvider: ProcessCameraProvider? by remember { mutableStateOf(null) }
    
    // Request permission launcher
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            onNavigateBack()
        }
    }
    
    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
    
    if (cameraPermissionState.status.isGranted) {
        Box(
            modifier = modifier.fillMaxSize()
        ) {
            // Camera Preview
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx)
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        val provider = cameraProviderFuture.get()
                        cameraProvider = provider
                        
                        preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }
                        
                        imageCapture = ImageCapture.Builder()
                            .setTargetRotation(previewView.display.rotation)
                            .build()
                        
                        try {
                            provider.unbindAll()
                            provider.bindToLifecycle(
                                lifecycleOwner,
                                CameraSelector.DEFAULT_BACK_CAMERA,
                                preview,
                                imageCapture
                            )
                        } catch (exc: Exception) {
                            // Handle exception
                        }
                    }, ContextCompat.getMainExecutor(ctx))
                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )
            
            // UI Overlay
            ReceiptCameraOverlay(
                onCapture = {
                    capturePhoto(
                        context = context,
                        imageCapture = imageCapture,
                        onPhotoCaptured = onReceiptCaptured
                    )
                },
                onNavigateBack = onNavigateBack,
                flashEnabled = flashEnabled,
                onToggleFlash = { flashEnabled = !flashEnabled },
                modifier = Modifier.fillMaxSize()
            )
        }
    } else {
        // Permission denied state
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Camera permission is required to capture receipts",
                    style = MaterialTheme.typography.bodyLarge
                )
                Button(
                    onClick = { requestPermissionLauncher.launch(Manifest.permission.CAMERA) }
                ) {
                    Text("Grant Permission")
                }
                TextButton(onClick = onNavigateBack) {
                    Text("Cancel")
                }
            }
        }
    }
}

@Composable
private fun ReceiptCameraOverlay(
    onCapture: () -> Unit,
    onNavigateBack: () -> Unit,
    flashEnabled: Boolean,
    onToggleFlash: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            
            IconButton(
                onClick = onToggleFlash,
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(
                    imageVector = if (flashEnabled) Icons.Default.FlashOn else Icons.Default.FlashOff,
                    contentDescription = if (flashEnabled) "Flash On" else "Flash Off",
                    tint = Color.White
                )
            }
        }
        
        // Receipt frame guide
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            ReceiptFrameGuide()
        }
        
        // Capture button
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 50.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            FloatingActionButton(
                onClick = onCapture,
                modifier = Modifier.size(80.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Capture Receipt",
                    modifier = Modifier.size(40.dp),
                    tint = Color.White
                )
            }
        }
        
        // Instructions
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 50.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Card(
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.7f)),
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.7f))
            ) {
                Text(
                    text = "Align receipt within the frame",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}

@Composable
private fun ReceiptFrameGuide() {
    // Simple frame guide for receipt alignment
    Box(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .aspectRatio(3f / 4f) // Typical receipt aspect ratio
    ) {
        // Corner markers
        val cornerSize = 20.dp
        val cornerThickness = 3.dp
        
        // Top-left corner
        Box(
            modifier = Modifier
                .size(cornerSize)
                .align(Alignment.TopStart)
        ) {
            Box(
                modifier = Modifier
                    .width(cornerSize)
                    .height(cornerThickness)
                    .background(Color.White)
            )
            Box(
                modifier = Modifier
                    .width(cornerThickness)
                    .height(cornerSize)
                    .background(Color.White)
            )
        }
        
        // Top-right corner
        Box(
            modifier = Modifier
                .size(cornerSize)
                .align(Alignment.TopEnd)
        ) {
            Box(
                modifier = Modifier
                    .width(cornerSize)
                    .height(cornerThickness)
                    .background(Color.White)
                    .align(Alignment.TopEnd)
            )
            Box(
                modifier = Modifier
                    .width(cornerThickness)
                    .height(cornerSize)
                    .background(Color.White)
                    .align(Alignment.TopEnd)
            )
        }
        
        // Bottom-left corner
        Box(
            modifier = Modifier
                .size(cornerSize)
                .align(Alignment.BottomStart)
        ) {
            Box(
                modifier = Modifier
                    .width(cornerSize)
                    .height(cornerThickness)
                    .background(Color.White)
                    .align(Alignment.BottomStart)
            )
            Box(
                modifier = Modifier
                    .width(cornerThickness)
                    .height(cornerSize)
                    .background(Color.White)
                    .align(Alignment.BottomStart)
            )
        }
        
        // Bottom-right corner
        Box(
            modifier = Modifier
                .size(cornerSize)
                .align(Alignment.BottomEnd)
        ) {
            Box(
                modifier = Modifier
                    .width(cornerSize)
                    .height(cornerThickness)
                    .background(Color.White)
                    .align(Alignment.BottomEnd)
            )
            Box(
                modifier = Modifier
                    .width(cornerThickness)
                    .height(cornerSize)
                    .background(Color.White)
                    .align(Alignment.BottomEnd)
            )
        }
    }
}

private fun capturePhoto(
    context: Context,
    imageCapture: ImageCapture?,
    onPhotoCaptured: (File) -> Unit
) {
    val imageCapture = imageCapture ?: return
    
    // Create output file
    val photoFile = File(
        context.cacheDir,
        "receipt_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.jpg"
    )
    
    val outputFileOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
    
    imageCapture.takePicture(
        outputFileOptions,
        Executors.newSingleThreadExecutor(),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                onPhotoCaptured(photoFile)
            }
            
            override fun onError(exception: ImageCaptureException) {
                // Handle error
            }
        }
    )
}