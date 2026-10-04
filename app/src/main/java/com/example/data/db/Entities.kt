package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz_results")
data class QuizResultEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val topicId: String,
    val topicTitle: String,
    val score: Int,
    val totalQuestions: Int,
    val percentage: Int,
    val timeTakenSeconds: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "mastery_progress")
data class MasteryProgressEntity(
    @PrimaryKey
    val domainKey: String, // FUNDAMENTALS, PERIODIC_TRENDS, REDOX_ELECTRO, ORGANIC, BIOCHEM
    val domainTitle: String,
    val masteryScore: Int, // 0 - 100
    val questionsAnswered: Int,
    val correctCount: Int,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "saved_reactions")
data class SavedReactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val equation: String,
    val category: String,
    val explanation: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sender: String, // "USER" or "AI"
    val content: String,
    val topicTag: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
