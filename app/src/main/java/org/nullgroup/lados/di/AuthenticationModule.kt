package org.nullgroup.lados.di

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.nullgroup.lados.R
import org.nullgroup.lados.data.repositories.implementations.AuthRepositoryImpl
import org.nullgroup.lados.data.repositories.interfaces.AuthRepository
import org.nullgroup.lados.data.repositories.interfaces.SharedPreferencesRepository
import org.nullgroup.lados.data.repositories.interfaces.UserRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthenticationModule {

    @Provides
    @Singleton
    fun provideSignInClient(@ApplicationContext context: Context): SignInClient {
        return Identity.getSignInClient(context)
    }

    @Provides
    @Singleton
    fun provideGetSignInWithGoogleOption(@ApplicationContext context: Context): GetSignInWithGoogleOption {
        val gso = GetSignInWithGoogleOption
            .Builder(context.getString(R.string.default_web_client_id))
            .build()
        return gso
    }

    @Provides
    @Singleton
    fun provideGoogleIdOption(@ApplicationContext context: Context): GetGoogleIdOption =
        GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(context.getString(R.string.default_web_client_id))
            .setAutoSelectEnabled(false)
            .build()

    @Provides
    @Singleton
    fun provideCredentialManager(@ApplicationContext context: Context): CredentialManager =
        CredentialManager.create(context)

    @Provides
    @Singleton
    fun provideCredentialRequest(googleIdOption: GetSignInWithGoogleOption): GetCredentialRequest =
        GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

    @Provides
    @Singleton
    fun provideAuthRepository(
        firestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth,
        userRepository: UserRepository,
        sharedPreferences: SharedPreferencesRepository,
        credentialManager: CredentialManager,
        credentialRequest: GetCredentialRequest,
    ): AuthRepository {
        return AuthRepositoryImpl(
            firestore = firestore,
            firebaseAuth = firebaseAuth,
            userRepository = userRepository,
            sharedPreferences = sharedPreferences,
            credentialManager = credentialManager,
            credentialRequest = credentialRequest,
        )
    }
}