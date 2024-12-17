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
import org.nullgroup.lados.data.repositories.implementations.CartItemRepositoryImplement
import org.nullgroup.lados.data.repositories.implementations.CategoryRepositoryImplement
import org.nullgroup.lados.data.repositories.implementations.OrderRepositoryImplement
import org.nullgroup.lados.data.repositories.implementations.ProductRepositoryImplement
import org.nullgroup.lados.data.repositories.implementations.SharedPreferencesImpl
import org.nullgroup.lados.data.repositories.implementations.ImageRepositoryImplement
import org.nullgroup.lados.data.repositories.implementations.ReviewProductRepositoryImplement
import org.nullgroup.lados.data.repositories.implementations.UserAddressRepositoryImplement
import org.nullgroup.lados.data.repositories.implementations.UserRepositoryImplement
import org.nullgroup.lados.data.repositories.implementations.WishlistItemRepositoryImplement
import org.nullgroup.lados.data.repositories.interfaces.CartItemRepository
import org.nullgroup.lados.data.repositories.interfaces.SharedPreferencesRepository

import org.nullgroup.lados.data.repositories.interfaces.CategoryRepository

import org.nullgroup.lados.data.repositories.interfaces.IUserAddressRepository

import org.nullgroup.lados.data.repositories.interfaces.ImageRepository
import org.nullgroup.lados.data.repositories.interfaces.OrderRepository
import org.nullgroup.lados.data.repositories.interfaces.ProductRepository
import org.nullgroup.lados.data.repositories.interfaces.ReviewProductRepository
import org.nullgroup.lados.data.repositories.interfaces.UserRepository
import org.nullgroup.lados.data.repositories.interfaces.WishlistItemRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Singleton
    @Provides
    fun provideUserRepository(
        firestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth,
        imageRepository: ImageRepository,
    ): UserRepository {
        return UserRepositoryImplement(firestore, firebaseAuth, imageRepository)
    }

    @Singleton
    @Provides
    fun provideProductRepository(
        firestore: FirebaseFirestore,
    ): ProductRepository {
        return ProductRepositoryImplement(firestore)
    }

    @Singleton
    @Provides
    fun provideUserAddressRepository(
        firestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth,
    ): IUserAddressRepository {
        return UserAddressRepositoryImplement(firestore, firebaseAuth)
    }

    @Singleton
    @Provides
    fun provideImageRepository(
        firebaseStorage: FirebaseStorage,
    ): ImageRepository {
        return ImageRepositoryImplement(firebaseStorage)
    }

    @Provides
    @Singleton
    fun provideSharedPreferencesRepository(
        @ApplicationContext context: Context,
    ): SharedPreferencesRepository {
        return SharedPreferencesImpl(context)
    }

    @Provides
    @Singleton
    fun provideActivity(
        @ActivityContext context: Context,
    ): Activity {
        return context as Activity
    }


    @Singleton
    @Provides
    fun provideCategoryRepository(
        firestore: FirebaseFirestore,
    ): CategoryRepository {
        return CategoryRepositoryImplement(firestore)
    }

    @Singleton
    @Provides
    fun provideOrderRepository(
        firestore: FirebaseFirestore,
        fireAuth: FirebaseAuth,
    ): OrderRepository {
        return OrderRepositoryImplement(firestore, fireAuth)
    }

    @Singleton
    @Provides
    fun provideReviewRepository(firestore: FirebaseFirestore): ReviewProductRepository {
        return ReviewProductRepositoryImplement(firestore)
    }

    @Singleton
    @Provides
    fun provideCartRepository(
        firestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth,
    ): CartItemRepository {
        return CartItemRepositoryImplement(firestore, firebaseAuth)
    }

    @Singleton
    @Provides
    fun provideWishlistRepository(firestore: FirebaseFirestore): WishlistItemRepository {
        return WishlistItemRepositoryImplement(firestore)
    }
}