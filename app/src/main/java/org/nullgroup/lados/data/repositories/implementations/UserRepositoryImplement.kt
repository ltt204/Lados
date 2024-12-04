package org.nullgroup.lados.data.repositories.implementations

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import org.nullgroup.lados.data.models.User
import org.nullgroup.lados.data.repositories.interfaces.ImageRepository
import org.nullgroup.lados.data.repositories.interfaces.UserRepository


class UserRepositoryImplement(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val imageRepository: ImageRepository,
) : UserRepository {

    override fun getCurrentUserFlow(): Flow<User> = callbackFlow {
        val userRef = firestore.collection("users").document(firebaseAuth.currentUser?.uid!!)


        val subscription = userRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val user = snapshot.toObject(User::class.java) ?: User()
                trySend(user).isSuccess
            }
        }

        awaitClose { subscription.remove() }
    }

    override suspend fun addUserToFirestore(user: User) {
        firestore.collection("users").add(user).await()
    }

    override suspend fun saveUserToFirestore(user: User) {
        user.photoUrl =
            user.photoUrl.ifEmpty {
                imageRepository.getImageUrl(
                    "users",
                    "default_avatar",
                    "jpg"
                )
            }

        firestore.collection("users").document(user.id).set(user).await()
    }

    override suspend fun deleteUser(id: String): Result<Boolean> {
        return try {
            firestore.collection("users").document(id).delete().await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }

    }

    override suspend fun updateUser(user: User): Result<Boolean> {
        return try {
            user.photoUrl =
                user.photoUrl.ifEmpty {
                    imageRepository.getImageUrl(
                        "users",
                        "default_avatar",
                        "jpg"
                    )
                }
            firestore.collection("users").document(user.id).set(user).await()
            Result.success(true)
        } catch (e: Exception) {
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


    override suspend fun getCurrentUser(): User {
        val firebaseUser = firebaseAuth.currentUser
        val userRef = firestore.collection("users").document(firebaseUser?.uid!!)

        val user = userRef.get().await().toObject(User::class.java) ?: User()

        Log.d("UserRepositoryImplement", "User profile picture: ${user.photoUrl}")
        return user

    }

    override suspend fun getUserFromFirestore(id: String): Result<User> {
        return try {
            val user =
                firestore.collection("users").document(id).get().await()
                    .toObject(User::class.java)
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

    override suspend fun getUserRole(id: String): Result<String> {
        return try {
            Log.d("UserRepositoryImplement", "Starting getUserRole for email: $id")

            // Add timeout
            withTimeout(5000L) {
                // Check if document exists first
                val docRef = firestore.collection("users").document(id)
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

    override suspend fun logout(): Result<Boolean> {
        return try {
            firebaseAuth.signOut()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}
