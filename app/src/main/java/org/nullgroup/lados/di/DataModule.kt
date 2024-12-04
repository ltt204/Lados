package org.nullgroup.lados.di

import android.app.Activity
import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.nullgroup.lados.data.repositories.implementations.ProductRepositoryImplement
import org.nullgroup.lados.data.repositories.implementations.SharedPreferencesImpl
import org.nullgroup.lados.data.repositories.implementations.ImageRepositoryImplement
import org.nullgroup.lados.data.repositories.implementations.UserAddressRepository
import org.nullgroup.lados.data.repositories.implementations.UserRepositoryImplement
import org.nullgroup.lados.data.repositories.interfaces.SharedPreferencesRepository
import org.nullgroup.lados.data.repositories.interfaces.IUserAddressRepository
import org.nullgroup.lados.data.repositories.interfaces.ImageRepository
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
        firebaseAuth: FirebaseAuth,
        imageRepository: ImageRepository
    ): UserRepository {
        return UserRepositoryImplement(firestore, firebaseAuth, imageRepository)
    }

    @Singleton
    @Provides
    fun provideProductRepository(
        firestore: FirebaseFirestore
    ): ProductRepository {
        return ProductRepositoryImplement(firestore)
    }

    @Singleton
    @Provides
    fun provideUserAddressRepository(
        firestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth
    ): IUserAddressRepository {
        return UserAddressRepository(firestore, firebaseAuth)
    }

    @Singleton
    @Provides
    fun provideImageRepository(
        firebaseStorage: FirebaseStorage
    ): ImageRepository {
        return ImageRepositoryImplement(firebaseStorage)
    }

    @Provides
    @Singleton
    fun provideSharedPreferencesRepository(
        @ApplicationContext context: Context
    ) : SharedPreferencesRepository {
        return SharedPreferencesImpl(context)
    }

    @Provides
    @Singleton
    fun provideActivity(
        @ActivityContext context: Context
    ) : Activity {
        return context as Activity
    }

}