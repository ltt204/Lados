package org.nullgroup.lados.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.nullgroup.lados.data.repositories.implementations.ImageRepositoryImplement
import org.nullgroup.lados.data.repositories.implementations.ProductRepositoryImplement
import org.nullgroup.lados.data.repositories.implementations.UserAddressRepository
import org.nullgroup.lados.data.repositories.implementations.UserRepositoryImplement
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
}