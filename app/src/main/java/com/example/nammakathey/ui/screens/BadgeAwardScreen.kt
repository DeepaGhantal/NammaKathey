package com.example.nammakathey.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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

@Composable
fun BadgeAwardScreen(
    viewModel: StoryViewModel,
    score: Int,
    onContinue: () -> Unit,
    onRetry: () -> Unit
) {
    val language by viewModel.language.collectAsState()
    val hero by viewModel.selectedHero.collectAsState()
    val context = LocalContext.current
    val totalQuestions = hero?.quiz?.size ?: 3

    val isGold = score == totalQuestions
    val isSilver = score >= (totalQuestions * 0.6).toInt() && score < totalQuestions
    val isLowScore = score < (totalQuestions * 0.6).toInt()

    val badgeColor = when {
        isGold -> Color(0xFFFFD700)
        isSilver -> Color(0xFFC0C0C0)
        else -> Color(0xFFCD7F32).copy(alpha = 0.6f)
    }

    // Animation for the badge
    val infiniteTransition = rememberInfiniteTransition(label = "badge")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface,
                        badgeColor.copy(alpha = 0.1f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = when {
                    isGold -> if (language == "kn") "ಅದ್ಭುತ ಸಾಧನೆ!" else "Outstanding!"
                    isSilver -> if (language == "kn") "ಅಭಿನಂದನೆಗಳು!" else "Well Done!"
                    else -> if (language == "kn") "ಉತ್ತಮ ಪ್ರಯತ್ನ!" else "Good Effort!"
                },
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.ExtraBold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = if (language == "kn") "ನಿಮ್ಮ ಅಂಕ: $score / $totalQuestions" else "Your Score: $score / $totalQuestions",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Hero Badge UI with animation
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.scale(scale)
            ) {
                // Glow effect
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .background(badgeColor.copy(alpha = 0.2f), CircleShape)
                )
                
                Surface(
                    modifier = Modifier.size(170.dp),
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 16.dp,
                    border = androidx.compose.foundation.BorderStroke(8.dp, badgeColor)
                ) {
                    val imageResId = hero?.imageName?.let { name ->
                        context.resources.getIdentifier(name, "drawable", context.packageName)
                    } ?: 0
                    
                    if (imageResId != 0) {
                        Image(
                            painter = painterResource(id = imageResId),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Stars,
                                contentDescription = null,
                                modifier = Modifier.size(80.dp),
                                tint = badgeColor
                            )
                        }
                    }
                }
                
                if (!isLowScore) {
                    // Small overlapping trophy icon
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(56.dp)
                            .offset(x = (-4).dp, y = (-4).dp),
                        shape = CircleShape,
                        color = badgeColor,
                        shadowElevation = 6.dp,
                        border = androidx.compose.foundation.BorderStroke(3.dp, Color.White)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            val badgeText = when {
                isGold -> if (language == "kn") "ಚಿನ್ನದ ಪದಕ ಗೆದ್ದಿದ್ದೀರಿ!" else "You won a Gold Badge!"
                isSilver -> if (language == "kn") "ಬೆಳ್ಳಿಯ ಪದಕ ಗೆದ್ದಿದ್ದೀರಿ!" else "You won a Silver Badge!"
                else -> if (language == "kn") "ಇನ್ನೂ ಸ್ವಲ್ಪ ಪ್ರಯತ್ನಿಸಿ!" else "Keep Learning!"
            }

            Text(
                text = badgeText,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = if (isLowScore) MaterialTheme.colorScheme.onSurface else badgeColor
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = when {
                    isGold -> if (language == "kn") 
                        "${hero?.name?.kn} ಅವರ ಕಥೆಯನ್ನು ನೀವು ಸಂಪೂರ್ಣವಾಗಿ ಅರ್ಥೈಸಿಕೊಂಡಿದ್ದೀರಿ!" 
                        else "You have complete mastery over the story of ${hero?.name?.en}!"
                    isSilver -> if (language == "kn")
                        "ನೀವು ಉತ್ತಮವಾಗಿ ಕಥೆಯನ್ನು ತಿಳಿದುಕೊಂಡಿದ್ದೀರಿ."
                        else "You have a great understanding of the story!"
                    else -> if (language == "kn")
                        "ಕಥೆಯನ್ನು ಮತ್ತೊಮ್ಮೆ ಓದಿ ಪದಕ ಗೆಲ್ಲಲು ಪ್ರಯತ್ನಿಸಿ."
                        else "Read the story again to improve your score and win a badge."
                },
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            if (isLowScore) {
                Button(
                    onClick = onRetry,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(if (language == "kn") "ಮತ್ತೆ ಪ್ರಯತ್ನಿಸಿ" else "Try Again", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onContinue,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(if (language == "kn") "ನಂತರ ನೋಡೋಣ" else "Maybe Later", fontSize = 16.sp)
                }
            } else {
                Button(
                    onClick = onContinue,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(if (language == "kn") "ಮುಂದುವರಿಸಿ" else "Continue", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
