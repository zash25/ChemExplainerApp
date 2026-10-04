package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.screens.chat.ReactionExplainerScreen
import com.example.ui.screens.molecules.MolecularVisualizerScreen
import com.example.ui.screens.notes.CurriculumNotesScreen
import com.example.ui.screens.progress.ProgressTrackerScreen
import com.example.ui.screens.quiz.QuizScreen
import com.example.ui.theme.ChemMasterTheme

enum class ChemScreen(val label: String, val icon: ImageVector, val tag: String) {
    EXPLAINER("AI Explainer", Icons.AutoMirrored.Filled.Chat, "nav_explainer"),
    MOLECULES("3D Molecules", Icons.Default.Hub, "nav_molecules"),
    QUIZ("Practice Quiz", Icons.Default.Quiz, "nav_quiz"),
    PROGRESS("Mastery", Icons.Default.Insights, "nav_progress"),
    NOTES("Curriculum", Icons.AutoMirrored.Filled.MenuBook, "nav_notes")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChemMasterTheme {
                ChemMasterApp()
            }
        }
    }
}

@Composable
fun ChemMasterApp() {
    var currentScreen by remember { mutableStateOf(ChemScreen.EXPLAINER) }

    // Ensure back press returns to Explainer if on another screen
    BackHandler(enabled = currentScreen != ChemScreen.EXPLAINER) {
        currentScreen = ChemScreen.EXPLAINER
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                modifier = Modifier.testTag("main_bottom_nav"),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                ChemScreen.values().forEach { screen ->
                    val isSelected = currentScreen == screen
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentScreen = screen },
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.label
                            )
                        },
                        label = { Text(screen.label) },
                        modifier = Modifier.testTag(screen.tag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            when (currentScreen) {
                ChemScreen.EXPLAINER -> ReactionExplainerScreen()
                ChemScreen.MOLECULES -> MolecularVisualizerScreen()
                ChemScreen.QUIZ -> QuizScreen()
                ChemScreen.PROGRESS -> ProgressTrackerScreen(
                    onNavigateToQuiz = { currentScreen = ChemScreen.QUIZ }
                )
                ChemScreen.NOTES -> CurriculumNotesScreen(
                    onAskAi = {
                        currentScreen = ChemScreen.EXPLAINER
                    }
                )
            }
        }
    }
}
