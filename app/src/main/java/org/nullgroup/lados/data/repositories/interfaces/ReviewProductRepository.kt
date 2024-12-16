package org.nullgroup.lados.data.repositories.interfaces

import org.nullgroup.lados.data.models.UserEngagement

interface ReviewProductRepository {
    suspend fun sendReview(
        productId: String,
        engagement: UserEngagement
    ): Result<Boolean>
}
