package com.example.nammakathey.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.nammakathey.viewmodel.StoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AskAiScreen(
    viewModel: StoryViewModel,
    onBack: () -> Unit
) {
    val response by viewModel.askAiResponse.collectAsState()
    val isLoading by viewModel.isAskAiLoading.collectAsState()
    val language by viewModel.language.collectAsState()
    val hero by viewModel.selectedHero.collectAsState()

    var query by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (language == "kn") "AI ಗೆ ಕೇಳಿ" else "Ask AI") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = if (language == "kn") 
                    "${hero?.name?.kn} ಅವರ ಬಗ್ಗೆ ಏನು ತಿಳಿಯಬೇಕಿದೆ?" 
                    else "What would you like to know about ${hero?.name?.en}?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { 
                    Text(if (language == "kn") "ಇಲ್ಲಿ ಬರೆಯಿರಿ..." else "Type your question here...") 
                },
                trailingIcon = {
                    IconButton(
                        onClick = { 
                            if (query.isNotBlank()) {
                                viewModel.askAi(query)
                            }
                        },
                        enabled = !isLoading && query.isNotBlank()
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else if (response != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = response!!,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}
