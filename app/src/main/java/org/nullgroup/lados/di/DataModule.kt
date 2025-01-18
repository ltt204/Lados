package org.nullgroup.lados.di

import android.app.Activity
import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.nullgroup.lados.data.repositories.implementations.cart.CartItemRepositoryImplement
import org.nullgroup.lados.data.repositories.implementations.category.CategoryRepositoryImplement
import org.nullgroup.lados.data.repositories.implementations.chat.ChatRepositoryImpl
import org.nullgroup.lados.data.repositories.implementations.order.OrderRepositoryImplement
import org.nullgroup.lados.data.repositories.implementations.product.ProductRepositoryImplement
import org.nullgroup.lados.data.repositories.implementations.common.SharedPreferencesImpl
import org.nullgroup.lados.data.repositories.implementations.common.ImageRepositoryImplement
import org.nullgroup.lados.data.repositories.implementations.product.ProductVariantRepository
import org.nullgroup.lados.data.repositories.implementations.product.ReviewProductRepositoryImplement
import org.nullgroup.lados.data.repositories.implementations.user.UserAddressRepositoryImplement
import org.nullgroup.lados.data.repositories.implementations.user.UserRepositoryImplement
import org.nullgroup.lados.data.repositories.implementations.wishlist.WishlistItemRepositoryImplement
import org.nullgroup.lados.data.repositories.interfaces.cart.CartItemRepository
import org.nullgroup.lados.data.repositories.interfaces.common.SharedPreferencesRepository

import org.nullgroup.lados.data.repositories.interfaces.category.CategoryRepository
import org.nullgroup.lados.data.repositories.interfaces.chat.ChatRepository

import org.nullgroup.lados.data.repositories.interfaces.user.IUserAddressRepository

import org.nullgroup.lados.data.repositories.interfaces.common.ImageRepository
import org.nullgroup.lados.data.repositories.interfaces.order.OrderRepository
import org.nullgroup.lados.data.repositories.interfaces.product.ProductRepository
import org.nullgroup.lados.data.repositories.interfaces.product.ReviewProductRepository
import org.nullgroup.lados.data.repositories.interfaces.user.UserRepository
import org.nullgroup.lados.data.repositories.interfaces.wishlist.WishlistItemRepository
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

    @Singleton
    @Provides
    fun provideChatRepository(
        database: FirebaseDatabase,
        auth: FirebaseAuth,
        storage: FirebaseStorage,
    ): ChatRepository {
        return ChatRepositoryImpl(database, auth, storage)
    }

    @Singleton
    @Provides
    fun provideProductVariantRepository(
        firestore: FirebaseFirestore,
        imageRepository: ImageRepository
    ): ProductVariantRepository {
        return ProductVariantRepository(
            firestore,
            imageRepository
        )
    }
}