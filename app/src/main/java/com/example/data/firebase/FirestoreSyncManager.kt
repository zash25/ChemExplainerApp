package com.example.data.firebase

import android.content.Context
import android.util.Log
import com.example.data.db.MasteryProgressEntity
import com.example.data.db.QuizResultEntity
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class FirestoreSyncManager(private val context: Context) {

    private var firestore: FirebaseFirestore? = null

    init {
        try {
            if (FirebaseApp.getApps(context).isNotEmpty()) {
                firestore = FirebaseFirestore.getInstance()
            }
        } catch (e: Exception) {
            Log.w("FirestoreSyncManager", "Firestore initialization: ${e.message}")
        }
    }

    suspend fun syncQuizResult(userId: String, result: QuizResultEntity) {
        val db = firestore ?: return
        try {
            val data = hashMapOf(
                "topicId" to result.topicId,
                "topicTitle" to result.topicTitle,
                "score" to result.score,
                "totalQuestions" to result.totalQuestions,
                "percentage" to result.percentage,
                "timeTakenSeconds" to result.timeTakenSeconds,
                "timestamp" to result.timestamp
            )
            db.collection("users")
                .document(userId)
                .collection("quiz_results")
                .add(data)
                .await()
        } catch (e: Exception) {
            Log.e("FirestoreSyncManager", "Error syncing quiz to Firestore", e)
        }
    }

    suspend fun syncMastery(userId: String, mastery: MasteryProgressEntity) {
        val db = firestore ?: return
        try {
            val data = hashMapOf(
                "domainKey" to mastery.domainKey,
                "domainTitle" to mastery.domainTitle,
                "masteryScore" to mastery.masteryScore,
                "questionsAnswered" to mastery.questionsAnswered,
                "correctCount" to mastery.correctCount,
                "lastUpdated" to mastery.lastUpdated
            )
            db.collection("users")
                .document(userId)
                .collection("mastery")
                .document(mastery.domainKey)
                .set(data, SetOptions.merge())
                .await()
        } catch (e: Exception) {
            Log.e("FirestoreSyncManager", "Error syncing mastery to Firestore", e)
        }
    }
}
