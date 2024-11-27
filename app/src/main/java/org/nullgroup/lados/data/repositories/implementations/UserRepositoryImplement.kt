package org.nullgroup.lados.data.repositories.implementations

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import org.nullgroup.lados.data.models.User
import org.nullgroup.lados.data.repositories.interfaces.UserRepository


class UserRepositoryImplement (
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : UserRepository {
    override suspend fun login(email: String, password: String): Result<Boolean> {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signUp(
        fullName: String,
        email: String,
        password: String
    ): Result<Boolean> {
        return try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveUserToFirestore(user: User) {
        firestore.collection("users").document(user.email).set(user).await()
    }

    override suspend fun getUserFromFirestore(email: String): Result<User> {
        return try {
            val user = firestore.collection("users").document(email).get().await().toObject(User::class.java)
            Result.success(user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllUsersFromFirestore(): Result<List<User>> {
        return try {
            val users = firestore.collection("users").get().await().toObjects(User::class.java)
            Result.success(users)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserRole(email: String): Result<String> {
        return try {
            Log.d("UserRepositoryImplement", "Starting getUserRole for email: $email")

            // Add timeout
            withTimeout(5000L) {
                // Check if document exists first
                val docRef = firestore.collection("users").document(email)
                val snapshot = docRef.get().await()

                if (!snapshot.exists()) {
                    Log.d("UserRepositoryImplement", "Document does not exist")
                    return@withTimeout Result.failure(Exception("User not found"))
                }

                val user = snapshot.toObject(User::class.java)
                Log.d("UserRepositoryImplement", "User role: ${user?.role ?: "null"}")

                user?.role?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("User data is null"))
            }
        } catch (e: TimeoutCancellationException) {
            Log.e("UserRepositoryImplement", "Timeout getting user role", e)
            Result.failure(e)
        } catch (e: Exception) {
            Log.e("UserRepositoryImplement", "Error getting user role", e)
            Result.failure(e)
        }
    }

    override suspend fun updateUserRole(email: String, role: String): Result<Boolean> {
        return try {
            firestore.collection("users").document(email).update("role", role).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteUser(email: String): Result<Boolean> {
        return try {
            firestore.collection("users").document(email).delete().await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}