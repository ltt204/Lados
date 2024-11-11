package org.nullgroup.lados.data.repositories.implementations

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.scopes.ActivityScoped
import org.nullgroup.lados.data.models.User
import org.nullgroup.lados.data.repositories.interfaces.UserRepository
import javax.inject.Inject


class UserRepositoryImplement (
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : UserRepository {
    override suspend fun login(email: String, password: String): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun signUp(
        fullName: String,
        email: String,
        password: String
    ): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun saveUserToFirestore(user: User) {
        TODO("Not yet implemented")
    }

    override suspend fun getUserRole(email: String): Result<String> {
        TODO("Not yet implemented")
    }
}