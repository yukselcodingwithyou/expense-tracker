package com.expensetracker.ui.screens

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.util.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun VoiceExpenseEntryScreen(
    onVoiceProcessed: (String) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    // Audio permission
    val audioPermissionState = rememberPermissionState(Manifest.permission.RECORD_AUDIO)
    
    var isListening by remember { mutableStateOf(false) }
    var spokenText by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }
    var speechRecognizer: SpeechRecognizer? by remember { mutableStateOf(null) }
    
    // Request permission launcher
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            onNavigateBack()
        }
    }
    
    LaunchedEffect(Unit) {
        if (!audioPermissionState.status.isGranted) {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }
    
    // Initialize speech recognizer
    LaunchedEffect(audioPermissionState.status.isGranted) {
        if (audioPermissionState.status.isGranted) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        isListening = true
                    }
                    
                    override fun onBeginningOfSpeech() {}
                    
                    override fun onRmsChanged(rmsdB: Float) {}
                    
                    override fun onBufferReceived(buffer: ByteArray?) {}
                    
                    override fun onEndOfSpeech() {
                        isListening = false
                    }
                    
                    override fun onError(error: Int) {
                        isListening = false
                        // Handle specific errors if needed
                    }
                    
                    override fun onResults(results: Bundle?) {
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        spokenText = matches?.firstOrNull() ?: ""
                        isListening = false
                    }
                    
                    override fun onPartialResults(partialResults: Bundle?) {
                        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        spokenText = matches?.firstOrNull() ?: ""
                    }
                    
                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }
        }
    }
    
    DisposableEffect(Unit) {
        onDispose {
            speechRecognizer?.destroy()
        }
    }
    
    if (audioPermissionState.status.isGranted) {
        VoiceInputContent(
            spokenText = spokenText,
            isListening = isListening,
            isProcessing = isProcessing,
            onStartListening = {
                if (!isListening) {
                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                        putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
                    }
                    speechRecognizer?.startListening(intent)
                }
            },
            onStopListening = {
                speechRecognizer?.stopListening()
            },
            onProcessVoice = {
                if (spokenText.isNotEmpty()) {
                    isProcessing = true
                    onVoiceProcessed(spokenText)
                }
            },
            onNavigateBack = onNavigateBack,
            onClearText = { spokenText = "" },
            modifier = modifier
        )
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
                    text = "Microphone permission is required for voice input",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                Button(
                    onClick = { requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO) }
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
private fun VoiceInputContent(
    spokenText: String,
    isListening: Boolean,
    isProcessing: Boolean,
    onStartListening: () -> Unit,
    onStopListening: () -> Unit,
    onProcessVoice: () -> Unit,
    onNavigateBack: () -> Unit,
    onClearText: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
            
            Text(
                text = "Voice Expense Entry",
                style = MaterialTheme.typography.headlineSmall
            )
            
            if (spokenText.isNotEmpty()) {
                TextButton(onClick = onClearText) {
                    Text("Clear")
                }
            } else {
                Spacer(modifier = Modifier.width(48.dp))
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Status text
        Text(
            text = when {
                isProcessing -> "Processing your expense..."
                isListening -> "Listening... Say your expense"
                spokenText.isNotEmpty() -> "Tap the send button to process"
                else -> "Tap the microphone to start recording"
            },
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Spoken text display
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = if (spokenText.isEmpty()) Alignment.Center else Alignment.TopStart
            ) {
                if (spokenText.isNotEmpty()) {
                    Text(
                        text = spokenText,
                        style = MaterialTheme.typography.bodyLarge
                    )
                } else {
                    Text(
                        text = "Your spoken expense will appear here\n\nExample: \"I spent 25 dollars on coffee at Starbucks\"",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Control buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Microphone button
            VoiceInputButton(
                isListening = isListening,
                onStartListening = onStartListening,
                onStopListening = onStopListening,
                enabled = !isProcessing
            )
            
            // Send button
            if (spokenText.isNotEmpty()) {
                FloatingActionButton(
                    onClick = onProcessVoice,
                    modifier = Modifier.size(56.dp),
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Process Voice",
                            tint = Color.White
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun VoiceInputButton(
    isListening: Boolean,
    onStartListening: () -> Unit,
    onStopListening: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isListening) 1.2f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mic_scale"
    )
    
    FloatingActionButton(
        onClick = {
            if (isListening) {
                onStopListening()
            } else {
                onStartListening()
            }
        },
        modifier = modifier
            .size(80.dp)
            .scale(scale),
        containerColor = if (isListening) 
            MaterialTheme.colorScheme.error 
        else 
            MaterialTheme.colorScheme.primary
    ) {
        Icon(
            imageVector = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
            contentDescription = if (isListening) "Stop Recording" else "Start Recording",
            modifier = Modifier.size(40.dp),
            tint = Color.White
        )
    }
}