package org.nullgroup.lados.data.repositories.implementations

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import org.nullgroup.lados.data.models.User
import org.nullgroup.lados.data.models.UserRole
import org.nullgroup.lados.data.repositories.interfaces.EmailAuthRepository
import org.nullgroup.lados.data.repositories.interfaces.SharedPreferencesRepository
import org.nullgroup.lados.data.repositories.interfaces.UserRepository
import org.nullgroup.lados.viewmodels.states.ResourceState


class EmailAuthRepositoryImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val userRepository: UserRepository,
    private val sharedPreferences: SharedPreferencesRepository,
) : EmailAuthRepository {

    override suspend fun signIn(email: String, password: String): ResourceState<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = userRepository.getUserFromFirestore(result.user?.uid ?: "").getOrNull()

            if (user == null) {
                return ResourceState.Error("User not found")
            }

            ResourceState.Success(user)
        } catch (e: Exception) {
            ResourceState.Error(e.message)
        }
    }

    override suspend fun signUp(
        fullName: String,
        email: String,
        password: String,
    ): ResourceState<User> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            if (result.user == null) {
                return ResourceState.Error("Failed to create user")
            }
            val user = User(
                id = result.user?.uid ?: "",
                email = email,
                name = fullName,
                role = UserRole.CUSTOMER.name,
                phoneNumber = result.user?.phoneNumber ?: "",
                photoUrl = result.user?.photoUrl.toString(),
                provider = result.user?.providerId ?: "",
            )
            val token = result.user?.getIdToken(true)?.await()?.token
            if (token != null) {
                sharedPreferences.saveData(result.user?.providerId!!, token)
            }

            userRepository.saveUserToFirestore(user)
            ResourceState.Success(user)
        } catch (e: Exception) {
            ResourceState.Error(e.message)
        }
    }

    override suspend fun signOut(): ResourceState<Boolean> {
        return try {
            auth.signOut()
            ResourceState.Success(true)
        } catch (e: Exception) {
            ResourceState.Error(e.message)
        }
    }

    override suspend fun resetPassword(email: String): ResourceState<Boolean> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            ResourceState.Success(true)
        } catch (e: Exception) {
            ResourceState.Error(e.message)
        }
    }
}