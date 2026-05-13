package com.example.nammakathey.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nammakathey.viewmodel.StoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    viewModel: StoryViewModel,
    onQuizComplete: (Int) -> Unit,
    onBack: () -> Unit
) {
    val hero by viewModel.selectedHero.collectAsState()
    val language by viewModel.language.collectAsState()
    val questions = hero?.quiz ?: emptyList()

    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var isAnswered by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (language == "kn") "ರಸಪ್ರಶ್ನೆ" else "Quiz Challenge") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Progress Bar
            val progress = (currentQuestionIndex + 1).toFloat() / questions.size
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${currentQuestionIndex + 1} / ${questions.size}",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )

            if (questions.isNotEmpty() && currentQuestionIndex < questions.size) {
                val currentQuestion = questions[currentQuestionIndex]

                Spacer(modifier = Modifier.height(32.dp))
                
                Text(
                    text = if (language == "kn") currentQuestion.question.kn else currentQuestion.question.en,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 32.sp
                )
                
                Spacer(modifier = Modifier.height(40.dp))

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    currentQuestion.options.forEachIndexed { index, option ->
                        val isCorrect = index == currentQuestion.correctIndex
                        val isSelected = selectedOptionIndex == index
                        
                        val containerColor = when {
                            isAnswered && isCorrect -> Color(0xFF4CAF50) // Green for correct
                            isAnswered && isSelected && !isCorrect -> Color(0xFFF44336) // Red for wrong selection
                            else -> MaterialTheme.colorScheme.secondaryContainer
                        }

                        val contentColor = when {
                            isAnswered && (isCorrect || isSelected) -> Color.White
                            else -> MaterialTheme.colorScheme.onSecondaryContainer
                        }

                        Button(
                            onClick = {
                                if (!isAnswered) {
                                    selectedOptionIndex = index
                                    isAnswered = true
                                    if (isCorrect) score++
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = containerColor,
                                contentColor = contentColor
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (language == "kn") option.kn else option.en,
                                    modifier = Modifier.weight(1f),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                                if (isAnswered && isCorrect) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = "Correct")
                                } else if (isAnswered && isSelected && !isCorrect) {
                                    Icon(Icons.Default.Cancel, contentDescription = "Incorrect")
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                if (isAnswered) {
                    Button(
                        onClick = {
                            if (currentQuestionIndex < questions.size - 1) {
                                currentQuestionIndex++
                                isAnswered = false
                                selectedOptionIndex = null
                            } else {
                                onQuizComplete(score)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (currentQuestionIndex == questions.size - 1) 
                                (if (language == "kn") "ಮುಗಿಸಿ" else "See Results") 
                                else (if (language == "kn") "ಮುಂದಿನ ಪ್ರಶ್ನೆ" else "Next Question"),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
