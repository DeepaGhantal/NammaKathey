package com.example.nammakathey.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nammakathey.viewmodel.StoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIStoryScreen(
    viewModel: StoryViewModel,
    onBack: () -> Unit
) {
    val aiStory by viewModel.aiStory.collectAsState()
    val isLoading by viewModel.isAiLoading.collectAsState()
    val error by viewModel.aiError.collectAsState()
    val language by viewModel.language.collectAsState()
    val hero by viewModel.selectedHero.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(hero) {
        if (aiStory == null && !isLoading && hero != null) {
            viewModel.generateAiStory()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (language == "kn") "AI ಕಥೆಗಾರ" else "AI Story Mode") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (aiStory != null && !isLoading) {
                        IconButton(onClick = { viewModel.generateAiStory() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Regenerate")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.05f)
                        )
                    )
                )
        ) {
            when {
                isLoading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        hero?.imageName?.let { name ->
                            val imageResId = context.resources.getIdentifier(name, "drawable", context.packageName)
                            if (imageResId != 0) {
                                Box(contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(120.dp),
                                        color = MaterialTheme.colorScheme.primary,
                                        strokeWidth = 4.dp
                                    )
                                    Image(
                                        painter = painterResource(id = imageResId),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(100.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            } else {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.primary,
                                    strokeWidth = 4.dp
                                )
                            }
                        } ?: CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 4.dp
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = if (language == "kn") "ಮ್ಯಾಜಿಕ್ ನಡೆಯುತ್ತಿದೆ..." else "Magic is happening...",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = if (language == "kn") 
                                "${hero?.name?.kn} ಅವರ ಕಥೆಯನ್ನು ಸಿದ್ಧಪಡಿಸುತ್ತಿದ್ದೇನೆ" 
                                else "Preparing a story about ${hero?.name?.en}",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                    }
                }
                error != null -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (language == "kn") "ಕ್ಷಮಿಸಿ, ದೋಷ ಸಂಭವಿಸಿದೆ" else "AI encountered an issue",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        // Display the actual error message
                        Text(
                            text = error ?: "Unknown error",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(onClick = { viewModel.generateAiStory() }) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (language == "kn") "ಮತ್ತೆ ಪ್ರಯತ್ನಿಸಿ" else "Try Again")
                        }
                    }
                }
                aiStory != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 24.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(48.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                                }
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = if (language == "kn") "AI ಕಥೆಗಾರ" else "AI Storyteller",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (language == "kn") "ಮಕ್ಕಳಿಗಾಗಿ ವಿಶೇಷವಾಗಿ ಸಿದ್ಧಪಡಿಸಿದ ಕಥೆ" else "A special story just for you",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }

                        hero?.imageName?.let { name ->
                            val imageResId = context.resources.getIdentifier(name, "drawable", context.packageName)
                            if (imageResId != 0) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .padding(bottom = 16.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Image(
                                        painter = painterResource(id = imageResId),
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }

                        aiStory?.split("\n\n")?.forEach { paragraph ->
                            if (paragraph.isNotBlank()) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color.White
                                    ),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Text(
                                        text = paragraph.trim(),
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontSize = 18.sp,
                                        lineHeight = 28.sp,
                                        modifier = Modifier.padding(20.dp),
                                        textAlign = TextAlign.Justify
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                        HorizontalDivider(modifier = Modifier.alpha(0.3f))
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = if (language == "kn") 
                                "ನೀವು ಈ ಕಥೆಯನ್ನು ಇಷ್ಟಪಟ್ಟಿದ್ದೀರಿ ಎಂದು ನಾನು ಭಾವಿಸುತ್ತೇನೆ! ಮತ್ತೊಂದು ಕಥೆ ಬೇಕಾದರೆ ಮೇಲಿನ ಬಟನ್ ಒತ್ತಿರಿ." 
                                else "I hope you enjoyed this story! Tap the refresh button if you want another version.",
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.outline
                        )
                        
                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }
            }
        }
    }
}
