package org.nullgroup.lados.di

import android.app.Activity
import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.components.SingletonComponent
import org.nullgroup.lados.data.repositories.implementations.ProductRepositoryImplement
import org.nullgroup.lados.data.repositories.implementations.UserRepositoryImplement
import org.nullgroup.lados.data.repositories.interfaces.ProductRepository
import org.nullgroup.lados.data.repositories.interfaces.UserRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Singleton
    @Provides
    fun provideUserRepository(
        firestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth
    ): UserRepository {
        return UserRepositoryImplement(firestore, firebaseAuth)
    }

    @Singleton
    @Provides
    fun provideProductRepository(
        firestore: FirebaseFirestore
    ): ProductRepository {
        return ProductRepositoryImplement(firestore)
    }

    @Provides
    @Singleton
    fun provideActivity(
        @ActivityContext context: Context
    ) : Activity {
        return context as Activity
    }
}