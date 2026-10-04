package com.example.ui.screens.chat

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.api.GeminiApiClient
import com.example.data.db.AppDatabase
import com.example.data.db.ChatMessageEntity
import com.example.data.db.SavedReactionEntity
import com.example.util.TtsManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class UiChatMessage(
    val id: String,
    val isUser: Boolean,
    val text: String,
    val modelTag: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReactionExplainerScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val geminiClient = remember { GeminiApiClient(context) }
    val ttsManager = remember { TtsManager(context) }
    val db = remember { AppDatabase.getDatabase(context) }
    val isSpeaking by ttsManager.isSpeaking.collectAsState()

    var inputText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var selectedModel by remember { mutableStateOf("gemini-3.8-flash") }
    var showLiveVoiceDialog by remember { mutableStateOf(false) }

    val initialWelcome = remember {
        UiChatMessage(
            id = "welcome",
            isUser = false,
            text = """
                👋 Hello! I am **Dr. Rutherford**, your AI Chemistry Tutor powered by **Gemini 3.8 Flash**.
                
                I'm grounded directly in your syllabus notes (SS1, SS2, SS3). Ask me about:
                • **Complex Reaction Mechanisms** (Redox balancing, Esterification, Dehydration, Addition)
                • **Electrolysis & Faraday's Laws** (Electrode reactions, preferential discharge, calculations)
                • **Periodic Trends & Orbitals** (Ionization energies, electron affinities, Aufbau principle)
                • **Biomolecules & Polymers** (Soaps, detergents, glucose, proteins, plastics)
                
                Tap **'Live Voice Lab'** above to speak in real-time with **gemini-3.8-live**!
            """.trimIndent()
        )
    }

    val messages = remember {
        mutableStateListOf(initialWelcome)
    }

    val listState = rememberLazyListState()

    val suggestedPrompts = listOf(
        "Balance: MnO4- + I- in acidic solution",
        "Electrolysis: CuSO4 with Pt vs Cu electrodes",
        "How do soaps and detergents clean grease?",
        "Why is IE of Be (899 kJ) higher than B (800 kJ)?",
        "Faraday's laws calculation: 4A for 1800s in CuSO4",
        "Explain addition reactions of ethene with KMnO4",
        "Distinguish between aliphatic alkynes and alkenes",
        "Describe tests for proteins (Biuret & Millon's)"
    )

    val modelOptions = listOf(
        "gemini-3.8-flash" to "3.8 Flash (Balanced)",
        "gemini-3.1-pro-preview" to "3.1 Pro (Complex Reasoning)",
        "gemini-3.1-flash-lite-preview" to "3.1 Flash-Lite (Fast)"
    )

    DisposableEffect(Unit) {
        onDispose {
            ttsManager.shutdown()
        }
    }

    fun sendMessage(queryText: String) {
        if (queryText.isBlank() || isLoading) return
        val userMsg = UiChatMessage(id = "user_${System.currentTimeMillis()}", isUser = true, text = queryText)
        messages.add(userMsg)
        inputText = ""
        isLoading = true

        coroutineScope.launch {
            listState.animateScrollToItem(messages.size - 1)
            // Persist to Room
            db.chemistryDao().insertChatMessage(
                ChatMessageEntity(sender = "USER", content = queryText)
            )

            // Convert history
            val history = messages.drop(1).map {
                (if (it.isUser) "USER" else "AI") to it.text
            }

            val reply = geminiClient.explainReactionWithRag(queryText, history, modelName = selectedModel)
            val aiMsg = UiChatMessage(
                id = "ai_${System.currentTimeMillis()}",
                isUser = false,
                text = reply,
                modelTag = selectedModel
            )
            messages.add(aiMsg)
            isLoading = false
            listState.animateScrollToItem(messages.size - 1)

            // Persist to Room
            db.chemistryDao().insertChatMessage(
                ChatMessageEntity(sender = "AI", content = reply)
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Science,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Reaction Explainer AI",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Dr. Rutherford (Model: $selectedModel)",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                actions = {
                    // Live Voice Lab Button
                    FilledTonalButton(
                        onClick = { showLiveVoiceDialog = true },
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .testTag("open_live_voice_lab_button"),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.RecordVoiceOver, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Live Voice", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    if (isSpeaking) {
                        IconButton(
                            onClick = { ttsManager.stop() },
                            modifier = Modifier.testTag("stop_speaking_button")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.VolumeOff, contentDescription = "Stop Speech", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                    IconButton(
                        onClick = {
                            messages.clear()
                            messages.add(initialWelcome)
                            ttsManager.stop()
                        },
                        modifier = Modifier.testTag("clear_chat_button")
                    ) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = "Clear Chat")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Model selector chips row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Model:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                modelOptions.forEach { (modelKey, label) ->
                    val isSelected = selectedModel == modelKey
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedModel = modelKey },
                        label = { Text(label, fontSize = 11.sp) },
                        modifier = Modifier.testTag("chip_model_$modelKey")
                    )
                }
            }

            // Suggested Prompts Row
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(suggestedPrompts) { prompt ->
                    SuggestionChip(
                        onClick = { sendMessage(prompt) },
                        label = { Text(prompt, fontSize = 12.sp) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }
            }

            // Chat Message Thread
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(messages, key = { it.id }) { msg ->
                    ChatBubble(
                        message = msg,
                        isSpeaking = isSpeaking,
                        onSpeak = { text ->
                            if (isSpeaking) ttsManager.stop() else ttsManager.speak(text)
                        },
                        onSave = { text ->
                            coroutineScope.launch {
                                db.chemistryDao().insertSavedReaction(
                                    SavedReactionEntity(
                                        title = "Saved Explanation",
                                        equation = text.take(60),
                                        category = "AI Explanation",
                                        explanation = text
                                    )
                                )
                            }
                        }
                    )
                }

                if (isLoading) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.5.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Dr. Rutherford ($selectedModel) is analyzing...",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Input Bar
            Surface(
                tonalElevation = 3.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("reaction_input_field"),
                        placeholder = { Text("Ask any reaction or concept...", fontSize = 14.sp) },
                        maxLines = 4,
                        shape = RoundedCornerShape(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    FilledIconButton(
                        onClick = { sendMessage(inputText) },
                        enabled = inputText.isNotBlank() && !isLoading,
                        modifier = Modifier.testTag("send_reaction_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                    }
                }
            }
        }
    }

    // Live Voice Lab Dialog (gemini-3.8-live)
    if (showLiveVoiceDialog) {
        LiveVoiceConversationDialog(
            geminiClient = geminiClient,
            ttsManager = ttsManager,
            onDismiss = {
                ttsManager.stop()
                showLiveVoiceDialog = false
            }
        )
    }
}

@Composable
fun LiveVoiceConversationDialog(
    geminiClient: GeminiApiClient,
    ttsManager: TtsManager,
    onDismiss: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var liveStatus by remember { mutableStateOf("Ready to speak") }
    var userPrompt by remember { mutableStateOf("") }
    var aiSpokenResponse by remember { mutableStateOf("") }
    var isLiveLoading by remember { mutableStateOf(false) }

    fun processVoicePrompt(prompt: String) {
        if (prompt.isBlank() || isLiveLoading) return
        isLiveLoading = true
        userPrompt = prompt
        liveStatus = "gemini-3.8-live is thinking..."
        coroutineScope.launch {
            val response = geminiClient.liveVoiceConversation(prompt)
            aiSpokenResponse = response
            liveStatus = "Dr. Rutherford Speaking..."
            isLiveLoading = false
            ttsManager.speak(response)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Mic, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("Live Voice Conversation", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Model: gemini-3.8-live", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Pulsating Audio Waveform Visualizer
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF0F172A),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val barHeights = listOf(24.dp, 48.dp, 80.dp, 60.dp, 96.dp, 70.dp, 40.dp, 20.dp)
                            barHeights.forEach { height ->
                                Box(
                                    modifier = Modifier
                                        .width(6.dp)
                                        .height(if (isLiveLoading) height else 16.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(if (isLiveLoading) Color(0xFF38BDF8) else Color(0xFF334155))
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = liveStatus,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                if (userPrompt.isNotBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "You: \"$userPrompt\"",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                }

                if (aiSpokenResponse.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = aiSpokenResponse,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(10.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Tap a voice question or type below:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )

                Spacer(modifier = Modifier.height(6.dp))
                val voiceSamples = listOf(
                    "Explain electrolysis of brine",
                    "Why does sodium react violently with water?",
                    "How to balance redox in basic solution?"
                )
                voiceSamples.forEach { sample ->
                    OutlinedButton(
                        onClick = { processVoicePrompt(sample) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.VolumeUp, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(sample, fontSize = 12.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close Voice Lab")
            }
        }
    )
}

@Composable
fun ChatBubble(
    message: UiChatMessage,
    isSpeaking: Boolean,
    onSpeak: (String) -> Unit,
    onSave: (String) -> Unit
) {
    val isUser = message.isUser
    var savedSuccess by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(32.dp)
                    .padding(top = 4.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            color = if (isUser) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            },
            modifier = Modifier.widthIn(max = 320.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                if (!isUser && message.modelTag.isNotBlank()) {
                    Text(
                        text = "Model: ${message.modelTag}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (!isUser && message.id != "welcome") {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { onSpeak(message.text) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (isSpeaking) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeMute,
                                contentDescription = "Read Aloud",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        IconButton(
                            onClick = {
                                onSave(message.text)
                                savedSuccess = true
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (savedSuccess) Icons.Default.BookmarkAdded else Icons.Default.BookmarkBorder,
                                contentDescription = "Bookmark",
                                tint = if (savedSuccess) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
