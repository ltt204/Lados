package org.nullgroup.lados.di

import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.nullgroup.lados.data.repositories.implementations.CartItemRepositoryImplement
import org.nullgroup.lados.data.repositories.implementations.WishlistItemRepositoryImplement
import org.nullgroup.lados.data.repositories.interfaces.CartItemRepository
import org.nullgroup.lados.data.repositories.interfaces.WishlistItemRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CartAndWishlistModule {

    @Singleton
    @Provides
    fun provideCartRepository(firestore: FirebaseFirestore): CartItemRepository {
        return CartItemRepositoryImplement(firestore)
    }

    @Singleton
    @Provides
    fun provideWishlistRepository(firestore: FirebaseFirestore): WishlistItemRepository {
        return WishlistItemRepositoryImplement(firestore)
    }
}