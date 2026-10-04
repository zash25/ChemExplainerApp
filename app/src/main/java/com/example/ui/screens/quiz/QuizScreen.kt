package com.example.ui.screens.quiz

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.api.GeminiApiClient
import com.example.data.db.AppDatabase
import com.example.data.db.MasteryProgressEntity
import com.example.data.db.QuizResultEntity
import com.example.data.firebase.FirebaseAuthManager
import com.example.data.firebase.FirestoreSyncManager
import com.example.data.knowledge.ChemistryKnowledgeBase
import com.example.data.knowledge.QuizQuestion
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class QuizScreenState {
    TOPIC_SELECTION,
    IN_PROGRESS,
    FINISHED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val db = remember { AppDatabase.getDatabase(context) }
    val geminiClient = remember { GeminiApiClient(context) }
    val authManager = remember { FirebaseAuthManager(context) }
    val firestoreSyncManager = remember { FirestoreSyncManager(context) }

    var screenState by remember { mutableStateOf(QuizScreenState.TOPIC_SELECTION) }
    var selectedTopicId by remember { mutableStateOf("all") }
    var isAiGenerating by remember { mutableStateOf(false) }
    var customTopicInput by remember { mutableStateOf("") }

    var activeQuestions by remember { mutableStateOf<List<QuizQuestion>>(emptyList()) }
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var isSubmitted by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }
    var elapsedSeconds by remember { mutableIntStateOf(0) }

    // Timer effect during active quiz
    LaunchedEffect(screenState) {
        if (screenState == QuizScreenState.IN_PROGRESS) {
            elapsedSeconds = 0
            while (screenState == QuizScreenState.IN_PROGRESS) {
                delay(1000)
                elapsedSeconds++
            }
        }
    }

    fun startQuiz(questions: List<QuizQuestion>) {
        activeQuestions = questions
        currentQuestionIndex = 0
        selectedOptionIndex = null
        isSubmitted = false
        score = 0
        screenState = QuizScreenState.IN_PROGRESS
    }

    fun finishQuiz() {
        screenState = QuizScreenState.FINISHED
        val total = activeQuestions.size
        val pct = if (total > 0) (score * 100) / total else 0

        coroutineScope.launch {
            val quizEntity = QuizResultEntity(
                topicId = selectedTopicId,
                topicTitle = if (selectedTopicId == "all") "Comprehensive Chemistry" else selectedTopicId,
                score = score,
                totalQuestions = total,
                percentage = pct,
                timeTakenSeconds = elapsedSeconds
            )
            db.chemistryDao().insertQuizResult(quizEntity)

            // Update Mastery Domain
            val domainKey = when (selectedTopicId) {
                "intro_matter_separation" -> "FUNDAMENTALS"
                "elements_valency_orbitals", "periodic_trends" -> "PERIODIC_TRENDS"
                "redox_balancing", "electrolysis_faraday" -> "REDOX_ELECTRO"
                "hydrocarbons_organic" -> "ORGANIC"
                else -> "BIOCHEM"
            }
            val existing = db.chemistryDao().getMastery(domainKey)
            val prevAnswered = existing?.questionsAnswered ?: 0
            val prevCorrect = existing?.correctCount ?: 0
            val newAnswered = prevAnswered + total
            val newCorrect = prevCorrect + score
            val newScore = if (newAnswered > 0) ((newCorrect * 100) / newAnswered) else pct

            val title = when (domainKey) {
                "FUNDAMENTALS" -> "Fundamentals & Separation"
                "PERIODIC_TRENDS" -> "Orbitals & Periodic Trends"
                "REDOX_ELECTRO" -> "Redox & Electrochemistry"
                "ORGANIC" -> "Organic & Hydrocarbons"
                else -> "Biochemistry & Polymers"
            }

            val masteryEntity = MasteryProgressEntity(
                domainKey = domainKey,
                domainTitle = title,
                masteryScore = newScore,
                questionsAnswered = newAnswered,
                correctCount = newCorrect
            )
            db.chemistryDao().upsertMastery(masteryEntity)

            // Sync to Cloud Firestore if user is authenticated
            val currentUser = authManager.authState.value.user
            if (currentUser != null) {
                firestoreSyncManager.syncQuizResult(currentUser.uid, quizEntity)
                firestoreSyncManager.syncMastery(currentUser.uid, masteryEntity)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Practice Quiz Generator",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = when (screenState) {
                                QuizScreenState.TOPIC_SELECTION -> "Test your concepts & earn mastery"
                                QuizScreenState.IN_PROGRESS -> "Question ${currentQuestionIndex + 1} of ${activeQuestions.size}"
                                QuizScreenState.FINISHED -> "Results & Analysis"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    if (screenState == QuizScreenState.IN_PROGRESS) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.padding(end = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Timer, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "%02d:%02d".format(elapsedSeconds / 60, elapsedSeconds % 60),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (screenState) {
                QuizScreenState.TOPIC_SELECTION -> {
                    TopicSelectionContent(
                        selectedTopicId = selectedTopicId,
                        onSelectTopic = { selectedTopicId = it },
                        customTopicInput = customTopicInput,
                        onCustomTopicChange = { customTopicInput = it },
                        isAiGenerating = isAiGenerating,
                        onStartCurriculumQuiz = {
                            val list = if (selectedTopicId == "all") {
                                ChemistryKnowledgeBase.practiceQuestions.shuffled().take(6)
                            } else {
                                ChemistryKnowledgeBase.practiceQuestions.filter { it.topicId == selectedTopicId }.ifEmpty {
                                    ChemistryKnowledgeBase.practiceQuestions.shuffled().take(5)
                                }
                            }
                            startQuiz(list)
                        },
                        onGenerateAiQuiz = {
                            isAiGenerating = true
                            coroutineScope.launch {
                                val aiQuestions = geminiClient.generateCustomQuizQuestions(
                                    if (customTopicInput.isNotBlank()) customTopicInput else selectedTopicId,
                                    count = 4
                                )
                                isAiGenerating = false
                                startQuiz(aiQuestions)
                            }
                        }
                    )
                }

                QuizScreenState.IN_PROGRESS -> {
                    if (activeQuestions.isNotEmpty() && currentQuestionIndex < activeQuestions.size) {
                        val currentQ = activeQuestions[currentQuestionIndex]
                        ActiveQuizContent(
                            question = currentQ,
                            currentIndex = currentQuestionIndex,
                            totalCount = activeQuestions.size,
                            selectedOption = selectedOptionIndex,
                            isSubmitted = isSubmitted,
                            onSelectOption = { if (!isSubmitted) selectedOptionIndex = it },
                            onSubmit = {
                                isSubmitted = true
                                if (selectedOptionIndex == currentQ.correctIndex) {
                                    score++
                                }
                            },
                            onNext = {
                                if (currentQuestionIndex + 1 < activeQuestions.size) {
                                    currentQuestionIndex++
                                    selectedOptionIndex = null
                                    isSubmitted = false
                                } else {
                                    finishQuiz()
                                }
                            }
                        )
                    }
                }

                QuizScreenState.FINISHED -> {
                    QuizResultContent(
                        score = score,
                        total = activeQuestions.size,
                        timeSeconds = elapsedSeconds,
                        questions = activeQuestions,
                        onRetake = {
                            startQuiz(activeQuestions.shuffled())
                        },
                        onDone = {
                            screenState = QuizScreenState.TOPIC_SELECTION
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TopicSelectionContent(
    selectedTopicId: String,
    onSelectTopic: (String) -> Unit,
    customTopicInput: String,
    onCustomTopicChange: (String) -> Unit,
    isAiGenerating: Boolean,
    onStartCurriculumQuiz: () -> Unit,
    onGenerateAiQuiz: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // AI Generator Banner Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                ),
                modifier = Modifier.fillMaxWidth().testTag("ai_quiz_banner_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.AutoAwesome, null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Gemini AI Custom Quiz",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Generates new challenges on any chemistry concept",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = customTopicInput,
                        onValueChange = onCustomTopicChange,
                        placeholder = { Text("E.g. Faraday's 2nd Law, Esterification, Ionization trends...", fontSize = 13.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("custom_topic_quiz_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = onGenerateAiQuiz,
                        enabled = !isAiGenerating,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("generate_ai_quiz_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isAiGenerating) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generating Questions...")
                        } else {
                            Icon(Icons.Default.Bolt, null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generate AI Practice Quiz")
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "Choose Curriculum Topic",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // All Topics Option
        item {
            TopicSelectCard(
                title = "Comprehensive Chemistry (All Modules)",
                subtitle = "Mix of SS1, SS2, and SS3 questions",
                level = "All Levels",
                isSelected = selectedTopicId == "all",
                onClick = { onSelectTopic("all") },
                testTag = "topic_card_all"
            )
        }

        // Specific Topics
        itemsIndexed(ChemistryKnowledgeBase.topics) { index, topic ->
            TopicSelectCard(
                title = topic.title,
                subtitle = topic.summary,
                level = topic.level,
                isSelected = selectedTopicId == topic.id,
                onClick = { onSelectTopic(topic.id) },
                testTag = "topic_card_$index"
            )
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onStartCurriculumQuiz,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("start_curriculum_quiz_button"),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.PlayArrow, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Start Practice Quiz", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun TopicSelectCard(
    title: String,
    subtitle: String,
    level: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(
                            text = level,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
private fun ActiveQuizContent(
    question: QuizQuestion,
    currentIndex: Int,
    totalCount: Int,
    selectedOption: Int?,
    isSubmitted: Boolean,
    onSelectOption: (Int) -> Unit,
    onSubmit: () -> Unit,
    onNext: () -> Unit
) {
    val progress = (currentIndex + 1).toFloat() / totalCount.toFloat()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Progress bar
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Question Card
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = question.question,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 24.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Options List
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(question.options.size) { optIndex ->
                val optionText = question.options[optIndex]
                val isSelected = selectedOption == optIndex
                val isCorrect = optIndex == question.correctIndex

                val optionColor = when {
                    isSubmitted && isCorrect -> Color(0xFF10B981) // Green
                    isSubmitted && isSelected && !isCorrect -> Color(0xFFEF4444) // Red
                    isSelected -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSubmitted) {
                        if (isCorrect) Color(0xFF10B981).copy(alpha = 0.15f)
                        else if (isSelected) Color(0xFFEF4444).copy(alpha = 0.15f)
                        else MaterialTheme.colorScheme.surface
                    } else {
                        if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        else MaterialTheme.colorScheme.surface
                    },
                    border = BorderStroke(
                        width = if (isSelected || (isSubmitted && isCorrect)) 2.dp else 1.dp,
                        color = optionColor
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(enabled = !isSubmitted) { onSelectOption(optIndex) }
                        .testTag("quiz_option_$optIndex")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = optionColor.copy(alpha = 0.2f),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = ('A'.code + optIndex).toChar().toString(),
                                    fontWeight = FontWeight.Bold,
                                    color = optionColor
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = optionText,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )

                        if (isSubmitted) {
                            if (isCorrect) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Correct", tint = Color(0xFF10B981))
                            } else if (isSelected) {
                                Icon(Icons.Default.Cancel, contentDescription = "Incorrect", tint = Color(0xFFEF4444))
                            }
                        }
                    }
                }
            }

            // Step-by-step reasoning reveal card
            if (isSubmitted) {
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("quiz_explanation_card")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Lightbulb, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Explanation & Chemistry Logic",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = question.explanation,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }
        }

        // Action Button: Submit or Next
        Spacer(modifier = Modifier.height(12.dp))
        if (!isSubmitted) {
            Button(
                onClick = onSubmit,
                enabled = selectedOption != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("submit_quiz_answer_button"),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Confirm Answer", fontWeight = FontWeight.Bold)
            }
        } else {
            Button(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("next_quiz_question_button"),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = if (currentIndex + 1 < totalCount) "Next Question" else "See Results",
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, null)
            }
        }
    }
}

@Composable
private fun QuizResultContent(
    score: Int,
    total: Int,
    timeSeconds: Int,
    questions: List<QuizQuestion>,
    onRetake: () -> Unit,
    onDone: () -> Unit
) {
    val percentage = if (total > 0) (score * 100) / total else 0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Surface(
                shape = CircleShape,
                color = if (percentage >= 70) Color(0xFF10B981).copy(alpha = 0.2f) else Color(0xFFF59E0B).copy(alpha = 0.2f),
                modifier = Modifier.size(100.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (percentage >= 70) Icons.Default.EmojiEvents else Icons.Default.School,
                        contentDescription = null,
                        tint = if (percentage >= 70) Color(0xFF10B981) else Color(0xFFF59E0B),
                        modifier = Modifier.size(52.dp)
                    )
                }
            }
        }

        item {
            Text(
                text = if (percentage >= 80) "Outstanding Mastery! 🌟" else if (percentage >= 50) "Solid Practice! Keep Going 👍" else "Review Recommended 📖",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Score: $score / $total ($percentage%) • Time: ${timeSeconds}s",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onRetake,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("retake_quiz_button"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Refresh, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Retake Quiz")
                }

                Button(
                    onClick = onDone,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("done_quiz_button"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Done")
                }
            }
        }

        item {
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Question Breakdown",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
        }

        itemsIndexed(questions) { qIdx, q ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Q${qIdx + 1}: ${q.question}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Correct Answer: ${q.options[q.correctIndex]}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF10B981),
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = q.explanation,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
