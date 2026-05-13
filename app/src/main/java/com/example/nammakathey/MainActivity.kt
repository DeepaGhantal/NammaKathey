package com.example.nammakathey

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.nammakathey.ui.screens.*
import com.example.nammakathey.ui.theme.NammaKatheyTheme
import com.example.nammakathey.viewmodel.StoryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NammaKatheyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewModel: StoryViewModel = hiltViewModel()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(onNavigateToNext = {
                if (viewModel.isFirstRun()) {
                    navController.navigate("onboarding") {
                        popUpTo("splash") { inclusive = true }
                    }
                } else {
                    navController.navigate("home") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            })
        }
        composable("onboarding") {
            OnboardingScreen(onFinished = { lang ->
                viewModel.completeOnboarding(lang)
                navController.navigate("home") {
                    popUpTo("onboarding") { inclusive = true }
                }
            })
        }
        composable("home") {
            HomeScreen(
                viewModel = viewModel,
                onDistrictClick = { districtId ->
                    viewModel.selectDistrict(districtId)
                    navController.navigate("district_detail")
                },
                onNavigateToBadges = {
                    navController.navigate("badge_gallery")
                }
            )
        }
        composable("district_detail") {
            DistrictDetailScreen(
                viewModel = viewModel,
                onHeroClick = { hero ->
                    viewModel.selectHero(hero)
                    navController.navigate("hero_detail")
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable("hero_detail") {
            HeroDetailScreen(
                viewModel = viewModel,
                onReadStory = { navController.navigate("story") },
                onAIStoryMode = { navController.navigate("ai_story") },
                onBack = { navController.popBackStack() }
            )
        }
        composable("story") {
            StoryScreen(
                viewModel = viewModel,
                onQuizStart = { navController.navigate("quiz") },
                onBack = { navController.popBackStack() }
            )
        }
        composable("quiz") {
            QuizScreen(
                viewModel = viewModel,
                onQuizComplete = { score ->
                    navController.navigate("badge_award/$score") {
                        popUpTo("quiz") { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            "badge_award/{score}",
            arguments = listOf(navArgument("score") { type = NavType.IntType })
        ) { backStackEntry ->
            val score = backStackEntry.arguments?.getInt("score") ?: 0
            BadgeAwardScreen(
                viewModel = viewModel,
                score = score,
                onContinue = {
                    viewModel.completeQuiz(score)
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onRetry = {
                    navController.navigate("quiz") {
                        popUpTo("badge_award/$score") { inclusive = true }
                    }
                }
            )
        }
        composable("badge_gallery") {
            BadgeGalleryScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("ai_story") {
            AIStoryScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
