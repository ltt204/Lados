package org.nullgroup.lados.data.repositories.implementations

import android.util.Log
import androidx.compose.ui.geometry.isEmpty
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.tasks.await
import org.nullgroup.lados.data.models.Category
import org.nullgroup.lados.data.repositories.interfaces.CategoryRepository

class CategoryRepositoryImplement(
    private val firestore: FirebaseFirestore
) : CategoryRepository {

    override suspend fun getAllCategoriesFromFireStore(): Result<List<Category>> {
        return try {
            val snapshots = firestore.collection("categories").get().await()
            val categoryList = if (snapshots.isEmpty) {
                emptyList()
            } else {
                snapshots.documents.mapNotNull { it.toObject(Category::class.java) }
            }
            Result.success(categoryList)
        } catch (e: FirebaseFirestoreException) {
            Result.failure(e)
        }
    }

    override suspend fun getCategoryByIdFromFireStore(id: String): Result<Category?> {
        return try {
            val categoryDoc = firestore.collection("categories")
                .document(id)
                .get()
                .await()

            val category = categoryDoc.toObject(Category::class.java)
            Result.success(category)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}