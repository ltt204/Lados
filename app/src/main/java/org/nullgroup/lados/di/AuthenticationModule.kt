package org.nullgroup.lados.di

import android.content.Context
import com.facebook.CallbackManager
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.nullgroup.lados.R
import org.nullgroup.lados.data.repositories.implementations.EmailAuthRepositoryImpl
import org.nullgroup.lados.data.repositories.implementations.FacebookAuthRepositoryImpl
import org.nullgroup.lados.data.repositories.implementations.GoogleAuthRepositoryImpl
import org.nullgroup.lados.data.repositories.implementations.SharedPreferencesImpl
import org.nullgroup.lados.data.repositories.interfaces.EmailAuthRepository
import org.nullgroup.lados.data.repositories.interfaces.FacebookAuthRepository
import org.nullgroup.lados.data.repositories.interfaces.GoogleAuthRepository
import org.nullgroup.lados.data.repositories.interfaces.SharedPreferencesRepository
import org.nullgroup.lados.data.repositories.interfaces.UserRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthenticationModule {

    @Provides
    @Singleton
    fun provideEmailAuthRepository(
        firestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth,
        userRepository: UserRepository,
        sharedPreferences: SharedPreferencesRepository,
    ): EmailAuthRepository {
        return EmailAuthRepositoryImpl(
            firebaseAuth,
            firestore,
            userRepository,
            sharedPreferences,
        )
    }

    @Provides
    @Singleton
    fun provideGoogleAuthRepository(
        @ApplicationContext context: Context,
        oneTapClient: SignInClient,
        firebaseAuth: FirebaseAuth,
        googleSignInClient: GoogleSignInClient,
        userRepository: UserRepository,
        sharedPreferences: SharedPreferencesRepository,
    ): GoogleAuthRepository {
        return GoogleAuthRepositoryImpl(
            context,
            oneTapClient,
            firebaseAuth,
            googleSignInClient,
            userRepository,
            sharedPreferences,
        )
    }

    @Provides
    @Singleton
    fun provideFacebookAuthRepository(
        firebaseAuth: FirebaseAuth,
        loginManager: LoginManager,
        callbackManager: CallbackManager,
        userRepository: UserRepository,
        sharedPreferences: SharedPreferencesRepository,
    ): FacebookAuthRepository {
        return FacebookAuthRepositoryImpl(
            auth = firebaseAuth,
            loginManager = loginManager,
            callbackManager = callbackManager,
            userRepository = userRepository,
            sharedPreferences = sharedPreferences,
        )
    }

    @Provides
    @Singleton
    fun provideSignInClient(@ApplicationContext context: Context): SignInClient {
        return Identity.getSignInClient(context)
    }

    @Provides
    @Singleton
    fun provideGoogleSignInClient(@ApplicationContext context: Context): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, gso)
    }

    @Provides
    @Singleton
    fun provideFacebookLoginManager(): LoginManager {
        return LoginManager.getInstance()
    }

    @Provides
    @Singleton
    fun provideCallbackManager(): CallbackManager {
        return CallbackManager.Factory.create()
    }
}