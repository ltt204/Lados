package org.nullgroup.lados.di

import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.nullgroup.lados.data.repositories.implementations.OrderRepositoryImplement
import org.nullgroup.lados.data.repositories.interfaces.OrderRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OrderModule {

    @Singleton
    @Provides
    fun provideOrderRepository(firestore: FirebaseFirestore): OrderRepository {
        return OrderRepositoryImplement(firestore)
    }
}
