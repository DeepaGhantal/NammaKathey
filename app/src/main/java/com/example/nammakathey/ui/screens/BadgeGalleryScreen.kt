package com.example.nammakathey.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nammakathey.viewmodel.StoryViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BadgeGalleryScreen(
    viewModel: StoryViewModel,
    onBack: () -> Unit
) {
    val language by viewModel.language.collectAsState()
    val badges = viewModel.getEarnedBadges()
    val exploredCount = viewModel.getExploredDistrictsCount()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (language == "kn") "ಪದಕ ಗ್ಯಾಲರಿ" else "Badge Gallery") },
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
                .verticalScroll(rememberScrollState())
        ) {
            // Progress Tracker Section
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 4.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (language == "kn") "ಜಿಲ್ಲಾ ಪರಿಶೋಧನೆ ಪ್ರಗತಿ" else "District Exploration Progress",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { exploredCount.toFloat() / 31f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .background(Color.White, RoundedCornerShape(6.dp))
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (language == "kn") "$exploredCount / 31 ಜಿಲ್ಲೆಗಳನ್ನು ಅನ್ವೇಷಿಸಲಾಗಿದೆ" else "$exploredCount of 31 districts explored",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // Milestone Badges
            Text(
                text = if (language == "kn") "ಮೈಲಿಗಲ್ಲುಗಳು" else "Milestone Badges",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                fontWeight = FontWeight.Bold
            )
            
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val milestones = listOf(
                    Triple(1, if (language == "kn") "ಆರಂಭಿಕ" else "Explorer", "1 District"),
                    Triple(5, if (language == "kn") "ಸಾಧಕ" else "Pathfinder", "5 Districts"),
                    Triple(10, if (language == "kn") "ಪರಿಣತ" else "Historian", "10 Districts")
                )
                items(milestones) { (count, title, desc) ->
                    MilestoneCard(
                        title = title,
                        desc = desc,
                        isUnlocked = exploredCount >= count
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Hero Badges
            Text(
                text = if (language == "kn") "ವೀರರ ಪದಕಗಳು" else "Hero Badges",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                fontWeight = FontWeight.Bold
            )

            if (badges.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(if (language == "kn") "ಇನ್ನೂ ಯಾವುದೇ ಪದಕಗಳಿಲ್ಲ!" else "No badges earned yet!")
                }
            } else {
                badges.chunked(3).forEach { rowBadges ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        rowBadges.forEach { badge ->
                            Box(modifier = Modifier.weight(1f)) {
                                BadgeItem(badge, viewModel)
                            }
                        }
                        repeat(3 - rowBadges.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun MilestoneCard(title: String, desc: String, isUnlocked: Boolean) {
    val alpha = if (isUnlocked) 1f else 0.4f
    Card(
        modifier = Modifier.width(140.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = if (isUnlocked) Icons.Default.MilitaryTech else Icons.Default.Star,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = if (isUnlocked) MaterialTheme.colorScheme.primary else Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha))
            Text(text = desc, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha))
        }
    }
}

@Composable
fun BadgeItem(badge: Map<String, String>, viewModel: StoryViewModel) {
    val context = LocalContext.current
    val tier = badge["tier"] ?: "SILVER"
    val heroName = badge["hero"] ?: "Hero"
    val district = badge["district"] ?: "Unknown"
    val dateMillis = badge["date"]?.toLongOrNull() ?: 0L
    
    val imageName = viewModel.getHeroImageByName(heroName)
    val imageResId = imageName?.let { name ->
        context.resources.getIdentifier(name, "drawable", context.packageName)
    } ?: 0

    val dateStr = if (dateMillis > 0) {
        val date = Date(dateMillis)
        val format = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        format.format(date)
    } else ""
    
    val badgeColor = if (tier == "GOLD") Color(0xFFFFD700) else Color(0xFFC0C0C0)

    Column(
        modifier = Modifier.padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(100.dp),
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            shadowElevation = 8.dp,
            border = androidx.compose.foundation.BorderStroke(4.dp, badgeColor)
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (imageResId != 0) {
                    Image(
                        painter = painterResource(id = imageResId),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = Color.LightGray
                    )
                }
                
                // Badge Tier Overlay
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(badgeColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.EmojiEvents,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Color.White
                    )
                }

                // Text overlay at bottom of image
                Surface(
                    modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
                    color = badgeColor.copy(alpha = 0.9f)
                ) {
                    Text(
                        text = tier,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.DarkGray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 1.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = heroName,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            textAlign = TextAlign.Center
        )
        Text(
            text = district.replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline,
            maxLines = 1
        )
        if (dateStr.isNotEmpty()) {
            Text(
                text = dateStr,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}
