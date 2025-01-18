package org.nullgroup.lados.data.repositories.implementations.category

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import org.nullgroup.lados.data.models.Category
import org.nullgroup.lados.data.remote.models.CategoryRemoteModel
import org.nullgroup.lados.data.repositories.interfaces.category.CategoryRepository
import org.nullgroup.lados.utilities.toLocalCategory

class CategoryRepositoryImplement(
    private val firestore: FirebaseFirestore
) : CategoryRepository {
    override suspend fun getAllCategoriesFromFireStore(): Result<List<Category>> {
        return try {
            val snapshots = firestore.collection("categories").get().await()
            val categoryList = if (snapshots.isEmpty) {
                emptyList()
            } else {
                snapshots.documents.mapNotNull { it.toObject(CategoryRemoteModel::class.java) }
                    .map { it.toLocalCategory() }
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

            val category = categoryDoc.toObject(CategoryRemoteModel::class.java)
            Result.success(category?.toLocalCategory())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllSortedAndFilteredCategoriesFromFireStore(
        filterField: String?,
        filterValue: Any?,
        sortByField: String,
        ascending: Boolean,
    ): Result<List<Category>> {
        return try {
            var query = firestore.collection("categories")

            // Apply filter if provided
            if (filterField != null && filterValue != null) {
                query = query.whereEqualTo(filterField, filterValue) as CollectionReference
            }

            // Apply sorting
            query = query.orderBy(sortByField, if (ascending) Query.Direction.ASCENDING else Query.Direction.ASCENDING) as CollectionReference

            // Retrieve data from Firestore
            val snapshots = query.get().await()

            val categoryList = snapshots.documents.mapNotNull { document ->
                document.toObject(CategoryRemoteModel::class.java)?.toLocalCategory()
            }

            Result.success(categoryList)
        } catch (e: FirebaseFirestoreException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}