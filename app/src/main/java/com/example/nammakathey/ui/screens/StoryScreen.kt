package com.example.nammakathey.ui.screens

import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nammakathey.viewmodel.StoryViewModel
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryScreen(
    viewModel: StoryViewModel,
    onQuizStart: () -> Unit,
    onBack: () -> Unit
) {
    val hero by viewModel.selectedHero.collectAsState()
    val currentHero = hero // Local copy to allow smart casting
    val language by viewModel.language.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    val pages = currentHero?.storyPages ?: emptyList()
    val pagerState = rememberPagerState(pageCount = { pages.size })
    
    // TTS Setup
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var isTtsReady by remember { mutableStateOf(false) }
    var isSpeaking by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val ttsInstance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isTtsReady = true
            }
        }
        tts = ttsInstance
        
        ttsInstance.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) { isSpeaking = true }
            override fun onDone(utteranceId: String?) { isSpeaking = false }
            
            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) { isSpeaking = false }
            
            override fun onError(utteranceId: String?, errorCode: Int) { 
                super.onError(utteranceId, errorCode)
                isSpeaking = false 
            }
        })

        onDispose {
            ttsInstance.stop()
            ttsInstance.shutdown()
        }
    }

    // Stop TTS when page or language changes
    LaunchedEffect(pagerState.currentPage, language) {
        tts?.stop()
        isSpeaking = false
        isPaused = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(
                            text = if (language == "kn") currentHero?.name?.kn ?: "" else currentHero?.name?.en ?: "",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (pages.isNotEmpty()) {
                            Text(
                                text = if (language == "kn") "ಪುಟ ${pagerState.currentPage + 1} / ${pages.size}" else "Page ${pagerState.currentPage + 1} / ${pages.size}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    FilledTonalButton(
                        onClick = { viewModel.toggleLanguage() },
                        modifier = Modifier.padding(end = 8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(if (language == "kn") "English" else "ಕನ್ನಡ", style = MaterialTheme.typography.labelLarge)
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
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
                        )
                    )
                )
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { pageIndex ->
                val page = pages.getOrNull(pageIndex) ?: return@HorizontalPager
                val text = if (language == "kn") page.text.kn else page.text.en
                val highlight = if (language == "kn") page.highlight.kn else page.highlight.en

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Hero Image on the first page
                    if (pageIndex == 0 && currentHero?.imageName != null) {
                        val imageResId = context.resources.getIdentifier(currentHero.imageName, "drawable", context.packageName)
                        if (imageResId != 0) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .padding(bottom = 24.dp),
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = imageResId),
                                    contentDescription = currentHero.name.en,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }

                    // Highlight Quote Section
                    if (highlight.isNotBlank()) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.FormatQuote,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = highlight,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontStyle = FontStyle.Italic,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    lineHeight = 24.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // Main Story Text
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 20.sp,
                        lineHeight = 34.sp,
                        textAlign = TextAlign.Justify,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.height(40.dp))
                    
                    // Audio Control (Floating Style)
                    if (isTtsReady) {
                        Surface(
                            tonalElevation = 8.dp,
                            shadowElevation = 6.dp,
                            shape = RoundedCornerShape(32.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                if (!isSpeaking || isPaused) {
                                    IconButton(
                                        onClick = {
                                            tts?.let {
                                                it.language = if (language == "kn") Locale("kn", "IN") else Locale.US
                                                it.speak(text, TextToSpeech.QUEUE_FLUSH, null, "story_utterance")
                                                isSpeaking = true
                                                isPaused = false
                                            }
                                        }
                                    ) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = "Read Aloud", tint = MaterialTheme.colorScheme.primary)
                                    }
                                } else {
                                    IconButton(
                                        onClick = {
                                            tts?.stop()
                                            isSpeaking = false
                                            isPaused = true
                                        }
                                    ) {
                                        Icon(Icons.Default.Pause, contentDescription = "Pause", tint = MaterialTheme.colorScheme.primary)
                                    }
                                }
                                
                                Text(
                                    if (language == "kn") "ಓದಿ ಹೇಳಿ" else "Read Aloud",
                                    style = MaterialTheme.typography.labelLarge,
                                    modifier = Modifier.padding(horizontal = 8.dp),
                                    fontWeight = FontWeight.Bold
                                )

                                IconButton(onClick = {
                                    tts?.stop()
                                    isSpeaking = false
                                    isPaused = false
                                }) {
                                    Icon(Icons.Default.Stop, contentDescription = "Stop", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(100.dp)) // Extra space for bottom bar
                }
            }

            // Bottom bar with progress and Next/Quiz button
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, MaterialTheme.colorScheme.surface)
                        )
                    )
                    .padding(16.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 3.dp,
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Page Dots
                        Row(modifier = Modifier.weight(1f)) {
                            repeat(pages.size) { index ->
                                val isSelected = pagerState.currentPage == index
                                Box(
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .size(if (isSelected) 10.dp else 8.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray)
                                )
                            }
                        }
                        
                        if (pages.isNotEmpty() && pagerState.currentPage == pages.size - 1) {
                            Button(
                                onClick = {
                                    tts?.stop()
                                    onQuizStart()
                                },
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                            ) {
                                Text(
                                    if (language == "kn") "ರಸಪ್ರಶ್ನೆ ಪ್ರಾರಂಭಿಸಿ" else "Start Quiz",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else if (pages.isNotEmpty()) {
                            TextButton(
                                onClick = {
                                    scope.launch {
                                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                    }
                                }
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (language == "kn") "ಮುಂದಕ್ಕೆ ಸರಿಸಿ" else "Next",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("➔", color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
