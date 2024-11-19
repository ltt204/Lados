package org.nullgroup.lados.data.repositories.implementations

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.jupiter.MockitoExtension
import org.nullgroup.lados.data.repositories.interfaces.UserRepository

@ExperimentalCoroutinesApi
@ExtendWith(MockitoExtension::class)
class UserRepositoryImplementTest {
    @Mock
    private lateinit var firestore: FirebaseFirestore

    @Mock
    private lateinit var firebaseAuth: FirebaseAuth

    @Mock
    private lateinit var authResult: Task<AuthResult>

    private lateinit var userRepository: UserRepository

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        userRepository = UserRepositoryImplement(firestore, firebaseAuth)
    }

    @Test
    fun login_LoginWithValidCredentials_ReturnsSuccess() = runTest {
        // Arrange
        val email = "admin@test.com"
        val password = "admin123"
        Mockito.`when`(authResult.isSuccessful).thenReturn(true)
        Mockito.`when`(firebaseAuth.signInWithEmailAndPassword(email, password))
            .thenReturn(authResult)

        // Act
        val result = userRepository.login(email, password)

        // Assert
        Assertions.assertTrue(result.isSuccess)
    }

    @Test
    fun signUp_signUpWithValidData_returnsSuccess() = runTest {
        // Arrange
        val fullName = "Test User"
        val email = "test@example.com"
        val password = "test123"
        Mockito.`when`(authResult.isSuccessful).thenReturn(true)
        Mockito.`when`(firebaseAuth.createUserWithEmailAndPassword(email, password))
            .thenReturn(authResult)

        // Act
        val result = userRepository.signUp(fullName, email, password)

        // Assert
        Assertions.assertTrue(result.isSuccess)
    }

    @After
    fun tearDown() = runTest {
        userRepository.deleteUser("test@test.com")
    }
}