package org.nullgroup.lados.data.repositories.implementations.product

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import org.nullgroup.lados.data.models.UserEngagement
import org.nullgroup.lados.data.repositories.interfaces.product.ReviewProductRepository

class ReviewProductRepositoryImplement(
    private val firestore: FirebaseFirestore
) : ReviewProductRepository {

    override suspend fun sendReview(
        productId: String,
        engagement: UserEngagement): Result<Boolean> {
        return try {

            val productDocRef = firestore.collection("products").document(productId)

            val engagementData = hashMapOf(
                "userId" to engagement.userId,
                "productId" to productId,
                "ratings" to engagement.ratings,
                "reviews" to engagement.reviews,
                "createdAt" to engagement.createdAt
            )

            productDocRef.collection("engagements").add(engagementData).await()

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
