package com.example.data.firebase

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

data class AuthState(
    val user: FirebaseUser? = null,
    val isLoading: Boolean = false,
    val isFirebaseInitialized: Boolean = false,
    val errorMessage: String? = null
)

class FirebaseAuthManager(private val context: Context) {

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private var firebaseAuth: FirebaseAuth? = null

    init {
        checkFirebaseInitialization()
    }

    private fun checkFirebaseInitialization() {
        try {
            val apps = FirebaseApp.getApps(context)
            if (apps.isNotEmpty()) {
                val auth = FirebaseAuth.getInstance()
                firebaseAuth = auth
                _authState.value = AuthState(
                    user = auth.currentUser,
                    isFirebaseInitialized = true
                )
                auth.addAuthStateListener { updatedAuth ->
                    _authState.value = _authState.value.copy(
                        user = updatedAuth.currentUser,
                        isFirebaseInitialized = true
                    )
                }
            } else {
                _authState.value = AuthState(
                    isFirebaseInitialized = false,
                    errorMessage = "Firebase is ready. Add google-services.json to sync with cloud."
                )
            }
        } catch (e: Exception) {
            Log.w("FirebaseAuthManager", "Firebase not yet configured: ${e.message}")
            _authState.value = AuthState(
                isFirebaseInitialized = false,
                errorMessage = e.message
            )
        }
    }

    suspend fun signInWithGoogle(webClientId: String = "") {
        _authState.value = _authState.value.copy(isLoading = true, errorMessage = null)
        try {
            val credentialManager = CredentialManager.create(context)

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(if (webClientId.isNotBlank()) webClientId else "placeholder-client-id.apps.googleusercontent.com")
                .setAutoSelectEnabled(true)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(context = context, request = request)
            val credential = result.credential

            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken

                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = firebaseAuth?.signInWithCredential(firebaseCredential)?.await()
                _authState.value = _authState.value.copy(
                    user = authResult?.user,
                    isLoading = false
                )
            } else {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    errorMessage = "Unsupported credential received"
                )
            }
        } catch (e: GetCredentialCancellationException) {
            _authState.value = _authState.value.copy(
                isLoading = false,
                errorMessage = "Sign-in was cancelled"
            )
        } catch (e: Exception) {
            Log.e("FirebaseAuthManager", "Sign-in error", e)
            _authState.value = _authState.value.copy(
                isLoading = false,
                errorMessage = e.message ?: "Authentication failed"
            )
        }
    }

    fun signOut() {
        firebaseAuth?.signOut()
        _authState.value = _authState.value.copy(user = null)
    }
}
