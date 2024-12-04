package org.nullgroup.lados.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Singleton
    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore {
        val instance = FirebaseFirestore.getInstance()
        return instance
    }

    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth {
        val instance = FirebaseAuth.getInstance()
        return instance
    }

    @Singleton
    @Provides
    fun provideFirebaseStorage(): FirebaseStorage {
        val instance = FirebaseStorage.getInstance()
        return instance
    }
}
