package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ChemistryDao {

    // Quiz Results
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizResult(result: QuizResultEntity): Long

    @Query("SELECT * FROM quiz_results ORDER BY timestamp DESC")
    fun getAllQuizResults(): Flow<List<QuizResultEntity>>

    @Query("SELECT * FROM quiz_results ORDER BY timestamp DESC LIMIT 10")
    fun getRecentQuizResults(): Flow<List<QuizResultEntity>>

    @Query("SELECT COUNT(*) FROM quiz_results")
    fun getTotalQuizzesTaken(): Flow<Int>

    // Mastery Progress
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMastery(mastery: MasteryProgressEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMastery(list: List<MasteryProgressEntity>)

    @Query("SELECT * FROM mastery_progress")
    fun getAllMastery(): Flow<List<MasteryProgressEntity>>

    @Query("SELECT * FROM mastery_progress WHERE domainKey = :key LIMIT 1")
    suspend fun getMastery(key: String): MasteryProgressEntity?

    // Saved Reactions & Bookmarks
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedReaction(reaction: SavedReactionEntity): Long

    @Query("SELECT * FROM saved_reactions ORDER BY timestamp DESC")
    fun getAllSavedReactions(): Flow<List<SavedReactionEntity>>

    @Query("DELETE FROM saved_reactions WHERE id = :id")
    suspend fun deleteSavedReaction(id: Long)

    // Chat History
    @Insert
    suspend fun insertChatMessage(message: ChatMessageEntity): Long

    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllChatMessages(): Flow<List<ChatMessageEntity>>

    @Query("DELETE FROM chat_messages")
    suspend fun clearChatHistory()
}
